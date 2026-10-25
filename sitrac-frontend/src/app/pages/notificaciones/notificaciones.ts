import { CommonModule } from '@angular/common';
import { ChangeDetectorRef, Component, OnInit, inject } from '@angular/core';
import { finalize } from 'rxjs';

import { EstadoNotificacion, RegistroNotificacion } from '../../core/models/notificacion.model';
import { NotificacionService } from '../../core/services/notificacion.service';

@Component({
  selector: 'app-notificaciones',
  imports: [CommonModule],
  templateUrl: './notificaciones.html',
  styleUrl: './notificaciones.scss'
})
export class Notificaciones implements OnInit {
  private readonly notificacionService = inject(NotificacionService);
  private readonly cdr = inject(ChangeDetectorRef);

  registros: RegistroNotificacion[] = [];
  cargando = false;
  error = '';

  ngOnInit(): void {
    this.cargar();
  }

  get totalEnviadas(): number {
    return this.registros.filter(item => item.estado === 'ENVIADA').length;
  }

  get totalSimuladas(): number {
    return this.registros.filter(item => item.estado === 'SIMULADA').length;
  }

  get totalErrores(): number {
    return this.registros.filter(item => item.estado === 'ERROR' || item.estado === 'OMITIDA').length;
  }

  cargar(): void {
    this.cargando = true;
    this.error = '';

    this.notificacionService.listar()
      .pipe(finalize(() => {
        this.cargando = false;
        this.cdr.detectChanges();
      }))
      .subscribe({
        next: registros => {
          this.registros = registros;
        },
        error: error => {
          this.error = error?.error?.message || error?.error?.error || 'No se pudo cargar la evidencia de notificaciones.';
        }
      });
  }

  etiquetaEvento(tipo: string): string {
    if (tipo === 'ABASTECIMIENTO_CONFIRMADO') {
      return 'Abastecimiento confirmado';
    }
    if (tipo === 'VIAJE_FINALIZADO') {
      return 'Viaje finalizado';
    }
    return tipo.replaceAll('_', ' ');
  }

  etiquetaEstado(estado: EstadoNotificacion): string {
    switch (estado) {
      case 'ENVIADA': return 'Enviada';
      case 'SIMULADA': return 'Simulada';
      case 'OMITIDA': return 'Omitida';
      case 'ERROR': return 'Error';
    }
  }

  claseEstado(estado: EstadoNotificacion): string {
    return estado.toLowerCase();
  }
}
