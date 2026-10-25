import { CommonModule } from '@angular/common';
import { ChangeDetectorRef, Component, OnInit, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { catchError, finalize, forkJoin, of } from 'rxjs';

import {
  AbastecimientoCombustible,
  ConfirmarSolicitudAbastecimiento,
  SolicitudAbastecimiento
} from '../../core/models/combustible.model';
import {
  ConductorOperacion,
  Programacion as ProgramacionModel,
  TractoOperacion
} from '../../core/models/programacion.model';
import { CombustibleService } from '../../core/services/combustible.service';
import { ProgramacionService } from '../../core/services/programacion.service';

@Component({
  selector: 'app-combustible',
  imports: [CommonModule, FormsModule],
  templateUrl: './combustible.html',
  styleUrl: './combustible.scss'
})
export class Combustible implements OnInit {
  private readonly combustibleService = inject(CombustibleService);
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
  abastecimientos: AbastecimientoCombustible[] = [];
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

  cargarDatos(): void {
    this.cargando = true;
    this.error = '';

    forkJoin({
      solicitudes: this.combustibleService.listarSolicitudes().pipe(
        catchError(() => of([] as SolicitudAbastecimiento[]))
      ),
      abastecimientos: this.combustibleService.listar().pipe(
        catchError(() => of([] as AbastecimientoCombustible[]))
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
          this.abastecimientos = [...data.abastecimientos].sort((a, b) => b.id - a.id);
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
          this.mensaje = `Solicitud #SOL-${this.codigo(actualizada.id)} confirmada. Se generó la notificación al conductor.`;
          this.solicitudSeleccionada = null;
          this.formularioConfirmacion = this.confirmacionVacia();
        },
        error: error => {
          this.error = this.mensajeError(error, 'No se pudo confirmar la solicitud.');
        }
      });
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
