import { CommonModule } from '@angular/common';
import { ChangeDetectorRef, Component, OnInit, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { finalize } from 'rxjs';

import { Cliente } from '../../core/models/cliente.model';
import {
  EstadoPedido,
  Pedido,
  PedidoForm,
  TipoCarga
} from '../../core/models/pedido.model';
import { ClienteService } from '../../core/services/cliente.service';
import { PedidoService } from '../../core/services/pedido.service';
import { confirmarAccionDestructiva } from '../../core/utils/confirmacion.util';

@Component({
  selector: 'app-pedidos',
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './pedidos.html',
  styleUrl: './pedidos.scss'
})
export class Pedidos implements OnInit {
  private readonly pedidoService = inject(PedidoService);
  private readonly clienteService = inject(ClienteService);
  private readonly cdr = inject(ChangeDetectorRef);

  readonly puntosRuta: string[] = [
    'Pacasmayo, La Libertad',
    'Trujillo, La Libertad',
    'Salaverry, La Libertad',
    'Virú, La Libertad',
    'Quiruvilca, La Libertad',
    'Chimbote, Áncash',
    'Chiclayo, Lambayeque',
    'Cajamarca, Cajamarca',
    'Piura, Piura',
    'Callao, Callao',
    'Lima, Lima'
  ];

  pedidos: Pedido[] = [];
  clientes: Cliente[] = [];

  busqueda = '';
  filtroEstado: EstadoPedido | 'TODOS' = 'TODOS';

  cargando = false;
  guardando = false;
  mostrarFormulario = false;
  pedidoEditandoId: number | null = null;

  mensaje = '';
  error = '';

  formulario: PedidoForm = this.formularioVacio();

  ngOnInit(): void {
    this.cargarDatos();
  }

  get pedidosFiltrados(): Pedido[] {
    const termino = this.busqueda.trim().toLowerCase();

    return this.pedidos.filter(pedido => {
      const coincideEstado =
        this.filtroEstado === 'TODOS' || pedido.estado === this.filtroEstado;

      if (!coincideEstado) {
        return false;
      }

      if (!termino) {
        return true;
      }

      const cliente = this.nombreCliente(pedido.clienteId).toLowerCase();

      return (
        `ped-${pedido.id}`.includes(termino) ||
        cliente.includes(termino) ||
        pedido.origen.toLowerCase().includes(termino) ||
        pedido.destino.toLowerCase().includes(termino) ||
        this.etiquetaCarga(pedido.tipoCarga).toLowerCase().includes(termino)
      );
    });
  }

  get totalRegistrados(): number {
    return this.pedidos.filter(pedido => pedido.estado === 'REGISTRADO').length;
  }

  get totalProgramados(): number {
    return this.pedidos.filter(pedido => pedido.estado === 'PROGRAMADO').length;
  }

  get totalEnViaje(): number {
    return this.pedidos.filter(pedido => pedido.estado === 'EN_VIAJE').length;
  }

  cargarDatos(): void {
    const primeraCarga = this.pedidos.length === 0;
    if (primeraCarga) {
      this.cargando = true;
    }
    this.error = '';

    this.pedidoService.listar().subscribe({
      next: pedidos => {
        this.pedidos = [...pedidos].sort((a, b) => b.id - a.id);
        this.cargando = false;
        this.cdr.detectChanges();
      },
      error: error => {
        this.error = this.obtenerMensajeError(
          error,
          'No se pudo cargar la lista de pedidos. Verifica que pedido-service esté ejecutándose.'
        );
        this.cargando = false;
        this.cdr.detectChanges();
      }
    });

    this.clienteService.listar().subscribe({
      next: clientes => {
        this.clientes = clientes;
        this.cdr.detectChanges();
      },
      error: () => {
        // Los pedidos pueden mostrarse aunque el catálogo de clientes tarde más.
      }
    });
  }

  abrirNuevo(): void {
    this.pedidoEditandoId = null;
    this.formulario = this.formularioVacio();
    this.mensaje = '';
    this.error = '';
    this.mostrarFormulario = true;
  }

  editar(pedido: Pedido): void {
    this.pedidoEditandoId = pedido.id;
    this.formulario = {
      clienteId: pedido.clienteId,
      tipoCarga: pedido.tipoCarga,
      descripcionCarga: pedido.descripcionCarga ?? '',
      toneladas: pedido.toneladas,
      origen: pedido.origen,
      destino: pedido.destino,
      fechaSolicitud: pedido.fechaSolicitud,
      estado: pedido.estado
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
    this.pedidoEditandoId = null;
    this.formulario = this.formularioVacio();
  }

  guardarPedido(): void {
    if (this.formulario.clienteId === null || this.formulario.toneladas === null) {
      this.error = 'Completa los campos obligatorios del pedido.';
      return;
    }

    if (!this.formulario.origen || !this.formulario.destino) {
      this.error = 'Selecciona el origen y el destino del pedido.';
      return;
    }

    if (this.formulario.origen === this.formulario.destino) {
      this.error = 'El origen y el destino deben ser diferentes.';
      return;
    }

    this.guardando = true;
    this.error = '';
    this.mensaje = '';

    const esNuevo = this.pedidoEditandoId === null;
    const payload: PedidoForm = {
      clienteId: Number(this.formulario.clienteId),
      tipoCarga: this.formulario.tipoCarga,
      descripcionCarga: this.formulario.descripcionCarga.trim(),
      toneladas: Number(this.formulario.toneladas),
      origen: this.formulario.origen.trim(),
      destino: this.formulario.destino.trim(),
      fechaSolicitud: this.formulario.fechaSolicitud,
      estado: esNuevo ? null : this.formulario.estado
    };

    const operacion = esNuevo
      ? this.pedidoService.crear(payload)
      : this.pedidoService.actualizar(this.pedidoEditandoId!, payload);

    operacion
      .pipe(
        finalize(() => {
          this.guardando = false;
          this.cdr.detectChanges();
        })
      )
      .subscribe({
        next: pedido => {
          this.mensaje = esNuevo
            ? `Pedido #PED-${this.codigoPedido(pedido.id)} registrado correctamente.`
            : `Pedido #PED-${this.codigoPedido(pedido.id)} actualizado correctamente.`;

          const existe = this.pedidos.some(x => x.id === pedido.id);
          this.pedidos = existe
            ? this.pedidos.map(x => x.id === pedido.id ? pedido : x)
            : [pedido, ...this.pedidos];

          this.mostrarFormulario = false;
          this.pedidoEditandoId = null;
          this.formulario = this.formularioVacio();
          this.cdr.detectChanges();
        },
        error: error => {
          this.error = this.obtenerMensajeError(error, 'No se pudo guardar el pedido.');
        }
      });
  }

  eliminar(pedido: Pedido): void {
    const confirmado = confirmarAccionDestructiva(
      `ALERTA: vas a eliminar el pedido #PED-${this.codigoPedido(pedido.id)}.`,
      `CONFIRMACIÓN FINAL: ¿Deseas eliminar definitivamente el pedido #PED-${this.codigoPedido(pedido.id)}?`
    );

    if (!confirmado) {
      return;
    }

    this.error = '';
    this.mensaje = '';

    this.pedidoService.eliminar(pedido.id).subscribe({
      next: () => {
        this.pedidos = this.pedidos.filter(x => x.id !== pedido.id);
        this.mensaje = 'Pedido eliminado correctamente.';
        this.cdr.detectChanges();
      },
      error: error => {
        this.error = this.obtenerMensajeError(error, 'No se pudo eliminar el pedido.');
      }
    });
  }

  nombreCliente(clienteId: number): string {
    return this.clientes.find(cliente => cliente.id === clienteId)?.nombreRazonSocial
      ?? `Cliente #${clienteId}`;
  }

  etiquetaCarga(tipo: TipoCarga): string {
    const etiquetas: Record<TipoCarga, string> = {
      CAL_GRANEL: 'Cal a granel',
      CEMENTO_BOLSA: 'Cemento en bolsa',
      MAQUINARIA: 'Maquinaria',
      CARGA_ANCHA: 'Carga ancha',
      ESPECIAL: 'Carga especial',
      OTRO: 'Otro'
    };

    return etiquetas[tipo];
  }

  recursoSugerido(tipo: TipoCarga): string {
    const recursos: Record<TipoCarga, string> = {
      CAL_GRANEL: 'Bombona',
      CEMENTO_BOLSA: 'Plataforma',
      MAQUINARIA: 'Cama baja',
      CARGA_ANCHA: 'Cama baja',
      ESPECIAL: 'Cama baja',
      OTRO: 'Según evaluación'
    };

    return recursos[tipo];
  }

  esPuntoRuta(punto: string): boolean {
    return this.puntosRuta.includes(punto);
  }

  etiquetaEstado(estado: EstadoPedido): string {
    const etiquetas: Record<EstadoPedido, string> = {
      REGISTRADO: 'REGISTRADO',
      PROGRAMADO: 'PROGRAMADO',
      EN_VIAJE: 'EN VIAJE',
      FINALIZADO: 'FINALIZADO',
      CANCELADO: 'CANCELADO'
    };

    return etiquetas[estado];
  }

  claseEstado(estado: EstadoPedido): string {
    return estado.toLowerCase().replace('_', '-');
  }

  codigoPedido(id: number): string {
    return id.toString().padStart(4, '0');
  }

  private formularioVacio(): PedidoForm {
    return {
      clienteId: null,
      tipoCarga: 'CEMENTO_BOLSA',
      descripcionCarga: '',
      toneladas: 35,
      origen: '',
      destino: '',
      fechaSolicitud: this.fechaHoy(),
      estado: null
    };
  }

  private fechaHoy(): string {
    const ahora = new Date();
    const offset = ahora.getTimezoneOffset();
    return new Date(ahora.getTime() - offset * 60_000).toISOString().slice(0, 10);
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
