import { CommonModule } from '@angular/common';
import { ChangeDetectorRef, Component, OnInit, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { finalize, forkJoin } from 'rxjs';

import {
  AbastecimientoCombustible,
  AbastecimientoForm,
  TipoAbastecimiento
} from '../../core/models/combustible.model';
import {
  ConductorOperacion,
  Programacion as ProgramacionModel,
  TractoOperacion
} from '../../core/models/programacion.model';
import { Viaje } from '../../core/models/viaje.model';
import { CombustibleService } from '../../core/services/combustible.service';
import { ProgramacionService } from '../../core/services/programacion.service';
import { ViajeService } from '../../core/services/viaje.service';
import { confirmarAccionDestructiva } from '../../core/utils/confirmacion.util';

@Component({
  selector: 'app-combustible',
  imports: [CommonModule, FormsModule],
  templateUrl: './combustible.html',
  styleUrl: './combustible.scss'
})
export class Combustible implements OnInit {
  private readonly combustibleService = inject(CombustibleService);
  private readonly programacionService = inject(ProgramacionService);
  private readonly viajeService = inject(ViajeService);
  private readonly cdr = inject(ChangeDetectorRef);

  readonly tanquesBase: string[] = [
    'Tanque Base Trujillo 01',
    'Tanque Base Lima 01',
    'Tanque Base Cajamarca 01',
    'Tanque Base Pacasmayo 01',
    'Tanque Base Chiclayo 01',
    'Tanque Base Piura 01',
    'Tanque Base Chimbote 01'
  ];

  readonly proveedoresCombustible: string[] = [
    'Primax',
    'Repsol',
    'Petroperú',
    'Pecsa'
  ];

  abastecimientos: AbastecimientoCombustible[] = [];
  programaciones: ProgramacionModel[] = [];
  conductores: ConductorOperacion[] = [];
  tractos: TractoOperacion[] = [];
  viajes: Viaje[] = [];

  busqueda = '';
  filtroTipo: TipoAbastecimiento | 'TODOS' = 'TODOS';
  cargando = false;
  guardando = false;
  mostrarFormulario = false;
  abastecimientoEditandoId: number | null = null;
  mensaje = '';
  error = '';

  formulario: AbastecimientoForm = this.formularioVacio();

  ngOnInit(): void {
    this.cargarDatos();
  }

  get abastecimientosFiltrados(): AbastecimientoCombustible[] {
    const termino = this.busqueda.trim().toLowerCase();

    return this.abastecimientos.filter(item => {
      if (this.filtroTipo !== 'TODOS' && item.tipoAbastecimiento !== this.filtroTipo) {
        return false;
      }

      if (!termino) {
        return true;
      }

      return (
        `ab-${item.id}`.includes(termino) ||
        this.codigoProgramacion(item.programacionId).toLowerCase().includes(termino) ||
        this.conductorNombre(item.conductorId).toLowerCase().includes(termino) ||
        this.tractoPlaca(item.tractoId).toLowerCase().includes(termino) ||
        (item.proveedor ?? '').toLowerCase().includes(termino) ||
        (item.numeroComprobante ?? '').toLowerCase().includes(termino)
      );
    });
  }

  get totalGalones(): number {
    return this.abastecimientos.reduce((total, item) => total + Number(item.cantidadGalones || 0), 0);
  }

  get costoTotal(): number {
    return this.abastecimientos.reduce((total, item) => total + Number(item.costoTotal || 0), 0);
  }

  get totalInternos(): number {
    return this.abastecimientos.filter(item => item.tipoAbastecimiento === 'INTERNO').length;
  }

  get totalTerceros(): number {
    return this.abastecimientos.filter(item => item.tipoAbastecimiento === 'TERCERO').length;
  }

  get programacionesDisponibles(): ProgramacionModel[] {
    return this.programaciones.filter(item => item.estado === 'PROGRAMADA');
  }

  get viajesDeProgramacion(): Viaje[] {
    if (this.formulario.programacionId === null) {
      return [];
    }

    return this.viajes.filter(
      viaje => viaje.programacionId === Number(this.formulario.programacionId)
    );
  }

  cargarDatos(): void {
    const primeraCarga = this.abastecimientos.length === 0;
    if (primeraCarga) {
      this.cargando = true;
    }

    this.error = '';

    this.combustibleService.listar().subscribe({
      next: abastecimientos => {
        this.abastecimientos = [...abastecimientos].sort((a, b) => b.id - a.id);
        this.cargando = false;
        this.cdr.detectChanges();
      },
      error: error => {
        this.error = this.obtenerMensajeError(
          error,
          'No se pudo cargar Combustible. Verifica combustible-service.'
        );
        this.cargando = false;
        this.cdr.detectChanges();
      }
    });

    this.cargarCatalogosEnSegundoPlano();
  }

  abrirNuevo(): void {
    this.abastecimientoEditandoId = null;
    this.formulario = this.formularioVacio();
    this.mensaje = '';
    this.error = '';
    this.mostrarFormulario = true;
  }

  editar(item: AbastecimientoCombustible): void {
    this.abastecimientoEditandoId = item.id;
    this.formulario = {
      programacionId: item.programacionId,
      viajeId: item.viajeId,
      conductorId: item.conductorId,
      tractoId: item.tractoId,
      tipoAbastecimiento: item.tipoAbastecimiento,
      fechaHora: item.fechaHora.slice(0, 16),
      cantidadGalones: item.cantidadGalones,
      kilometraje: item.kilometraje,
      precioUnitario: item.precioUnitario,
      tanqueOrigen: item.tanqueOrigen ?? '',
      proveedor: item.proveedor ?? '',
      numeroComprobante: item.numeroComprobante ?? '',
      observacion: item.observacion ?? ''
    };
    this.mensaje = '';
    this.error = '';
    this.mostrarFormulario = true;
  }

  cerrarFormulario(): void {
    if (this.guardando) {
      return;
    }

    this.mostrarFormulario = false;
    this.abastecimientoEditandoId = null;
    this.formulario = this.formularioVacio();
  }

  alCambiarProgramacion(): void {
    const programacion = this.programaciones.find(
      item => item.id === Number(this.formulario.programacionId)
    );

    this.formulario.viajeId = null;
    this.formulario.conductorId = programacion?.conductorId ?? null;
    this.formulario.tractoId = programacion?.tractoId ?? null;
  }

  alCambiarTipo(): void {
    if (this.formulario.tipoAbastecimiento === 'INTERNO') {
      this.formulario.proveedor = '';
      this.formulario.numeroComprobante = '';
      this.formulario.precioUnitario = null;
    } else {
      this.formulario.tanqueOrigen = '';
    }
  }

  guardar(): void {
    if (
      this.formulario.programacionId === null ||
      this.formulario.conductorId === null ||
      this.formulario.tractoId === null ||
      this.formulario.cantidadGalones === null ||
      this.formulario.kilometraje === null
    ) {
      this.error = 'Completa los campos obligatorios del abastecimiento.';
      return;
    }

    if (this.formulario.tipoAbastecimiento === 'INTERNO' && !this.formulario.tanqueOrigen.trim()) {
      this.error = 'Selecciona el tanque de origen para el abastecimiento interno.';
      return;
    }

    if (
      this.formulario.tipoAbastecimiento === 'TERCERO' &&
      (!this.formulario.proveedor.trim() || !this.formulario.numeroComprobante.trim())
    ) {
      this.error = 'Para compra a tercero selecciona proveedor e indica número de comprobante.';
      return;
    }

    this.guardando = true;
    this.error = '';
    this.mensaje = '';

    const esNuevo = this.abastecimientoEditandoId === null;
    const payload: AbastecimientoForm = {
      programacionId: Number(this.formulario.programacionId),
      viajeId: this.formulario.viajeId === null ? null : Number(this.formulario.viajeId),
      conductorId: Number(this.formulario.conductorId),
      tractoId: Number(this.formulario.tractoId),
      tipoAbastecimiento: this.formulario.tipoAbastecimiento,
      fechaHora: this.formulario.fechaHora,
      cantidadGalones: Number(this.formulario.cantidadGalones),
      kilometraje: Number(this.formulario.kilometraje),
      precioUnitario: this.formulario.precioUnitario === null
        ? null
        : Number(this.formulario.precioUnitario),
      tanqueOrigen: this.formulario.tanqueOrigen.trim(),
      proveedor: this.formulario.proveedor.trim(),
      numeroComprobante: this.formulario.numeroComprobante.trim(),
      observacion: this.formulario.observacion.trim()
    };

    const operacion = esNuevo
      ? this.combustibleService.crear(payload)
      : this.combustibleService.actualizar(this.abastecimientoEditandoId!, payload);

    operacion
      .pipe(
        finalize(() => {
          this.guardando = false;
          this.cdr.detectChanges();
        })
      )
      .subscribe({
        next: item => {
          this.mensaje = esNuevo
            ? `Abastecimiento #AB-${this.codigo(item.id)} registrado correctamente.`
            : `Abastecimiento #AB-${this.codigo(item.id)} actualizado correctamente.`;
          this.aplicarLocal(item);
          this.mostrarFormulario = false;
          this.abastecimientoEditandoId = null;
          this.formulario = this.formularioVacio();
          this.cdr.detectChanges();
        },
        error: error => {
          this.error = this.obtenerMensajeError(error, 'No se pudo guardar el abastecimiento.');
        }
      });
  }

  eliminar(item: AbastecimientoCombustible): void {
    const confirmado = confirmarAccionDestructiva(
      `ALERTA: vas a eliminar el abastecimiento #AB-${this.codigo(item.id)}.`,
      'CONFIRMACIÓN FINAL: ¿Deseas eliminar definitivamente este abastecimiento?'
    );

    if (!confirmado) {
      return;
    }

    this.error = '';
    this.mensaje = '';

    this.combustibleService.eliminar(item.id).subscribe({
      next: () => {
        this.abastecimientos = this.abastecimientos.filter(x => x.id !== item.id);
        this.mensaje = 'Abastecimiento eliminado correctamente.';
        this.cdr.detectChanges();
      },
      error: error => {
        this.error = this.obtenerMensajeError(error, 'No se pudo eliminar el abastecimiento.');
      }
    });
  }

  codigo(id: number): string {
    return id.toString().padStart(4, '0');
  }

  codigoProgramacion(id: number): string {
    return `PROG-${id.toString().padStart(4, '0')}`;
  }

  conductorNombre(id: number): string {
    const conductor = this.conductores.find(item => item.id === id);
    return conductor ? `${conductor.nombres} ${conductor.apellidos}` : `Conductor #${id}`;
  }

  tractoPlaca(id: number): string {
    return this.tractos.find(item => item.id === id)?.placa ?? `Tracto #${id}`;
  }

  tipoEtiqueta(tipo: TipoAbastecimiento): string {
    return tipo === 'INTERNO' ? 'Interno' : 'Tercero';
  }

  private cargarCatalogosEnSegundoPlano(): void {
    forkJoin({
      programaciones: this.programacionService.listar(),
      conductores: this.programacionService.listarConductores(),
      tractos: this.programacionService.listarTractos(),
      viajes: this.viajeService.listar()
    }).subscribe({
      next: data => {
        this.programaciones = data.programaciones;
        this.conductores = data.conductores;
        this.tractos = data.tractos;
        this.viajes = data.viajes;
        this.cdr.detectChanges();
      },
      error: () => {
        // La lista principal ya está visible; los catálogos se reintentan al volver a entrar.
      }
    });
  }

  private aplicarLocal(item: AbastecimientoCombustible): void {
    const existe = this.abastecimientos.some(x => x.id === item.id);
    this.abastecimientos = existe
      ? this.abastecimientos.map(x => x.id === item.id ? item : x)
      : [item, ...this.abastecimientos];
  }

  private formularioVacio(): AbastecimientoForm {
    return {
      programacionId: null,
      viajeId: null,
      conductorId: null,
      tractoId: null,
      tipoAbastecimiento: 'INTERNO',
      fechaHora: this.fechaHoraActual(),
      cantidadGalones: null,
      kilometraje: null,
      precioUnitario: null,
      tanqueOrigen: '',
      proveedor: '',
      numeroComprobante: '',
      observacion: ''
    };
  }

  private fechaHoraActual(): string {
    const ahora = new Date();
    const offset = ahora.getTimezoneOffset();
    return new Date(ahora.getTime() - offset * 60_000).toISOString().slice(0, 16);
  }

  private obtenerMensajeError(error: any, fallback: string): string {
    const respuesta = error?.error;

    if (typeof respuesta === 'string' && respuesta.trim()) {
      return respuesta;
    }

    if (respuesta?.error) {
      return respuesta.error;
    }

    if (respuesta?.message) {
      return respuesta.message;
    }

    if (respuesta?.campos) {
      const primerMensaje = Object.values(respuesta.campos)[0];
      if (typeof primerMensaje === 'string') {
        return primerMensaje;
      }
    }

    return fallback;
  }
}
