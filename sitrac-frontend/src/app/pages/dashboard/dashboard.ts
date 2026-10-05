import { CommonModule } from '@angular/common';
import { Component, OnInit, inject } from '@angular/core';
import { RouterLink } from '@angular/router';
import { catchError, forkJoin, of } from 'rxjs';

import { AbastecimientoCombustible } from '../../core/models/combustible.model';
import { Pedido, TipoCarga } from '../../core/models/pedido.model';
import {
  ConductorOperacion,
  Programacion as ProgramacionModel,
  SemirremolqueOperacion,
  TractoOperacion
} from '../../core/models/programacion.model';
import { Viaje } from '../../core/models/viaje.model';
import { ClienteService } from '../../core/services/cliente.service';
import { CombustibleService } from '../../core/services/combustible.service';
import { MantenimientoService } from '../../core/services/mantenimiento.service';
import { PedidoService } from '../../core/services/pedido.service';
import { ProgramacionService } from '../../core/services/programacion.service';
import { SommaService } from '../../core/services/somma.service';
import { ViajeService } from '../../core/services/viaje.service';

interface ActividadDashboard {
  tipo: 'viaje' | 'programacion' | 'combustible';
  titulo: string;
  detalle: string;
  fecha: string;
  icono: string;
  clase: string;
}

@Component({
  selector: 'app-dashboard',
  imports: [CommonModule, RouterLink],
  templateUrl: './dashboard.html',
  styleUrl: './dashboard.scss'
})
export class Dashboard implements OnInit {
  private readonly pedidoService = inject(PedidoService);
  private readonly programacionService = inject(ProgramacionService);
  private readonly viajeService = inject(ViajeService);
  private readonly combustibleService = inject(CombustibleService);
  private readonly clienteService = inject(ClienteService);
  private readonly mantenimientoService = inject(MantenimientoService);
  private readonly sommaService = inject(SommaService);

  pedidos: Pedido[] = [];
  programaciones: ProgramacionModel[] = [];
  viajes: Viaje[] = [];
  conductores: ConductorOperacion[] = [];
  tractos: TractoOperacion[] = [];
  semirremolques: SemirremolqueOperacion[] = [];
  abastecimientos: AbastecimientoCombustible[] = [];
  actividades: ActividadDashboard[] = [];

  cargando = false;
  serviciosConError: string[] = [];
  actualizadoEn: Date | null = null;

  ngOnInit(): void {
    this.cargarDashboard();
    this.precargarModulosSecundarios();
  }

  get pedidosActivos(): number {
    return this.pedidos.filter(pedido =>
      pedido.estado !== 'FINALIZADO' && pedido.estado !== 'CANCELADO'
    ).length;
  }

  get viajesEnCurso(): number {
    return this.viajes.filter(viaje => viaje.estado === 'EN_VIAJE').length;
  }

  get flotaTotal(): number {
    return [...this.tractos, ...this.semirremolques]
      .filter(unidad => unidad.activo && unidad.estado !== 'INACTIVO').length;
  }

  get flotaDisponible(): number {
    return [...this.tractos, ...this.semirremolques]
      .filter(unidad => unidad.activo && unidad.estado === 'DISPONIBLE').length;
  }

  get flotaAsignada(): number {
    return [...this.tractos, ...this.semirremolques]
      .filter(unidad => unidad.activo && unidad.estado === 'ASIGNADO').length;
  }

  get flotaMantenimiento(): number {
    return [...this.tractos, ...this.semirremolques]
      .filter(unidad => unidad.activo && unidad.estado === 'MANTENIMIENTO').length;
  }

  get porcentajeDisponible(): number {
    if (this.flotaTotal === 0) {
      return 0;
    }
    return Math.round((this.flotaDisponible / this.flotaTotal) * 100);
  }

  get operacionesRecientes(): ProgramacionModel[] {
    return [...this.programaciones]
      .sort((a, b) => b.id - a.id)
      .slice(0, 5);
  }

  get graficoFlota(): string {
    const total = this.flotaTotal;
    if (total === 0) {
      return 'conic-gradient(#e7edf0 0 100%)';
    }

    const disponible = (this.flotaDisponible / total) * 100;
    const asignada = disponible + (this.flotaAsignada / total) * 100;

    return `conic-gradient(
      #20a878 0 ${disponible}%,
      #f58a28 ${disponible}% ${asignada}%,
      #da5c5c ${asignada}% 100%
    )`;
  }

  cargarDashboard(): void {
    this.cargando = true;
    this.serviciosConError = [];

    forkJoin({
      pedidos: this.pedidoService.listar().pipe(
        catchError(() => {
          this.serviciosConError.push('Pedidos');
          return of([] as Pedido[]);
        })
      ),
      programaciones: this.programacionService.listar().pipe(
        catchError(() => {
          this.serviciosConError.push('Programación');
          return of([] as ProgramacionModel[]);
        })
      ),
      viajes: this.viajeService.listar().pipe(
        catchError(() => {
          this.serviciosConError.push('Viajes');
          return of([] as Viaje[]);
        })
      ),
      conductores: this.programacionService.listarConductores().pipe(
        catchError(() => {
          this.serviciosConError.push('Conductores');
          return of([] as ConductorOperacion[]);
        })
      ),
      tractos: this.programacionService.listarTractos().pipe(
        catchError(() => {
          this.serviciosConError.push('Tractos');
          return of([] as TractoOperacion[]);
        })
      ),
      semirremolques: this.programacionService.listarSemirremolques().pipe(
        catchError(() => {
          this.serviciosConError.push('Semirremolques');
          return of([] as SemirremolqueOperacion[]);
        })
      ),
      combustible: this.combustibleService.listar().pipe(
        catchError(() => {
          this.serviciosConError.push('Combustible');
          return of([] as AbastecimientoCombustible[]);
        })
      )
    }).subscribe({
      next: data => {
        this.pedidos = data.pedidos;
        this.programaciones = data.programaciones;
        this.viajes = data.viajes;
        this.conductores = data.conductores;
        this.tractos = data.tractos;
        this.semirremolques = data.semirremolques;
        this.abastecimientos = data.combustible;
        this.actividades = this.generarActividad();
        this.actualizadoEn = new Date();
        this.cargando = false;
      },
      error: () => {
        this.cargando = false;
      }
    });
  }

  pedidoPorId(id: number): Pedido | undefined {
    return this.pedidos.find(pedido => pedido.id === id);
  }

  rutaPedido(id: number): string {
    const pedido = this.pedidoPorId(id);
    return pedido ? `${pedido.origen} → ${pedido.destino}` : 'Ruta no disponible';
  }

  tipoCargaPedido(id: number): string {
    const pedido = this.pedidoPorId(id);
    return pedido ? this.etiquetaCarga(pedido.tipoCarga) : 'Sin detalle';
  }

  conductorNombre(id: number): string {
    const conductor = this.conductores.find(item => item.id === id);
    return conductor ? `${conductor.nombres} ${conductor.apellidos}` : `Conductor #${id}`;
  }

  tractoPlaca(id: number): string {
    return this.tractos.find(item => item.id === id)?.placa ?? `Tracto #${id}`;
  }

  codigoPedido(id: number): string {
    return `PED-${id.toString().padStart(4, '0')}`;
  }

  estadoOperacion(programacion: ProgramacionModel): string {
    const viaje = this.viajes.find(item => item.programacionId === programacion.id);
    if (viaje) {
      return viaje.estado;
    }
    return programacion.estado === 'CANCELADA' ? 'CANCELADO' : 'PROGRAMADO';
  }

  claseEstado(estado: string): string {
    if (estado === 'EN_VIAJE') {
      return 'in-route';
    }
    if (estado === 'FINALIZADO') {
      return 'completed';
    }
    if (estado === 'CANCELADO') {
      return 'cancelled';
    }
    return 'programmed';
  }

  etiquetaEstado(estado: string): string {
    return estado.replaceAll('_', ' ');
  }

  private etiquetaCarga(tipo: TipoCarga): string {
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

  private generarActividad(): ActividadDashboard[] {
    const actividades: ActividadDashboard[] = [];

    for (const viaje of this.viajes) {
      const fecha = viaje.fechaFin ?? viaje.fechaInicio;
      if (!fecha) {
        continue;
      }

      actividades.push({
        tipo: 'viaje',
        titulo: viaje.estado === 'FINALIZADO' ? 'Viaje finalizado' : 'Viaje en curso',
        detalle: `Viaje #${viaje.id} · ${this.codigoProgramacion(viaje.programacionId)}`,
        fecha,
        icono: '✓',
        clase: 'green-bg'
      });
    }

    for (const programacion of this.programaciones) {
      actividades.push({
        tipo: 'programacion',
        titulo: programacion.estado === 'CANCELADA' ? 'Programación cancelada' : 'Programación registrada',
        detalle: `${this.codigoProgramacion(programacion.id)} · ${this.codigoPedido(programacion.pedidoId)}`,
        fecha: programacion.fechaProgramada,
        icono: '+',
        clase: 'orange-bg'
      });
    }

    for (const abastecimiento of this.abastecimientos) {
      actividades.push({
        tipo: 'combustible',
        titulo: 'Abastecimiento registrado',
        detalle: `${abastecimiento.cantidadGalones} gal · ${this.tractoPlaca(abastecimiento.tractoId)}`,
        fecha: abastecimiento.fechaHora,
        icono: '◆',
        clase: 'blue-bg'
      });
    }

    return actividades
      .sort((a, b) => new Date(b.fecha).getTime() - new Date(a.fecha).getTime())
      .slice(0, 4);
  }

  private codigoProgramacion(id: number): string {
    return `PROG-${id.toString().padStart(4, '0')}`;
  }

  private precargarModulosSecundarios(): void {
    // Estas peticiones no bloquean el dashboard. Alimentan la caché global para
    // que Clientes, Mantenimiento y SOMMA abran más rápido al navegar.
    this.clienteService.listar().pipe(catchError(() => of([]))).subscribe();
    this.mantenimientoService.listar().pipe(catchError(() => of([]))).subscribe();
    this.sommaService.listar().pipe(catchError(() => of([]))).subscribe();
  }
}
