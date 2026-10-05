import { CommonModule } from '@angular/common';
import { Component, OnInit, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { forkJoin } from 'rxjs';

import {
  EstadoMantenimiento,
  Mantenimiento as MantenimientoModel,
  MantenimientoForm,
  TipoUnidad
} from '../../core/models/mantenimiento.model';
import { SemirremolqueOperacion, TractoOperacion } from '../../core/models/programacion.model';
import { MantenimientoService } from '../../core/services/mantenimiento.service';

@Component({
  selector: 'app-mantenimiento',
  imports: [CommonModule, FormsModule],
  templateUrl: './mantenimiento.html',
  styleUrl: './mantenimiento.scss'
})
export class Mantenimiento implements OnInit {
  private readonly service = inject(MantenimientoService);

  mantenimientos: MantenimientoModel[] = [];
  tractos: TractoOperacion[] = [];
  semirremolques: SemirremolqueOperacion[] = [];

  busqueda = '';
  filtroEstado: EstadoMantenimiento | 'TODOS' = 'TODOS';
  cargando = false;
  guardando = false;
  mostrarFormulario = false;
  editandoId: number | null = null;
  mensaje = '';
  error = '';

  formulario: MantenimientoForm = this.formularioVacio();

  ngOnInit(): void {
    this.cargarDatos();
  }

  get mantenimientosFiltrados(): MantenimientoModel[] {
    const termino = this.busqueda.trim().toLowerCase();
    return this.mantenimientos.filter(item => {
      if (this.filtroEstado !== 'TODOS' && item.estado !== this.filtroEstado) return false;
      if (!termino) return true;
      return this.unidadEtiqueta(item).toLowerCase().includes(termino)
        || item.descripcion.toLowerCase().includes(termino)
        || item.tipoMantenimiento.toLowerCase().includes(termino);
    });
  }

  get totalProgramados(): number {
    return this.mantenimientos.filter(x => x.estado === 'PROGRAMADO').length;
  }

  get totalEnProceso(): number {
    return this.mantenimientos.filter(x => x.estado === 'EN_PROCESO').length;
  }

  get totalFinalizados(): number {
    return this.mantenimientos.filter(x => x.estado === 'FINALIZADO').length;
  }

  get unidadesDisponibles(): Array<TractoOperacion | SemirremolqueOperacion> {
    const lista = this.formulario.tipoUnidad === 'TRACTO' ? this.tractos : this.semirremolques;
    return lista.filter(x => x.activo && x.estado !== 'INACTIVO' && x.estado !== 'ASIGNADO');
  }

  cargarDatos(): void {
    this.cargando = true;
    this.error = '';

    forkJoin({
      mantenimientos: this.service.listar(),
      tractos: this.service.listarTractos(),
      semirremolques: this.service.listarSemirremolques()
    }).subscribe({
      next: data => {
        this.mantenimientos = [...data.mantenimientos].sort((a, b) => b.id - a.id);
        this.tractos = data.tractos;
        this.semirremolques = data.semirremolques;
        this.cargando = false;
      },
      error: error => {
        this.error = this.mensajeError(error, 'No se pudo cargar Mantenimiento. Verifica mantenimiento-service y flota-service.');
        this.cargando = false;
      }
    });
  }

  abrirNuevo(): void {
    this.editandoId = null;
    this.formulario = this.formularioVacio();
    this.mensaje = '';
    this.error = '';
    this.mostrarFormulario = true;
  }

  editar(item: MantenimientoModel): void {
    this.editandoId = item.id;
    this.formulario = {
      tipoUnidad: item.tipoUnidad,
      unidadId: item.unidadId,
      tipoMantenimiento: item.tipoMantenimiento,
      fechaInicio: item.fechaInicio,
      fechaFin: item.fechaFin,
      descripcion: item.descripcion,
      costo: item.costo,
      estado: item.estado
    };
    this.mostrarFormulario = true;
    this.mensaje = '';
    this.error = '';
  }

  cerrarFormulario(): void {
    if (this.guardando) return;
    this.mostrarFormulario = false;
    this.editandoId = null;
    this.formulario = this.formularioVacio();
  }

  alCambiarTipoUnidad(): void {
    this.formulario.unidadId = null;
  }

  guardar(): void {
    if (this.formulario.unidadId === null) {
      this.error = 'Selecciona una unidad.';
      return;
    }

    this.guardando = true;
    this.error = '';
    this.mensaje = '';

    const payload: MantenimientoForm = {
      ...this.formulario,
      unidadId: Number(this.formulario.unidadId),
      descripcion: this.formulario.descripcion.trim(),
      costo: this.formulario.costo === null ? null : Number(this.formulario.costo),
      fechaFin: this.formulario.fechaFin || null,
      estado: this.editandoId === null ? null : this.formulario.estado
    };

    const operacion = this.editandoId === null
      ? this.service.crear(payload)
      : this.service.actualizar(this.editandoId, payload);

    operacion.subscribe({
      next: item => {
        this.mensaje = this.editandoId === null
          ? `Mantenimiento #MANT-${this.codigo(item.id)} registrado correctamente.`
          : `Mantenimiento #MANT-${this.codigo(item.id)} actualizado correctamente.`;
        this.guardando = false;
        this.mostrarFormulario = false;
        this.editandoId = null;
        this.formulario = this.formularioVacio();
        this.cargarDatos();
      },
      error: error => {
        this.error = this.mensajeError(error, 'No se pudo guardar el mantenimiento.');
        this.guardando = false;
      }
    });
  }

  cambiarEstado(item: MantenimientoModel, estado: EstadoMantenimiento): void {
    const payload: MantenimientoForm = {
      tipoUnidad: item.tipoUnidad,
      unidadId: item.unidadId,
      tipoMantenimiento: item.tipoMantenimiento,
      fechaInicio: item.fechaInicio,
      fechaFin: estado === 'FINALIZADO' ? (item.fechaFin ?? this.fechaHoy()) : item.fechaFin,
      descripcion: item.descripcion,
      costo: item.costo,
      estado
    };

    this.service.actualizar(item.id, payload).subscribe({
      next: () => {
        this.mensaje = `Mantenimiento actualizado a ${this.etiquetaEstado(estado)}.`;
        this.cargarDatos();
      },
      error: error => this.error = this.mensajeError(error, 'No se pudo actualizar el estado.')
    });
  }

  eliminar(item: MantenimientoModel): void {
    if (!window.confirm(`¿Eliminar el mantenimiento #MANT-${this.codigo(item.id)}?`)) return;

    this.service.eliminar(item.id).subscribe({
      next: () => {
        this.mensaje = 'Mantenimiento eliminado correctamente.';
        this.cargarDatos();
      },
      error: error => this.error = this.mensajeError(error, 'No se pudo eliminar el mantenimiento.')
    });
  }

  unidadEtiqueta(item: MantenimientoModel): string {
    const lista = item.tipoUnidad === 'TRACTO' ? this.tractos : this.semirremolques;
    const unidad = lista.find(x => x.id === item.unidadId);
    return unidad ? `${item.tipoUnidad === 'TRACTO' ? 'Tracto' : 'Semirremolque'} ${unidad.placa}` : `${item.tipoUnidad} #${item.unidadId}`;
  }

  unidadOpcion(unidad: TractoOperacion | SemirremolqueOperacion): string {
    return `${unidad.placa} · ${unidad.estado} · ${unidad.capacidadToneladas} TM`;
  }

  etiquetaEstado(estado: EstadoMantenimiento): string {
    return estado.replace('_', ' ');
  }

  codigo(id: number): string {
    return id.toString().padStart(4, '0');
  }

  private formularioVacio(): MantenimientoForm {
    return {
      tipoUnidad: 'TRACTO',
      unidadId: null,
      tipoMantenimiento: 'PREVENTIVO',
      fechaInicio: this.fechaHoy(),
      fechaFin: null,
      descripcion: '',
      costo: null,
      estado: null
    };
  }

  private fechaHoy(): string {
    const ahora = new Date();
    const offset = ahora.getTimezoneOffset();
    return new Date(ahora.getTime() - offset * 60_000).toISOString().slice(0, 10);
  }

  private mensajeError(error: any, fallback: string): string {
    const respuesta = error?.error;
    if (typeof respuesta === 'string' && respuesta.trim()) return respuesta;
    if (respuesta?.error) return respuesta.error;
    if (respuesta?.message) return respuesta.message;
    return fallback;
  }
}
