import { CommonModule } from '@angular/common';
import { Component, OnInit, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { forkJoin } from 'rxjs';

import { Pedido } from '../../core/models/pedido.model';
import {
  ConductorOperacion,
  Programacion as ProgramacionModel,
  TractoOperacion
} from '../../core/models/programacion.model';
import { EstadoViaje, Viaje, ViajeForm } from '../../core/models/viaje.model';
import { PedidoService } from '../../core/services/pedido.service';
import { ProgramacionService } from '../../core/services/programacion.service';
import { ViajeService } from '../../core/services/viaje.service';

@Component({
  selector: 'app-viajes',
  imports: [CommonModule, FormsModule],
  templateUrl: './viajes.html',
  styleUrl: './viajes.scss'
})
export class Viajes implements OnInit {
  private readonly viajeService = inject(ViajeService);
  private readonly programacionService = inject(ProgramacionService);
  private readonly pedidoService = inject(PedidoService);

  viajes: Viaje[] = [];
  programaciones: ProgramacionModel[] = [];
  pedidos: Pedido[] = [];
  conductores: ConductorOperacion[] = [];
  tractos: TractoOperacion[] = [];

  busqueda = '';
  filtroEstado: EstadoViaje | 'TODOS' = 'TODOS';
  cargando = false;
  guardando = false;
  mostrarNuevo = false;
  mostrarOperacion = false;
  tipoOperacion: 'INICIAR' | 'FINALIZAR' | null = null;
  viajeSeleccionado: Viaje | null = null;

  mensaje = '';
  error = '';

  nuevoViaje = {
    programacionId: null as number | null,
    observacion: ''
  };

  operacion = {
    fecha: '',
    kilometraje: null as number | null,
    observacion: ''
  };

  ngOnInit(): void {
    this.cargarDatos();
  }

  get viajesFiltrados(): Viaje[] {
    const termino = this.busqueda.trim().toLowerCase();

    return this.viajes.filter(viaje => {
      if (this.filtroEstado !== 'TODOS' && viaje.estado !== this.filtroEstado) {
        return false;
      }

      if (!termino) {
        return true;
      }

      const programacion = this.programacionPorId(viaje.programacionId);
      const pedidoId = programacion?.pedidoId ?? 0;

      return (
        `viaje-${viaje.id}`.includes(termino) ||
        `ped-${pedidoId}`.includes(termino) ||
        this.conductorNombre(programacion?.conductorId ?? 0).toLowerCase().includes(termino) ||
        this.tractoPlaca(programacion?.tractoId ?? 0).toLowerCase().includes(termino)
      );
    });
  }

  get programacionesDisponibles(): ProgramacionModel[] {
    const usadas = new Set(this.viajes.map(viaje => viaje.programacionId));
    return this.programaciones.filter(programacion =>
      programacion.estado === 'PROGRAMADA' && !usadas.has(programacion.id)
    );
  }

  get totalProgramados(): number {
    return this.viajes.filter(viaje => viaje.estado === 'PROGRAMADO').length;
  }

  get totalEnViaje(): number {
    return this.viajes.filter(viaje => viaje.estado === 'EN_VIAJE').length;
  }

  get totalFinalizados(): number {
    return this.viajes.filter(viaje => viaje.estado === 'FINALIZADO').length;
  }

  cargarDatos(): void {
    this.cargando = true;
    this.error = '';

    forkJoin({
      viajes: this.viajeService.listar(),
      programaciones: this.programacionService.listar(),
      pedidos: this.pedidoService.listar(),
      conductores: this.programacionService.listarConductores(),
      tractos: this.programacionService.listarTractos()
    }).subscribe({
      next: data => {
        this.viajes = [...data.viajes].sort((a, b) => b.id - a.id);
        this.programaciones = data.programaciones;
        this.pedidos = data.pedidos;
        this.conductores = data.conductores;
        this.tractos = data.tractos;
        this.cargando = false;
      },
      error: error => {
        this.error = this.obtenerMensajeError(
          error,
          'No se pudo cargar Viajes. Verifica viaje-service y los servicios relacionados.'
        );
        this.cargando = false;
      }
    });
  }

  abrirNuevo(): void {
    this.nuevoViaje = { programacionId: null, observacion: '' };
    this.mensaje = '';
    this.error = '';
    this.mostrarNuevo = true;
  }

  cerrarNuevo(): void {
    if (!this.guardando) {
      this.mostrarNuevo = false;
    }
  }

  crearViaje(): void {
    if (this.nuevoViaje.programacionId === null) {
      this.error = 'Selecciona una programación.';
      return;
    }

    const payload: ViajeForm = {
      programacionId: Number(this.nuevoViaje.programacionId),
      fechaInicio: null,
      fechaFin: null,
      kilometrajeInicial: null,
      kilometrajeFinal: null,
      observacion: this.nuevoViaje.observacion.trim(),
      estado: null
    };

    this.guardando = true;
    this.error = '';
    this.mensaje = '';

    this.viajeService.crear(payload).subscribe({
      next: viaje => {
        this.mensaje = `Viaje #VIA-${this.codigo(viaje.id)} generado correctamente.`;
        this.guardando = false;
        this.mostrarNuevo = false;
        this.cargarDatos();
      },
      error: error => {
        this.error = this.obtenerMensajeError(error, 'No se pudo generar el viaje.');
        this.guardando = false;
      }
    });
  }

  abrirOperacion(viaje: Viaje, tipo: 'INICIAR' | 'FINALIZAR'): void {
    this.viajeSeleccionado = viaje;
    this.tipoOperacion = tipo;
    this.operacion = {
      fecha: this.fechaHoraActual(),
      kilometraje: tipo === 'INICIAR' ? viaje.kilometrajeInicial : viaje.kilometrajeFinal,
      observacion: viaje.observacion ?? ''
    };
    this.error = '';
    this.mensaje = '';
    this.mostrarOperacion = true;
  }

  cerrarOperacion(): void {
    if (!this.guardando) {
      this.mostrarOperacion = false;
      this.viajeSeleccionado = null;
      this.tipoOperacion = null;
    }
  }

  guardarOperacion(): void {
    const viaje = this.viajeSeleccionado;

    if (!viaje || !this.tipoOperacion || this.operacion.kilometraje === null) {
      this.error = 'Completa fecha y kilometraje.';
      return;
    }

    const iniciar = this.tipoOperacion === 'INICIAR';

    const payload: ViajeForm = {
      programacionId: viaje.programacionId,
      fechaInicio: iniciar ? this.operacion.fecha : viaje.fechaInicio,
      fechaFin: iniciar ? viaje.fechaFin : this.operacion.fecha,
      kilometrajeInicial: iniciar ? Number(this.operacion.kilometraje) : viaje.kilometrajeInicial,
      kilometrajeFinal: iniciar ? viaje.kilometrajeFinal : Number(this.operacion.kilometraje),
      observacion: this.operacion.observacion.trim(),
      estado: iniciar ? 'EN_VIAJE' : 'FINALIZADO'
    };

    this.guardando = true;
    this.error = '';

    this.viajeService.actualizar(viaje.id, payload).subscribe({
      next: actualizado => {
        this.mensaje = iniciar
          ? `Viaje #VIA-${this.codigo(actualizado.id)} iniciado correctamente.`
          : `Viaje #VIA-${this.codigo(actualizado.id)} finalizado correctamente.`;
        this.guardando = false;
        this.mostrarOperacion = false;
        this.viajeSeleccionado = null;
        this.tipoOperacion = null;
        this.cargarDatos();
      },
      error: error => {
        this.error = this.obtenerMensajeError(error, 'No se pudo actualizar el viaje.');
        this.guardando = false;
      }
    });
  }

  cancelar(viaje: Viaje): void {
    if (viaje.estado !== 'PROGRAMADO' && viaje.estado !== 'EN_VIAJE') {
      return;
    }

    if (!window.confirm(`¿Cancelar el viaje #VIA-${this.codigo(viaje.id)}?`)) {
      return;
    }

    const payload: ViajeForm = {
      programacionId: viaje.programacionId,
      fechaInicio: viaje.fechaInicio,
      fechaFin: viaje.fechaFin,
      kilometrajeInicial: viaje.kilometrajeInicial,
      kilometrajeFinal: viaje.kilometrajeFinal,
      observacion: viaje.observacion ?? '',
      estado: 'CANCELADO'
    };

    this.viajeService.actualizar(viaje.id, payload).subscribe({
      next: () => {
        this.mensaje = 'Viaje cancelado correctamente.';
        this.cargarDatos();
      },
      error: error => {
        this.error = this.obtenerMensajeError(error, 'No se pudo cancelar el viaje.');
      }
    });
  }

  eliminar(viaje: Viaje): void {
    if (viaje.estado === 'PROGRAMADO' || viaje.estado === 'EN_VIAJE') {
      this.error = 'No se puede eliminar un viaje activo. Primero cancélalo.';
      return;
    }

    if (!window.confirm(`¿Eliminar definitivamente el viaje #VIA-${this.codigo(viaje.id)}?`)) {
      return;
    }

    this.viajeService.eliminar(viaje.id).subscribe({
      next: () => {
        this.mensaje = 'Viaje eliminado correctamente.';
        this.cargarDatos();
      },
      error: error => {
        this.error = this.obtenerMensajeError(error, 'No se pudo eliminar el viaje.');
      }
    });
  }

  programacionPorId(id: number): ProgramacionModel | undefined {
    return this.programaciones.find(item => item.id === id);
  }

  pedidoPorProgramacion(programacionId: number): Pedido | undefined {
    const programacion = this.programacionPorId(programacionId);
    return programacion
      ? this.pedidos.find(pedido => pedido.id === programacion.pedidoId)
      : undefined;
  }

  conductorNombre(id: number): string {
    const conductor = this.conductores.find(item => item.id === id);
    return conductor ? `${conductor.nombres} ${conductor.apellidos}` : `Conductor #${id}`;
  }

  tractoPlaca(id: number): string {
    return this.tractos.find(item => item.id === id)?.placa ?? `Tracto #${id}`;
  }

  codigo(id: number): string {
    return id.toString().padStart(4, '0');
  }

  etiquetaEstado(estado: EstadoViaje): string {
    return estado === 'EN_VIAJE' ? 'EN VIAJE' : estado;
  }

  claseEstado(estado: EstadoViaje): string {
    return estado.toLowerCase().replace('_', '-');
  }

  distancia(viaje: Viaje): number | null {
    if (viaje.kilometrajeInicial === null || viaje.kilometrajeFinal === null) {
      return null;
    }

    return viaje.kilometrajeFinal - viaje.kilometrajeInicial;
  }

  private fechaHoraActual(): string {
    const fecha = new Date();
    const offset = fecha.getTimezoneOffset();
    return new Date(fecha.getTime() - offset * 60_000).toISOString().slice(0, 16);
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
