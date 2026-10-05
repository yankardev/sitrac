import { CommonModule } from '@angular/common';
import { Component, OnInit, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { forkJoin } from 'rxjs';

import { ConductorOperacion, Programacion } from '../../core/models/programacion.model';
import {
  EstadoRegistroSomma,
  RegistroSomma,
  RegistroSommaForm,
  TipoRegistroSomma
} from '../../core/models/somma.model';
import { ProgramacionService } from '../../core/services/programacion.service';
import { SommaService } from '../../core/services/somma.service';

@Component({
  selector: 'app-somma',
  imports: [CommonModule, FormsModule],
  templateUrl: './somma.html',
  styleUrl: './somma.scss'
})
export class Somma implements OnInit {
  private readonly sommaService = inject(SommaService);
  private readonly programacionService = inject(ProgramacionService);

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
    this.cargando = true;
    this.error = '';

    forkJoin({
      registros: this.sommaService.listar(),
      programaciones: this.programacionService.listar(),
      conductores: this.programacionService.listarConductores()
    }).subscribe({
      next: data => {
        this.registros = [...data.registros].sort((a, b) => b.id - a.id);
        this.programaciones = data.programaciones;
        this.conductores = data.conductores;
        this.cargando = false;
      },
      error: error => {
        this.error = this.obtenerMensajeError(
          error,
          'No se pudo cargar SOMMA. Verifica somma-service, programacion-service y conductor-service.'
        );
        this.cargando = false;
      }
    });
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
      estado: this.registroEditandoId === null ? null : this.formulario.estado
    };

    const operacion = this.registroEditandoId === null
      ? this.sommaService.crear(payload)
      : this.sommaService.actualizar(this.registroEditandoId, payload);

    operacion.subscribe({
      next: registro => {
        this.mensaje = this.registroEditandoId === null
          ? `Registro SOMMA #${this.codigo(registro.id)} creado correctamente.`
          : `Registro SOMMA #${this.codigo(registro.id)} actualizado correctamente.`;
        this.guardando = false;
        this.mostrarFormulario = false;
        this.registroEditandoId = null;
        this.formulario = this.formularioVacio();
        this.cargarDatos();
      },
      error: error => {
        this.error = this.obtenerMensajeError(error, 'No se pudo guardar el registro SOMMA.');
        this.guardando = false;
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
      next: () => {
        this.mensaje = estado === 'CERRADO'
          ? 'Registro SOMMA cerrado correctamente.'
          : 'Registro SOMMA cancelado correctamente.';
        this.cargarDatos();
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

    if (!window.confirm(`¿Eliminar el registro SOMMA #${this.codigo(registro.id)}?`)) {
      return;
    }

    this.sommaService.eliminar(registro.id).subscribe({
      next: () => {
        this.mensaje = 'Registro SOMMA eliminado correctamente.';
        this.cargarDatos();
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
