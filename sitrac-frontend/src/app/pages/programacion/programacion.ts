import { CommonModule } from '@angular/common';
import { Component, OnInit, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { forkJoin } from 'rxjs';

import { Pedido, TipoCarga } from '../../core/models/pedido.model';
import {
  ConductorOperacion,
  EstadoProgramacion,
  Programacion,
  ProgramacionForm,
  SemirremolqueOperacion,
  TractoOperacion
} from '../../core/models/programacion.model';
import { PedidoService } from '../../core/services/pedido.service';
import { ProgramacionService } from '../../core/services/programacion.service';

@Component({
  selector: 'app-programacion',
  imports: [CommonModule, FormsModule],
  templateUrl: './programacion.html',
  styleUrl: './programacion.scss'
})
export class Programacion implements OnInit {
  private readonly programacionService = inject(ProgramacionService);
  private readonly pedidoService = inject(PedidoService);

  programaciones: Programacion[] = [];
  pedidos: Pedido[] = [];
  conductores: ConductorOperacion[] = [];
  tractos: TractoOperacion[] = [];
  semirremolques: SemirremolqueOperacion[] = [];

  busqueda = '';
  filtroEstado: EstadoProgramacion | 'TODOS' = 'TODOS';

  cargando = false;
  guardando = false;
  mostrarFormulario = false;
  programacionEditandoId: number | null = null;

  mensaje = '';
  error = '';

  formulario: ProgramacionForm = this.formularioVacio();

  ngOnInit(): void {
    this.cargarDatos();
  }

  get programacionesFiltradas(): Programacion[] {
    const termino = this.busqueda.trim().toLowerCase();

    return this.programaciones.filter(programacion => {
      if (this.filtroEstado !== 'TODOS' && programacion.estado !== this.filtroEstado) {
        return false;
      }

      if (!termino) {
        return true;
      }

      return (
        `prog-${programacion.id}`.includes(termino) ||
        this.codigoPedido(programacion.pedidoId).includes(termino) ||
        this.conductorNombre(programacion.conductorId).toLowerCase().includes(termino) ||
        this.tractoPlaca(programacion.tractoId).toLowerCase().includes(termino) ||
        this.semirremolquePlaca(programacion.semirremolqueId).toLowerCase().includes(termino)
      );
    });
  }

  get pedidosDisponibles(): Pedido[] {
    return this.pedidos.filter(pedido => pedido.estado === 'REGISTRADO');
  }

  get conductoresDisponibles(): ConductorOperacion[] {
    const hoy = new Date().toISOString().slice(0, 10);

    return this.conductores.filter(conductor =>
      conductor.activo &&
      conductor.disponible &&
      conductor.fechaVencimientoLicencia >= hoy
    );
  }

  get tractosDisponibles(): TractoOperacion[] {
    return this.tractos.filter(tracto => tracto.activo && tracto.estado === 'DISPONIBLE');
  }

  get semirremolquesDisponibles(): SemirremolqueOperacion[] {
    const pedido = this.pedidoSeleccionado();
    const tipoRequerido = pedido ? this.tipoSemirremolqueRequerido(pedido.tipoCarga) : null;

    return this.semirremolques.filter(semirremolque => {
      const disponible = semirremolque.activo && semirremolque.estado === 'DISPONIBLE';
      const compatible = !tipoRequerido || tipoRequerido === 'SEGUN_EVALUACION' || semirremolque.tipo === tipoRequerido;
      return disponible && compatible;
    });
  }

  get totalActivas(): number {
    return this.programaciones.filter(item => item.estado === 'PROGRAMADA').length;
  }

  get totalCanceladas(): number {
    return this.programaciones.filter(item => item.estado === 'CANCELADA').length;
  }

  cargarDatos(): void {
    this.cargando = true;
    this.error = '';

    forkJoin({
      programaciones: this.programacionService.listar(),
      pedidos: this.pedidoService.listar(),
      conductores: this.programacionService.listarConductores(),
      tractos: this.programacionService.listarTractos(),
      semirremolques: this.programacionService.listarSemirremolques()
    }).subscribe({
      next: data => {
        this.programaciones = [...data.programaciones].sort((a, b) => b.id - a.id);
        this.pedidos = data.pedidos;
        this.conductores = data.conductores;
        this.tractos = data.tractos;
        this.semirremolques = data.semirremolques;
        this.cargando = false;
      },
      error: error => {
        this.error = this.obtenerMensajeError(
          error,
          'No se pudo cargar Programación. Verifica pedido-service, conductor-service, flota-service y programacion-service.'
        );
        this.cargando = false;
      }
    });
  }

  abrirNueva(): void {
    this.programacionEditandoId = null;
    this.formulario = this.formularioVacio();
    this.mensaje = '';
    this.error = '';
    this.mostrarFormulario = true;
  }

  editar(programacion: Programacion): void {
    this.programacionEditandoId = programacion.id;
    this.formulario = {
      pedidoId: programacion.pedidoId,
      conductorId: programacion.conductorId,
      tractoId: programacion.tractoId,
      semirremolqueId: programacion.semirremolqueId,
      fechaProgramada: programacion.fechaProgramada.slice(0, 16),
      observacion: programacion.observacion ?? '',
      estado: programacion.estado
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
    this.programacionEditandoId = null;
    this.formulario = this.formularioVacio();
  }

  alCambiarPedido(): void {
    this.formulario.semirremolqueId = null;
  }

  guardar(): void {
    if (
      this.formulario.pedidoId === null ||
      this.formulario.conductorId === null ||
      this.formulario.tractoId === null ||
      this.formulario.semirremolqueId === null
    ) {
      this.error = 'Selecciona pedido, conductor, tracto y semirremolque.';
      return;
    }

    this.guardando = true;
    this.error = '';
    this.mensaje = '';

    const payload: ProgramacionForm = {
      pedidoId: Number(this.formulario.pedidoId),
      conductorId: Number(this.formulario.conductorId),
      tractoId: Number(this.formulario.tractoId),
      semirremolqueId: Number(this.formulario.semirremolqueId),
      fechaProgramada: this.formulario.fechaProgramada,
      observacion: this.formulario.observacion.trim(),
      estado: this.programacionEditandoId === null ? null : this.formulario.estado
    };

    const operacion = this.programacionEditandoId === null
      ? this.programacionService.crear(payload)
      : this.programacionService.actualizar(this.programacionEditandoId, payload);

    operacion.subscribe({
      next: programacion => {
        this.mensaje = this.programacionEditandoId === null
          ? `Programación #PROG-${this.codigo(programacion.id)} creada correctamente.`
          : `Programación #PROG-${this.codigo(programacion.id)} actualizada correctamente.`;
        this.guardando = false;
        this.mostrarFormulario = false;
        this.programacionEditandoId = null;
        this.formulario = this.formularioVacio();
        this.cargarDatos();
      },
      error: error => {
        this.error = this.obtenerMensajeError(error, 'No se pudo guardar la programación.');
        this.guardando = false;
      }
    });
  }

  cancelar(programacion: Programacion): void {
    if (programacion.estado !== 'PROGRAMADA') {
      return;
    }

    const confirmado = window.confirm(
      `¿Cancelar la programación #PROG-${this.codigo(programacion.id)}? Los recursos volverán a quedar disponibles.`
    );

    if (!confirmado) {
      return;
    }

    const payload: ProgramacionForm = {
      pedidoId: programacion.pedidoId,
      conductorId: programacion.conductorId,
      tractoId: programacion.tractoId,
      semirremolqueId: programacion.semirremolqueId,
      fechaProgramada: programacion.fechaProgramada,
      observacion: programacion.observacion ?? '',
      estado: 'CANCELADA'
    };

    this.error = '';
    this.mensaje = '';

    this.programacionService.actualizar(programacion.id, payload).subscribe({
      next: () => {
        this.mensaje = 'Programación cancelada y recursos liberados correctamente.';
        this.cargarDatos();
      },
      error: error => {
        this.error = this.obtenerMensajeError(error, 'No se pudo cancelar la programación.');
      }
    });
  }

  eliminar(programacion: Programacion): void {
    if (programacion.estado !== 'CANCELADA') {
      this.error = 'Solo se pueden eliminar programaciones canceladas.';
      return;
    }

    const confirmado = window.confirm(
      `¿Eliminar definitivamente la programación #PROG-${this.codigo(programacion.id)}?`
    );

    if (!confirmado) {
      return;
    }

    this.programacionService.eliminar(programacion.id).subscribe({
      next: () => {
        this.mensaje = 'Programación eliminada correctamente.';
        this.cargarDatos();
      },
      error: error => {
        this.error = this.obtenerMensajeError(error, 'No se pudo eliminar la programación.');
      }
    });
  }

  pedidoSeleccionado(): Pedido | undefined {
    if (this.formulario.pedidoId === null) {
      return undefined;
    }

    return this.pedidos.find(pedido => pedido.id === Number(this.formulario.pedidoId));
  }

  pedidoPorId(id: number): Pedido | undefined {
    return this.pedidos.find(pedido => pedido.id === id);
  }

  conductorNombre(id: number): string {
    const conductor = this.conductores.find(item => item.id === id);
    return conductor ? `${conductor.nombres} ${conductor.apellidos}` : `Conductor #${id}`;
  }

  tractoPlaca(id: number): string {
    const tracto = this.tractos.find(item => item.id === id);
    return tracto?.placa ?? `Tracto #${id}`;
  }

  semirremolquePlaca(id: number): string {
    const semirremolque = this.semirremolques.find(item => item.id === id);
    return semirremolque?.placa ?? `Semirremolque #${id}`;
  }

  semirremolqueTipo(id: number): string {
    const semirremolque = this.semirremolques.find(item => item.id === id);
    return semirremolque ? this.etiquetaTipoSemirremolque(semirremolque.tipo) : 'Sin información';
  }

  tipoSemirremolqueRequerido(tipoCarga: TipoCarga): string {
    const reglas: Record<TipoCarga, string> = {
      CAL_GRANEL: 'BOMBONA',
      CEMENTO_BOLSA: 'PLATAFORMA',
      MAQUINARIA: 'CAMA_BAJA',
      CARGA_ANCHA: 'CAMA_BAJA',
      ESPECIAL: 'CAMA_BAJA',
      OTRO: 'SEGUN_EVALUACION'
    };

    return reglas[tipoCarga];
  }

  etiquetaTipoSemirremolque(tipo: string): string {
    return tipo.replaceAll('_', ' ');
  }

  codigo(id: number): string {
    return id.toString().padStart(4, '0');
  }

  codigoPedido(id: number): string {
    return `PED-${id.toString().padStart(4, '0')}`;
  }

  private formularioVacio(): ProgramacionForm {
    return {
      pedidoId: null,
      conductorId: null,
      tractoId: null,
      semirremolqueId: null,
      fechaProgramada: this.fechaInicial(),
      observacion: '',
      estado: null
    };
  }

  private fechaInicial(): string {
    const fecha = new Date(Date.now() + 60 * 60 * 1000);
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
