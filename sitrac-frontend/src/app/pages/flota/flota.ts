import { CommonModule } from '@angular/common';
import { ChangeDetectorRef, Component, OnInit, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { finalize } from 'rxjs';

import {
  EstadoUnidad,
  Semirremolque,
  SemirremolqueForm,
  TipoSemirremolque,
  Tracto,
  TractoForm
} from '../../core/models/flota.model';
import { FlotaService } from '../../core/services/flota.service';
import { confirmarAccionDestructiva } from '../../core/utils/confirmacion.util';

@Component({
  selector: 'app-flota',
  imports: [CommonModule, FormsModule],
  templateUrl: './flota.html',
  styleUrl: './flota.scss'
})
export class Flota implements OnInit {
  private readonly flotaService = inject(FlotaService);
  private readonly cdr = inject(ChangeDetectorRef);

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
    const primeraCarga = this.tractos.length === 0;
    if (primeraCarga) {
      this.cargando = true;
    }
    this.error = '';

    this.flotaService.listarTractos().subscribe({
      next: tractos => {
        this.tractos = [...tractos].sort((a, b) => b.id - a.id);
        this.cargando = false;
        this.cdr.detectChanges();
      },
      error: error => {
        this.error = this.obtenerMensajeError(error, 'No se pudo cargar la flota. Verifica flota-service.');
        this.cargando = false;
        this.cdr.detectChanges();
      }
    });

    this.flotaService.listarSemirremolques().subscribe({
      next: semis => {
        this.semirremolques = [...semis].sort((a, b) => b.id - a.id);
        this.cdr.detectChanges();
      },
      error: () => {
        // Tractos pueden mostrarse aunque semirremolques tarden un poco más.
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
    const esNuevo = this.editandoId === null;
    const payload: TractoForm = {
      ...this.tractoForm,
      placa: this.tractoForm.placa.trim().toUpperCase(),
      marca: this.tractoForm.marca.trim(),
      modelo: this.tractoForm.modelo.trim()
    };

    const op = esNuevo
      ? this.flotaService.crearTracto(payload)
      : this.flotaService.actualizarTracto(this.editandoId!, payload);

    op.pipe(finalize(() => {
      this.guardando = false;
      this.cdr.detectChanges();
    })).subscribe({
      next: tracto => {
        this.tractos = this.tractos.some(x => x.id === tracto.id)
          ? this.tractos.map(x => x.id === tracto.id ? tracto : x)
          : [tracto, ...this.tractos];
        this.finalizarGuardado('Tracto guardado correctamente.');
      },
      error: error => {
        this.error = this.obtenerMensajeError(error, 'No se pudo guardar el tracto.');
      }
    });
  }

  private guardarSemi(): void {
    if (!this.semiForm.placa.trim() || !this.semiForm.capacidadToneladas) {
      this.error = 'Completa los campos obligatorios del semirremolque.';
      return;
    }

    this.guardando = true;
    const esNuevo = this.editandoId === null;
    const payload: SemirremolqueForm = {
      ...this.semiForm,
      placa: this.semiForm.placa.trim().toUpperCase()
    };

    const op = esNuevo
      ? this.flotaService.crearSemirremolque(payload)
      : this.flotaService.actualizarSemirremolque(this.editandoId!, payload);

    op.pipe(finalize(() => {
      this.guardando = false;
      this.cdr.detectChanges();
    })).subscribe({
      next: semi => {
        this.semirremolques = this.semirremolques.some(x => x.id === semi.id)
          ? this.semirremolques.map(x => x.id === semi.id ? semi : x)
          : [semi, ...this.semirremolques];
        this.finalizarGuardado('Semirremolque guardado correctamente.');
      },
      error: error => {
        this.error = this.obtenerMensajeError(error, 'No se pudo guardar el semirremolque.');
      }
    });
  }

  cambiarEstadoTracto(item: Tracto, estado: EstadoUnidad): void {
    this.flotaService.cambiarEstadoTracto(item.id, estado).subscribe({
      next: actualizado => {
        this.tractos = this.tractos.map(x => x.id === actualizado.id ? actualizado : x);
        this.mensaje = `Estado de ${item.placa} actualizado.`;
        this.cdr.detectChanges();
      },
      error: error => this.error = this.obtenerMensajeError(error, 'No se pudo cambiar el estado.')
    });
  }

  cambiarEstadoSemi(item: Semirremolque, estado: EstadoUnidad): void {
    this.flotaService.cambiarEstadoSemirremolque(item.id, estado).subscribe({
      next: actualizado => {
        this.semirremolques = this.semirremolques.map(x => x.id === actualizado.id ? actualizado : x);
        this.mensaje = `Estado de ${item.placa} actualizado.`;
        this.cdr.detectChanges();
      },
      error: error => this.error = this.obtenerMensajeError(error, 'No se pudo cambiar el estado.')
    });
  }

  eliminarTracto(item: Tracto): void {
    if (item.estado === 'ASIGNADO') {
      this.error = 'No se puede eliminar un tracto asignado.';
      return;
    }

    const confirmado = confirmarAccionDestructiva(
      `ALERTA: vas a eliminar el tracto ${item.placa}.`,
      `CONFIRMACIÓN FINAL: ¿Deseas eliminar definitivamente el tracto ${item.placa}?`
    );
    if (!confirmado) return;

    this.flotaService.eliminarTracto(item.id).subscribe({
      next: () => {
        this.tractos = this.tractos.filter(x => x.id !== item.id);
        this.mensaje = 'Tracto eliminado correctamente.';
        this.cdr.detectChanges();
      },
      error: error => this.error = this.obtenerMensajeError(error, 'No se pudo eliminar el tracto.')
    });
  }

  eliminarSemi(item: Semirremolque): void {
    if (item.estado === 'ASIGNADO') {
      this.error = 'No se puede eliminar un semirremolque asignado.';
      return;
    }

    const confirmado = confirmarAccionDestructiva(
      `ALERTA: vas a eliminar el semirremolque ${item.placa}.`,
      `CONFIRMACIÓN FINAL: ¿Deseas eliminar definitivamente el semirremolque ${item.placa}?`
    );
    if (!confirmado) return;

    this.flotaService.eliminarSemirremolque(item.id).subscribe({
      next: () => {
        this.semirremolques = this.semirremolques.filter(x => x.id !== item.id);
        this.mensaje = 'Semirremolque eliminado correctamente.';
        this.cdr.detectChanges();
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
    this.mostrarFormulario = false;
    this.editandoId = null;
    this.cdr.detectChanges();
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
