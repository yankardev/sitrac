import { CommonModule } from '@angular/common';
import { ChangeDetectorRef, Component, OnInit, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { finalize, forkJoin } from 'rxjs';

import { ConfirmarSolicitudAbastecimiento, EstadoSolicitudAbastecimiento, SolicitudAbastecimiento } from '../../core/models/combustible.model';
import { ConductorOperacion, TractoOperacion } from '../../core/models/programacion.model';
import { CombustibleService } from '../../core/services/combustible.service';
import { ProgramacionService } from '../../core/services/programacion.service';

@Component({ selector: 'app-combustible', imports: [CommonModule, FormsModule], templateUrl: './combustible.html', styleUrl: './combustible.scss' })
export class Combustible implements OnInit {
  private readonly service = inject(CombustibleService);
  private readonly programacionService = inject(ProgramacionService);
  private readonly cdr = inject(ChangeDetectorRef);

  solicitudes: SolicitudAbastecimiento[] = [];
  conductores: ConductorOperacion[] = [];
  tractos: TractoOperacion[] = [];
  busqueda = '';
  filtroEstado: EstadoSolicitudAbastecimiento | 'TODOS' = 'TODOS';
  cargando = false;
  guardando = false;
  mostrarFormulario = false;
  seleccionada: SolicitudAbastecimiento | null = null;
  mensaje = '';
  error = '';
  formulario: ConfirmarSolicitudAbastecimiento = { puntoAbastecimiento: '', cantidadGalones: 0, observacion: '' };

  ngOnInit(): void { this.cargar(); }

  get filtradas(): SolicitudAbastecimiento[] {
    const termino = this.busqueda.trim().toLowerCase();
    return this.solicitudes.filter(item => {
      if (this.filtroEstado !== 'TODOS' && item.estado !== this.filtroEstado) return false;
      if (!termino) return true;
      return `sol-${item.id}`.includes(termino) || `prog-${item.programacionId}`.includes(termino) || this.conductorNombre(item.conductorId).toLowerCase().includes(termino) || this.tractoPlaca(item.tractoId).toLowerCase().includes(termino) || (item.puntoAbastecimiento ?? '').toLowerCase().includes(termino);
    });
  }
  get pendientes(): number { return this.solicitudes.filter(s => s.estado === 'PENDIENTE').length; }
  get confirmadas(): number { return this.solicitudes.filter(s => s.estado === 'CONFIRMADA').length; }
  get galonesConfirmados(): number { return this.solicitudes.filter(s => s.estado === 'CONFIRMADA').reduce((t,s)=>t+Number(s.cantidadGalones??0),0); }

  cargar(): void {
    this.cargando = true; this.error = '';
    forkJoin({ solicitudes: this.service.listarSolicitudes(), conductores: this.programacionService.listarConductores(), tractos: this.programacionService.listarTractos() }).subscribe({
      next: data => { this.solicitudes=[...data.solicitudes].sort((a,b)=>b.id-a.id); this.conductores=data.conductores; this.tractos=data.tractos; this.cargando=false; this.cdr.detectChanges(); },
      error: error => { this.error=this.mensajeError(error,'No se pudieron cargar las solicitudes de combustible.'); this.cargando=false; this.cdr.detectChanges(); }
    });
  }

  abrirConfirmacion(item: SolicitudAbastecimiento): void {
    if (item.estado === 'CONFIRMADA') return;
    this.seleccionada=item;
    this.formulario={ puntoAbastecimiento:item.puntoAbastecimiento??'', cantidadGalones:Number(item.cantidadGalones??0), observacion:item.observacion??'' };
    this.mensaje=''; this.error=''; this.mostrarFormulario=true;
  }
  cerrar(): void { if(this.guardando)return; this.mostrarFormulario=false; this.seleccionada=null; }
  confirmar(): void {
    if(!this.seleccionada)return;
    if(!this.formulario.puntoAbastecimiento.trim()){this.error='Indica el punto de abastecimiento.';return;}
    if(!this.formulario.cantidadGalones || this.formulario.cantidadGalones<=0){this.error='La cantidad de galones debe ser mayor que cero.';return;}
    this.guardando=true; this.error='';
    this.service.confirmarSolicitud(this.seleccionada.id,{...this.formulario,puntoAbastecimiento:this.formulario.puntoAbastecimiento.trim(),observacion:this.formulario.observacion.trim()}).pipe(finalize(()=>{this.guardando=false;this.cdr.detectChanges();})).subscribe({
      next: actualizada => { this.solicitudes=this.solicitudes.map(s=>s.id===actualizada.id?actualizada:s); this.mensaje=`Solicitud #SOL-${this.codigo(actualizada.id)} confirmada. Se generó el evento de notificación al conductor.`; this.mostrarFormulario=false; this.seleccionada=null; },
      error: error => { this.error=this.mensajeError(error,'No se pudo confirmar el abastecimiento.'); }
    });
  }
  codigo(id:number):string{return id.toString().padStart(4,'0');}
  codigoProgramacion(id:number):string{return `PROG-${id.toString().padStart(4,'0')}`;}
  conductorNombre(id:number):string{const c=this.conductores.find(x=>x.id===id);return c?`${c.nombres} ${c.apellidos}`:`Conductor #${id}`;}
  tractoPlaca(id:number):string{return this.tractos.find(x=>x.id===id)?.placa??`Tracto #${id}`;}
  etiquetaEstado(e:EstadoSolicitudAbastecimiento):string{return e==='PENDIENTE'?'Pendiente':'Confirmada';}
  private mensajeError(error:any,fallback:string):string{const r=error?.error;if(typeof r==='string'&&r.trim())return r;if(r?.error)return r.error;if(r?.message)return r.message;if(r?.campos){const m=Object.values(r.campos)[0];if(typeof m==='string')return m;}return fallback;}
}
