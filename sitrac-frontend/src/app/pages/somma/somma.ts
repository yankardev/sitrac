import { CommonModule } from '@angular/common';
import { ChangeDetectorRef, Component, OnInit, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { finalize, forkJoin } from 'rxjs';

import { ConductorOperacion, Programacion } from '../../core/models/programacion.model';
import {
  EstadoRegistroSomma,
  RegistroSomma,
  RegistroSommaForm,
  TipoRegistroSomma
} from '../../core/models/somma.model';
import { ProgramacionService } from '../../core/services/programacion.service';
import { SommaService } from '../../core/services/somma.service';
import { confirmarAccionDestructiva } from '../../core/utils/confirmacion.util';

@Component({
  selector: 'app-somma',
  imports: [CommonModule, FormsModule],
  templateUrl: './somma.html',
  styleUrl: './somma.scss'
})
export class Somma implements OnInit {
  private readonly sommaService = inject(SommaService);
  private readonly programacionService = inject(ProgramacionService);
  private readonly cdr = inject(ChangeDetectorRef);

  readonly lugaresOperacion: string[] = [
    'Base Trujillo',
    'Base Lima',
    'Base Cajamarca',
    'Base Pacasmayo',
    'Base Chiclayo',
    'Base Piura',
    'Base Chimbote',
    'Patio de Operaciones Trujillo',
    'Patio de Operaciones Lima'
  ];

  registros: RegistroSomma[] = [];
  programaciones: Programacion[] = [];
  conductores: ConductorOperacion[] = [];

  busqueda = '';
  filtroTipo: TipoRegistroSomma | 'TODOS' = 'TODOS';
  filtroEstado: EstadoRegistroSomma | 'TODOS' = 'TODOS';

  cargando = false;
  guardando = false;
  mostrarFormulario = false;
  registroEditandoId: number | null = null;
  mensaje = '';
  error = '';

  formulario: RegistroSommaForm = this.formularioVacio();

  ngOnInit(): void {
    this.cargarDatos();
  }

  get registrosFiltrados(): RegistroSomma[] {
    const termino = this.busqueda.trim().toLowerCase();

    return this.registros.filter(registro => {
      if (this.filtroTipo !== 'TODOS' && registro.tipo !== this.filtroTipo) {
        return false;
      }

      if (this.filtroEstado !== 'TODOS' && registro.estado !== this.filtroEstado) {
        return false;
      }

      if (!termino) {
        return true;
      }

      return (
        registro.titulo.toLowerCase().includes(termino) ||
        registro.descripcion.toLowerCase().includes(termino) ||
        (registro.lugar ?? '').toLowerCase().includes(termino) ||
        this.etiquetaTipo(registro.tipo).toLowerCase().includes(termino) ||
        this.conductorNombre(registro.conductorId).toLowerCase().includes(termino)
      );
    });
  }

  get programacionesActivas(): Programacion[] {
    return this.programaciones.filter(programacion => programacion.estado === 'PROGRAMADA');
  }

  get totalCharlas(): number {
    return this.registros.filter(registro => registro.tipo === 'CHARLA').length;
  }

  get totalCapacitaciones(): number {
    return this.registros.filter(registro => registro.tipo === 'CAPACITACION').length;
  }

  get totalEventos(): number {
    return this.registros.filter(registro => registro.tipo === 'ACCIDENTE' || registro.tipo === 'INCIDENTE').length;
  }

  get totalAbiertos(): number {
    return this.registros.filter(registro => registro.estado === 'REGISTRADO').length;
  }

  cargarDatos(): void {
    const primeraCarga = this.registros.length === 0;
    if (primeraCarga) {
      this.cargando = true;
    }
    this.error = '';

    this.sommaService.listar().subscribe({
      next: registros => {
        this.registros = [...registros].sort((a, b) => b.id - a.id);
        this.cargando = false;
        this.cdr.detectChanges();
      },
      error: error => {
        this.error = this.obtenerMensajeError(
          error,
          'No se pudo cargar SOMMA. Verifica somma-service.'
        );
        this.cargando = false;
        this.cdr.detectChanges();
      }
    });

    this.cargarCatalogosEnSegundoPlano();
  }

  abrirNuevo(tipo: TipoRegistroSomma = 'CHARLA'): void {
    this.registroEditandoId = null;
    this.formulario = this.formularioVacio();
    this.formulario.tipo = tipo;
    this.mensaje = '';
    this.error = '';
    this.mostrarFormulario = true;
  }

  editar(registro: RegistroSomma): void {
    this.registroEditandoId = registro.id;
    this.formulario = {
      tipo: registro.tipo,
      programacionId: registro.programacionId,
      conductorId: registro.conductorId,
      fecha: registro.fecha.slice(0, 16),
      titulo: registro.titulo,
      descripcion: registro.descripcion,
      lugar: registro.lugar ?? '',
      estado: registro.estado
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
    this.registroEditandoId = null;
    this.formulario = this.formularioVacio();
  }

  alCambiarTipo(): void {
    if (this.formulario.tipo === 'CHARLA') {
      this.formulario.programacionId = null;
      this.formulario.conductorId = null;
    }
  }

  alCambiarProgramacion(): void {
    if (this.formulario.programacionId === null) {
      if (this.formulario.tipo === 'CHARLA') {
        this.formulario.conductorId = null;
      }
      return;
    }

    const programacion = this.programaciones.find(
      item => item.id === Number(this.formulario.programacionId)
    );

    if (programacion) {
      this.formulario.conductorId = programacion.conductorId;
    }
  }

  guardar(): void {
    if (this.formulario.tipo === 'CHARLA') {
      if (this.formulario.programacionId === null || this.formulario.conductorId === null) {
        this.error = 'La charla SOMMA debe indicar una programación y su conductor.';
        return;
      }
    }

    this.guardando = true;
    this.error = '';
    this.mensaje = '';

    const esNuevo = this.registroEditandoId === null;
    const payload: RegistroSommaForm = {
      tipo: this.formulario.tipo,
      programacionId: this.formulario.programacionId === null
        ? null
        : Number(this.formulario.programacionId),
      conductorId: this.formulario.conductorId === null
        ? null
        : Number(this.formulario.conductorId),
      fecha: this.formulario.fecha,
      titulo: this.formulario.titulo.trim(),
      descripcion: this.formulario.descripcion.trim(),
      lugar: this.formulario.lugar.trim(),
      estado: esNuevo ? null : this.formulario.estado
    };

    const operacion = esNuevo
      ? this.sommaService.crear(payload)
      : this.sommaService.actualizar(this.registroEditandoId!, payload);

    operacion
      .pipe(
        finalize(() => {
          this.guardando = false;
          this.cdr.detectChanges();
        })
      )
      .subscribe({
        next: registro => {
          this.mensaje = esNuevo
            ? `Registro SOMMA #${this.codigo(registro.id)} creado correctamente.`
            : `Registro SOMMA #${this.codigo(registro.id)} actualizado correctamente.`;
          this.aplicarLocal(registro);
          this.mostrarFormulario = false;
          this.registroEditandoId = null;
          this.formulario = this.formularioVacio();
          this.cdr.detectChanges();
        },
        error: error => {
          this.error = this.obtenerMensajeError(error, 'No se pudo guardar el registro SOMMA.');
        }
      });
  }

  cambiarEstado(registro: RegistroSomma, estado: EstadoRegistroSomma): void {
    const accion = estado === 'CERRADO' ? 'cerrar' : 'cancelar';
    const confirmado = window.confirm(
      `¿Deseas ${accion} el registro SOMMA #${this.codigo(registro.id)}?`
    );

    if (!confirmado) {
      return;
    }

    const payload: RegistroSommaForm = {
      tipo: registro.tipo,
      programacionId: registro.programacionId,
      conductorId: registro.conductorId,
      fecha: registro.fecha,
      titulo: registro.titulo,
      descripcion: registro.descripcion,
      lugar: registro.lugar ?? '',
      estado
    };

    this.sommaService.actualizar(registro.id, payload).subscribe({
      next: actualizado => {
        this.mensaje = estado === 'CERRADO'
          ? 'Registro SOMMA cerrado correctamente.'
          : 'Registro SOMMA cancelado correctamente.';
        this.aplicarLocal(actualizado);
        this.cdr.detectChanges();
      },
      error: error => {
        this.error = this.obtenerMensajeError(error, 'No se pudo actualizar el estado del registro.');
      }
    });
  }

  eliminar(registro: RegistroSomma): void {
    if (registro.estado === 'REGISTRADO') {
      this.error = 'Cierra o cancela el registro antes de eliminarlo.';
      return;
    }

    const confirmado = confirmarAccionDestructiva(
      `ALERTA: vas a eliminar el registro SOMMA #${this.codigo(registro.id)}.`,
      'CONFIRMACIÓN FINAL: ¿Deseas eliminar definitivamente este registro SOMMA?'
    );

    if (!confirmado) {
      return;
    }

    this.sommaService.eliminar(registro.id).subscribe({
      next: () => {
        this.registros = this.registros.filter(x => x.id !== registro.id);
        this.mensaje = 'Registro SOMMA eliminado correctamente.';
        this.cdr.detectChanges();
      },
      error: error => {
        this.error = this.obtenerMensajeError(error, 'No se pudo eliminar el registro SOMMA.');
      }
    });
  }

  conductorNombre(id: number | null): string {
    if (id === null) {
      return 'No asociado';
    }

    const conductor = this.conductores.find(item => item.id === id);
    return conductor ? `${conductor.nombres} ${conductor.apellidos}` : `Conductor #${id}`;
  }

  etiquetaTipo(tipo: TipoRegistroSomma): string {
    const etiquetas: Record<TipoRegistroSomma, string> = {
      CHARLA: 'Charla de seguridad',
      CAPACITACION: 'Capacitación',
      ACCIDENTE: 'Accidente',
      INCIDENTE: 'Incidente',
      INSPECCION: 'Inspección'
    };
    return etiquetas[tipo];
  }

  etiquetaEstado(estado: EstadoRegistroSomma): string {
    const etiquetas: Record<EstadoRegistroSomma, string> = {
      REGISTRADO: 'REGISTRADO',
      CERRADO: 'CERRADO',
      CANCELADO: 'CANCELADO'
    };
    return etiquetas[estado];
  }

  claseTipo(tipo: TipoRegistroSomma): string {
    return tipo.toLowerCase();
  }

  codigo(id: number): string {
    return id.toString().padStart(4, '0');
  }

  private cargarCatalogosEnSegundoPlano(): void {
    forkJoin({
      programaciones: this.programacionService.listar(),
      conductores: this.programacionService.listarConductores()
    }).subscribe({
      next: data => {
        this.programaciones = data.programaciones;
        this.conductores = data.conductores;
        this.cdr.detectChanges();
      },
      error: () => {
        // La lista principal de SOMMA permanece visible aunque un catálogo demore.
      }
    });
  }

  private aplicarLocal(registro: RegistroSomma): void {
    const existe = this.registros.some(x => x.id === registro.id);
    this.registros = existe
      ? this.registros.map(x => x.id === registro.id ? registro : x)
      : [registro, ...this.registros];
  }

  private formularioVacio(): RegistroSommaForm {
    return {
      tipo: 'CHARLA',
      programacionId: null,
      conductorId: null,
      fecha: this.fechaActual(),
      titulo: '',
      descripcion: '',
      lugar: '',
      estado: null
    };
  }

  private fechaActual(): string {
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
