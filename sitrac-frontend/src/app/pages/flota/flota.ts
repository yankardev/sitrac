import { CommonModule } from '@angular/common';
import { Component, OnInit, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { forkJoin } from 'rxjs';

import {
  EstadoUnidad,
  Semirremolque,
  SemirremolqueForm,
  TipoSemirremolque,
  Tracto,
  TractoForm
} from '../../core/models/flota.model';
import { FlotaService } from '../../core/services/flota.service';

@Component({
  selector: 'app-flota',
  imports: [CommonModule, FormsModule],
  templateUrl: './flota.html',
  styleUrl: './flota.scss'
})
export class Flota implements OnInit {
  private readonly flotaService = inject(FlotaService);

  tractos: Tracto[] = [];
  semirremolques: Semirremolque[] = [];

  vista: 'TRACTOS' | 'SEMIRREMOLQUES' = 'TRACTOS';
  busqueda = '';
  filtroEstado: EstadoUnidad | 'TODOS' = 'TODOS';

  cargando = false;
  guardando = false;
  mostrarFormulario = false;
  editandoId: number | null = null;

  mensaje = '';
  error = '';

  tractoForm: TractoForm = this.tractoVacio();
  semiForm: SemirremolqueForm = this.semiVacio();

  ngOnInit(): void {
    this.cargarDatos();
  }

  get tractosFiltrados(): Tracto[] {
    const t = this.busqueda.trim().toLowerCase();
    return this.tractos.filter(item => {
      const estadoOk = this.filtroEstado === 'TODOS' || item.estado === this.filtroEstado;
      const textoOk = !t || [item.placa, item.marca, item.modelo].join(' ').toLowerCase().includes(t);
      return estadoOk && textoOk;
    });
  }

  get semisFiltrados(): Semirremolque[] {
    const t = this.busqueda.trim().toLowerCase();
    return this.semirremolques.filter(item => {
      const estadoOk = this.filtroEstado === 'TODOS' || item.estado === this.filtroEstado;
      const textoOk = !t || [item.placa, item.tipo].join(' ').toLowerCase().includes(t);
      return estadoOk && textoOk;
    });
  }

  get totalUnidades(): number {
    return this.tractos.length + this.semirremolques.length;
  }

  get disponibles(): number {
    return [...this.tractos, ...this.semirremolques].filter(item => item.estado === 'DISPONIBLE').length;
  }

  get asignadas(): number {
    return [...this.tractos, ...this.semirremolques].filter(item => item.estado === 'ASIGNADO').length;
  }

  get mantenimiento(): number {
    return [...this.tractos, ...this.semirremolques].filter(item => item.estado === 'MANTENIMIENTO').length;
  }

  cargarDatos(): void {
    this.cargando = true;
    this.error = '';

    forkJoin({
      tractos: this.flotaService.listarTractos(),
      semirremolques: this.flotaService.listarSemirremolques()
    }).subscribe({
      next: data => {
        this.tractos = [...data.tractos].sort((a, b) => b.id - a.id);
        this.semirremolques = [...data.semirremolques].sort((a, b) => b.id - a.id);
        this.cargando = false;
      },
      error: error => {
        this.error = this.obtenerMensajeError(error, 'No se pudo cargar la flota. Verifica flota-service.');
        this.cargando = false;
      }
    });
  }

  cambiarVista(vista: 'TRACTOS' | 'SEMIRREMOLQUES'): void {
    this.vista = vista;
    this.busqueda = '';
    this.filtroEstado = 'TODOS';
  }

  abrirNuevo(): void {
    this.editandoId = null;
    this.tractoForm = this.tractoVacio();
    this.semiForm = this.semiVacio();
    this.mensaje = '';
    this.error = '';
    this.mostrarFormulario = true;
  }

  editarTracto(item: Tracto): void {
    this.vista = 'TRACTOS';
    this.editandoId = item.id;
    this.tractoForm = {
      placa: item.placa,
      marca: item.marca,
      modelo: item.modelo,
      anio: item.anio,
      capacidadToneladas: item.capacidadToneladas,
      estado: item.estado,
      activo: item.activo
    };
    this.mostrarFormulario = true;
  }

  editarSemi(item: Semirremolque): void {
    this.vista = 'SEMIRREMOLQUES';
    this.editandoId = item.id;
    this.semiForm = {
      placa: item.placa,
      tipo: item.tipo,
      capacidadToneladas: item.capacidadToneladas,
      estado: item.estado,
      activo: item.activo
    };
    this.mostrarFormulario = true;
  }

  cerrarFormulario(): void {
    if (this.guardando) return;
    this.mostrarFormulario = false;
    this.editandoId = null;
  }

  guardar(): void {
    this.vista === 'TRACTOS' ? this.guardarTracto() : this.guardarSemi();
  }

  private guardarTracto(): void {
    if (!this.tractoForm.placa.trim() || !this.tractoForm.marca.trim() || !this.tractoForm.modelo.trim() || !this.tractoForm.anio || !this.tractoForm.capacidadToneladas) {
      this.error = 'Completa los campos obligatorios del tracto.';
      return;
    }

    this.guardando = true;
    const payload: TractoForm = {
      ...this.tractoForm,
      placa: this.tractoForm.placa.trim().toUpperCase(),
      marca: this.tractoForm.marca.trim(),
      modelo: this.tractoForm.modelo.trim()
    };

    const op = this.editandoId === null
      ? this.flotaService.crearTracto(payload)
      : this.flotaService.actualizarTracto(this.editandoId, payload);

    op.subscribe({
      next: () => this.finalizarGuardado('Tracto guardado correctamente.'),
      error: error => {
        this.error = this.obtenerMensajeError(error, 'No se pudo guardar el tracto.');
        this.guardando = false;
      }
    });
  }

  private guardarSemi(): void {
    if (!this.semiForm.placa.trim() || !this.semiForm.capacidadToneladas) {
      this.error = 'Completa los campos obligatorios del semirremolque.';
      return;
    }

    this.guardando = true;
    const payload: SemirremolqueForm = {
      ...this.semiForm,
      placa: this.semiForm.placa.trim().toUpperCase()
    };

    const op = this.editandoId === null
      ? this.flotaService.crearSemirremolque(payload)
      : this.flotaService.actualizarSemirremolque(this.editandoId, payload);

    op.subscribe({
      next: () => this.finalizarGuardado('Semirremolque guardado correctamente.'),
      error: error => {
        this.error = this.obtenerMensajeError(error, 'No se pudo guardar el semirremolque.');
        this.guardando = false;
      }
    });
  }

  cambiarEstadoTracto(item: Tracto, estado: EstadoUnidad): void {
    this.flotaService.cambiarEstadoTracto(item.id, estado).subscribe({
      next: () => {
        this.mensaje = `Estado de ${item.placa} actualizado.`;
        this.cargarDatos();
      },
      error: error => this.error = this.obtenerMensajeError(error, 'No se pudo cambiar el estado.')
    });
  }

  cambiarEstadoSemi(item: Semirremolque, estado: EstadoUnidad): void {
    this.flotaService.cambiarEstadoSemirremolque(item.id, estado).subscribe({
      next: () => {
        this.mensaje = `Estado de ${item.placa} actualizado.`;
        this.cargarDatos();
      },
      error: error => this.error = this.obtenerMensajeError(error, 'No se pudo cambiar el estado.')
    });
  }

  eliminarTracto(item: Tracto): void {
    if (item.estado === 'ASIGNADO') {
      this.error = 'No se puede eliminar un tracto asignado.';
      return;
    }
    if (!window.confirm(`¿Eliminar el tracto ${item.placa}?`)) return;
    this.flotaService.eliminarTracto(item.id).subscribe({
      next: () => {
        this.mensaje = 'Tracto eliminado correctamente.';
        this.cargarDatos();
      },
      error: error => this.error = this.obtenerMensajeError(error, 'No se pudo eliminar el tracto.')
    });
  }

  eliminarSemi(item: Semirremolque): void {
    if (item.estado === 'ASIGNADO') {
      this.error = 'No se puede eliminar un semirremolque asignado.';
      return;
    }
    if (!window.confirm(`¿Eliminar el semirremolque ${item.placa}?`)) return;
    this.flotaService.eliminarSemirremolque(item.id).subscribe({
      next: () => {
        this.mensaje = 'Semirremolque eliminado correctamente.';
        this.cargarDatos();
      },
      error: error => this.error = this.obtenerMensajeError(error, 'No se pudo eliminar el semirremolque.')
    });
  }

  etiquetaEstado(estado: EstadoUnidad): string {
    return estado.replace('_', ' ');
  }

  etiquetaTipo(tipo: TipoSemirremolque): string {
    return tipo.replace('_', ' ');
  }

  private finalizarGuardado(mensaje: string): void {
    this.mensaje = mensaje;
    this.error = '';
    this.guardando = false;
    this.mostrarFormulario = false;
    this.editandoId = null;
    this.cargarDatos();
  }

  private tractoVacio(): TractoForm {
    return {
      placa: '',
      marca: '',
      modelo: '',
      anio: new Date().getFullYear(),
      capacidadToneladas: 35,
      estado: 'DISPONIBLE',
      activo: true
    };
  }

  private semiVacio(): SemirremolqueForm {
    return {
      placa: '',
      tipo: 'PLATAFORMA',
      capacidadToneladas: 35,
      estado: 'DISPONIBLE',
      activo: true
    };
  }

  private obtenerMensajeError(error: any, fallback: string): string {
    const r = error?.error;
    if (typeof r === 'string' && r.trim()) return r;
    if (r?.error) return r.error;
    if (r?.message) return r.message;
    return fallback;
  }
}
