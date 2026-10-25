import { CommonModule } from '@angular/common';
import { ChangeDetectorRef, Component, OnInit, inject } from '@angular/core';
import { RouterLink } from '@angular/router';
import { catchError, forkJoin, of } from 'rxjs';

import { SolicitudAbastecimiento } from '../../core/models/combustible.model';
import { Pedido, TipoCarga } from '../../core/models/pedido.model';
import { ConductorOperacion, Programacion as ProgramacionModel, SemirremolqueOperacion, TractoOperacion } from '../../core/models/programacion.model';
import { Viaje } from '../../core/models/viaje.model';
import { CombustibleService } from '../../core/services/combustible.service';
import { PedidoService } from '../../core/services/pedido.service';
import { ProgramacionService } from '../../core/services/programacion.service';
import { ViajeService } from '../../core/services/viaje.service';

interface ActividadDashboard { tipo: 'viaje' | 'programacion' | 'combustible'; titulo: string; detalle: string; fecha: string; icono: string; clase: string; }

@Component({ selector: 'app-dashboard', imports: [CommonModule, RouterLink], templateUrl: './dashboard.html', styleUrl: './dashboard.scss' })
export class Dashboard implements OnInit {
  private readonly pedidoService = inject(PedidoService);
  private readonly programacionService = inject(ProgramacionService);
  private readonly viajeService = inject(ViajeService);
  private readonly combustibleService = inject(CombustibleService);
  private readonly cdr = inject(ChangeDetectorRef);

  pedidos: Pedido[] = []; programaciones: ProgramacionModel[] = []; viajes: Viaje[] = []; conductores: ConductorOperacion[] = []; tractos: TractoOperacion[] = []; semirremolques: SemirremolqueOperacion[] = []; solicitudes: SolicitudAbastecimiento[] = []; actividades: ActividadDashboard[] = [];
  cargando = false; serviciosConError: string[] = []; actualizadoEn: Date | null = null;

  ngOnInit(): void { this.cargarDashboard(); }
  get pedidosActivos(): number { return this.pedidos.filter(p => p.estado !== 'FINALIZADO' && p.estado !== 'CANCELADO').length; }
  get viajesEnCurso(): number { return this.viajes.filter(v => v.estado === 'EN_VIAJE').length; }
  get solicitudesPendientes(): number { return this.solicitudes.filter(s => s.estado === 'PENDIENTE').length; }
  get flotaTotal(): number { return [...this.tractos, ...this.semirremolques].filter(u => u.activo && u.estado !== 'INACTIVO').length; }
  get flotaDisponible(): number { return [...this.tractos, ...this.semirremolques].filter(u => u.activo && u.estado === 'DISPONIBLE').length; }
  get flotaAsignada(): number { return [...this.tractos, ...this.semirremolques].filter(u => u.activo && u.estado === 'ASIGNADO').length; }
  get flotaNoDisponible(): number { return Math.max(this.flotaTotal - this.flotaDisponible - this.flotaAsignada, 0); }
  get porcentajeDisponible(): number { return this.flotaTotal === 0 ? 0 : Math.round((this.flotaDisponible / this.flotaTotal) * 100); }
  get operacionesRecientes(): ProgramacionModel[] { return [...this.programaciones].sort((a,b) => b.id-a.id).slice(0,5); }
  get graficoFlota(): string { const total=this.flotaTotal; if(total===0)return 'conic-gradient(#e7edf0 0 100%)'; const disponible=(this.flotaDisponible/total)*100; const asignada=disponible+(this.flotaAsignada/total)*100; return `conic-gradient(#20a878 0 ${disponible}%, #f58a28 ${disponible}% ${asignada}%, #da5c5c ${asignada}% 100%)`; }

  cargarDashboard(): void {
    this.cargando=true; this.serviciosConError=[];
    forkJoin({
      pedidos:this.pedidoService.listar().pipe(catchError(()=>{this.serviciosConError.push('Pedidos');return of([] as Pedido[]);})),
      programaciones:this.programacionService.listar().pipe(catchError(()=>{this.serviciosConError.push('Programación');return of([] as ProgramacionModel[]);})),
      viajes:this.viajeService.listar().pipe(catchError(()=>{this.serviciosConError.push('Viajes');return of([] as Viaje[]);})),
      conductores:this.programacionService.listarConductores().pipe(catchError(()=>{this.serviciosConError.push('Conductores');return of([] as ConductorOperacion[]);})),
      tractos:this.programacionService.listarTractos().pipe(catchError(()=>{this.serviciosConError.push('Tractos');return of([] as TractoOperacion[]);})),
      semirremolques:this.programacionService.listarSemirremolques().pipe(catchError(()=>{this.serviciosConError.push('Semirremolques');return of([] as SemirremolqueOperacion[]);})),
      solicitudes:this.combustibleService.listarSolicitudes().pipe(catchError(()=>{this.serviciosConError.push('Combustible');return of([] as SolicitudAbastecimiento[]);}))
    }).subscribe({next:data=>{this.pedidos=data.pedidos;this.programaciones=data.programaciones;this.viajes=data.viajes;this.conductores=data.conductores;this.tractos=data.tractos;this.semirremolques=data.semirremolques;this.solicitudes=data.solicitudes;this.actividades=this.generarActividad();this.actualizadoEn=new Date();this.cargando=false;this.cdr.detectChanges();},error:()=>{this.cargando=false;this.cdr.detectChanges();}});
  }
  pedidoPorId(id:number):Pedido|undefined{return this.pedidos.find(p=>p.id===id);} rutaPedido(id:number):string{const p=this.pedidoPorId(id);return p?`${p.origen} → ${p.destino}`:'Ruta no disponible';} tipoCargaPedido(id:number):string{const p=this.pedidoPorId(id);return p?this.etiquetaCarga(p.tipoCarga):'Sin detalle';} conductorNombre(id:number):string{const c=this.conductores.find(x=>x.id===id);return c?`${c.nombres} ${c.apellidos}`:`Conductor #${id}`;} tractoPlaca(id:number):string{return this.tractos.find(x=>x.id===id)?.placa??`Tracto #${id}`;} codigoPedido(id:number):string{return `PED-${id.toString().padStart(4,'0')}`;}
  estadoOperacion(p:ProgramacionModel):string{const v=this.viajes.find(x=>x.programacionId===p.id);if(v)return v.estado;return p.estado==='CANCELADA'?'CANCELADO':'PROGRAMADO';} claseEstado(e:string):string{if(e==='EN_VIAJE')return'in-route';if(e==='FINALIZADO')return'completed';if(e==='CANCELADO')return'cancelled';return'programmed';} etiquetaEstado(e:string):string{return e.replaceAll('_',' ');}
  private etiquetaCarga(tipo:TipoCarga):string{const etiquetas:Record<TipoCarga,string>={CAL_GRANEL:'Cal a granel',CEMENTO_BOLSA:'Cemento en bolsa',MAQUINARIA:'Maquinaria',CARGA_ANCHA:'Carga ancha',ESPECIAL:'Carga especial',OTRO:'Otro'};return etiquetas[tipo];}
  private generarActividad():ActividadDashboard[]{const a:ActividadDashboard[]=[];for(const v of this.viajes){const f=v.fechaFin??v.fechaInicio;if(!f)continue;a.push({tipo:'viaje',titulo:v.estado==='FINALIZADO'?'Viaje finalizado':'Viaje en curso',detalle:`Viaje #${v.id} · PROG-${v.programacionId.toString().padStart(4,'0')}`,fecha:f,icono:'✓',clase:'green-bg'});}for(const p of this.programaciones){a.push({tipo:'programacion',titulo:p.estado==='CANCELADA'?'Programación cancelada':'Programación registrada',detalle:`PROG-${p.id.toString().padStart(4,'0')} · ${this.codigoPedido(p.pedidoId)}`,fecha:p.fechaProgramada,icono:'+',clase:'orange-bg'});}for(const s of this.solicitudes){a.push({tipo:'combustible',titulo:s.estado==='CONFIRMADA'?'Abastecimiento confirmado':'Solicitud de combustible',detalle:`PROG-${s.programacionId.toString().padStart(4,'0')} · ${this.tractoPlaca(s.tractoId)}`,fecha:s.fechaConfirmacion??s.fechaGeneracion,icono:'◆',clase:'blue-bg'});}return a.sort((x,y)=>new Date(y.fecha).getTime()-new Date(x.fecha).getTime()).slice(0,4);}
}
