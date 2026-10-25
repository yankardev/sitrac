import { CommonModule } from '@angular/common';
import { ChangeDetectorRef, Component, OnInit, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { catchError, finalize, forkJoin, of } from 'rxjs';

import {
  ConfirmarSolicitudAbastecimiento,
  SolicitudAbastecimiento
} from '../../core/models/combustible.model';
import { RegistroNotificacion } from '../../core/models/notificacion.model';
import {
  ConductorOperacion,
  Programacion as ProgramacionModel,
  TractoOperacion
} from '../../core/models/programacion.model';
import { CombustibleService } from '../../core/services/combustible.service';
import { NotificacionService } from '../../core/services/notificacion.service';
import { ProgramacionService } from '../../core/services/programacion.service';

@Component({
  selector: 'app-combustible',
  imports: [CommonModule, FormsModule],
  templateUrl: './combustible.html',
  styleUrl: './combustible.scss'
})
export class Combustible implements OnInit {
  private readonly combustibleService = inject(CombustibleService);
  private readonly notificacionService = inject(NotificacionService);
  private readonly programacionService = inject(ProgramacionService);
  private readonly cdr = inject(ChangeDetectorRef);

  readonly puntosAbastecimiento = [
    'Base Trujillo - Tanque 01',
    'Base Pacasmayo - Tanque 01',
    'Base Chiclayo - Tanque 01',
    'Base Piura - Tanque 01',
    'Grifo autorizado - Primax',
    'Grifo autorizado - Repsol',
    'Grifo autorizado - Petroperú'
  ];

  solicitudes: SolicitudAbastecimiento[] = [];
  notificaciones: RegistroNotificacion[] = [];
  programaciones: ProgramacionModel[] = [];
  conductores: ConductorOperacion[] = [];
  tractos: TractoOperacion[] = [];

  cargando = false;
  confirmando = false;
  solicitudSeleccionada: SolicitudAbastecimiento | null = null;
  mensaje = '';
  error = '';

  formularioConfirmacion: ConfirmarSolicitudAbastecimiento = this.confirmacionVacia();

  ngOnInit(): void {
    this.cargarDatos();
  }

  get solicitudesPendientes(): SolicitudAbastecimiento[] {
    return this.solicitudes.filter(item => item.estado === 'PENDIENTE');
  }

  get solicitudesConfirmadas(): SolicitudAbastecimiento[] {
    return this.solicitudes.filter(item => item.estado === 'CONFIRMADA');
  }

  get totalGalonesConfirmados(): number {
    return this.solicitudesConfirmadas.reduce(
      (total, item) => total + Number(item.cantidadGalones || 0),
      0
    );
  }

  get totalNotificacionesConductor(): number {
    return this.notificaciones.filter(item => item.tipoEvento === 'ABASTECIMIENTO_CONFIRMADO').length;
  }

  cargarDatos(): void {
    this.cargando = true;
    this.error = '';

    forkJoin({
      solicitudes: this.combustibleService.listarSolicitudes().pipe(
        catchError(() => of([] as SolicitudAbastecimiento[]))
      ),
      notificaciones: this.notificacionService.listar().pipe(
        catchError(() => of([] as RegistroNotificacion[]))
      ),
      programaciones: this.programacionService.listar().pipe(
        catchError(() => of([] as ProgramacionModel[]))
      ),
      conductores: this.programacionService.listarConductores().pipe(
        catchError(() => of([] as ConductorOperacion[]))
      ),
      tractos: this.programacionService.listarTractos().pipe(
        catchError(() => of([] as TractoOperacion[]))
      )
    })
      .pipe(finalize(() => {
        this.cargando = false;
        this.cdr.detectChanges();
      }))
      .subscribe({
        next: data => {
          this.solicitudes = [...data.solicitudes].sort((a, b) => b.id - a.id);
          this.notificaciones = [...data.notificaciones];
          this.programaciones = data.programaciones;
          this.conductores = data.conductores;
          this.tractos = data.tractos;
        },
        error: () => {
          this.error = 'No se pudo cargar el módulo de combustible.';
        }
      });
  }

  abrirConfirmacion(solicitud: SolicitudAbastecimiento): void {
    this.solicitudSeleccionada = solicitud;
    this.formularioConfirmacion = this.confirmacionVacia();
    this.mensaje = '';
    this.error = '';
  }

  cerrarConfirmacion(): void {
    if (this.confirmando) return;
    this.solicitudSeleccionada = null;
    this.formularioConfirmacion = this.confirmacionVacia();
  }

  confirmarSolicitud(): void {
    if (!this.solicitudSeleccionada) return;

    if (!this.formularioConfirmacion.puntoAbastecimiento.trim()) {
      this.error = 'Selecciona el punto de abastecimiento.';
      return;
    }

    if (!this.formularioConfirmacion.cantidadGalones || this.formularioConfirmacion.cantidadGalones <= 0) {
      this.error = 'Indica una cantidad de galones mayor que cero.';
      return;
    }

    this.confirmando = true;
    this.error = '';

    this.combustibleService.confirmarSolicitud(
      this.solicitudSeleccionada.id,
      {
        puntoAbastecimiento: this.formularioConfirmacion.puntoAbastecimiento.trim(),
        cantidadGalones: Number(this.formularioConfirmacion.cantidadGalones),
        observacion: this.formularioConfirmacion.observacion.trim()
      }
    )
      .pipe(finalize(() => {
        this.confirmando = false;
        this.cdr.detectChanges();
      }))
      .subscribe({
        next: actualizada => {
          this.solicitudes = this.solicitudes.map(item =>
            item.id === actualizada.id ? actualizada : item
          );
          this.mensaje = `Solicitud #SOL-${this.codigo(actualizada.id)} confirmada. RabbitMQ envió el evento para notificar al conductor.`;
          this.solicitudSeleccionada = null;
          this.formularioConfirmacion = this.confirmacionVacia();

          // La notificación se procesa de forma asíncrona. Se refresca la evidencia
          // unos instantes después para que el usuario vea el resultado sin salir del módulo.
          window.setTimeout(() => this.cargarNotificaciones(), 900);
        },
        error: error => {
          this.error = this.mensajeError(error, 'No se pudo confirmar la solicitud.');
        }
      });
  }

  notificacionDeSolicitud(solicitud: SolicitudAbastecimiento): RegistroNotificacion | undefined {
    const referenciaSolicitud = `SOL-${solicitud.id}`;
    const referenciaProgramacion = `PROG-${solicitud.programacionId}`;

    return this.notificaciones.find(item =>
      item.tipoEvento === 'ABASTECIMIENTO_CONFIRMADO' &&
      (item.referencia?.includes(referenciaSolicitud) || item.referencia?.includes(referenciaProgramacion))
    );
  }

  etiquetaNotificacion(solicitud: SolicitudAbastecimiento): string {
    const registro = this.notificacionDeSolicitud(solicitud);
    if (!registro) return 'Procesando / sin evidencia';

    switch (registro.estado) {
      case 'ENVIADA': return 'Correo enviado';
      case 'SIMULADA': return 'Generada (modo demo)';
      case 'OMITIDA': return 'Sin correo registrado';
      case 'ERROR': return 'Error de notificación';
    }
  }

  claseNotificacion(solicitud: SolicitudAbastecimiento): string {
    return this.notificacionDeSolicitud(solicitud)?.estado?.toLowerCase() || 'pendiente';
  }

  detalleNotificacion(solicitud: SolicitudAbastecimiento): string {
    const registro = this.notificacionDeSolicitud(solicitud);
    if (!registro) {
      return 'La confirmación existe, pero aún no hay evidencia registrada por notification-service.';
    }
    if (registro.estado === 'ENVIADA') {
      return `Enviado a ${registro.destinatario}`;
    }
    if (registro.estado === 'SIMULADA') {
      return `Contenido generado para ${registro.destinatario}; SMTP desactivado en demo.`;
    }
    if (registro.estado === 'OMITIDA') {
      return 'No se pudo enviar porque el conductor no tiene correo registrado.';
    }
    return registro.detalle || 'La notificación presentó un error.';
  }

  codigo(id: number): string {
    return id.toString().padStart(4, '0');
  }

  codigoProgramacion(id: number): string {
    return `PROG-${this.codigo(id)}`;
  }

  conductorNombre(id: number): string {
    const conductor = this.conductores.find(item => item.id === id);
    return conductor ? `${conductor.nombres} ${conductor.apellidos}` : `Conductor #${id}`;
  }

  tractoPlaca(id: number): string {
    return this.tractos.find(item => item.id === id)?.placa ?? `Tracto #${id}`;
  }

  rutaProgramacion(programacionId: number): string {
    const programacion = this.programaciones.find(item => item.id === programacionId);
    return programacion ? `Pedido #${programacion.pedidoId}` : 'Programación registrada';
  }

  private cargarNotificaciones(): void {
    this.notificacionService.listar().subscribe({
      next: registros => {
        this.notificaciones = [...registros];
        this.cdr.detectChanges();
      }
    });
  }

  private confirmacionVacia(): ConfirmarSolicitudAbastecimiento {
    return {
      puntoAbastecimiento: '',
      cantidadGalones: 0,
      observacion: ''
    };
  }

  private mensajeError(error: any, fallback: string): string {
    const respuesta = error?.error;
    if (typeof respuesta === 'string' && respuesta.trim()) return respuesta;
    if (respuesta?.error) return respuesta.error;
    if (respuesta?.message) return respuesta.message;
    if (respuesta?.campos) {
      const primerMensaje = Object.values(respuesta.campos)[0];
      if (typeof primerMensaje === 'string') return primerMensaje;
    }
    return fallback;
  }
}
