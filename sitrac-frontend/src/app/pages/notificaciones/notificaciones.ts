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

  get totalConductor(): number {
    return this.registros.filter(item => item.tipoEvento === 'ABASTECIMIENTO_CONFIRMADO').length;
  }

  get totalCliente(): number {
    return this.registros.filter(item => item.tipoEvento === 'VIAJE_FINALIZADO').length;
  }

  get totalCorrectas(): number {
    return this.registros.filter(item => item.estado === 'ENVIADA' || item.estado === 'SIMULADA').length;
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

  comunicacion(registro: RegistroNotificacion): string {
    if (registro.tipoEvento === 'ABASTECIMIENTO_CONFIRMADO') {
      return 'Conductor · abastecimiento confirmado';
    }
    if (registro.tipoEvento === 'VIAJE_FINALIZADO') {
      return 'Cliente · servicio finalizado';
    }
    return registro.tipoEvento.replaceAll('_', ' ');
  }

  destinatarioTipo(registro: RegistroNotificacion): string {
    if (registro.tipoEvento === 'ABASTECIMIENTO_CONFIRMADO') return 'CONDUCTOR';
    if (registro.tipoEvento === 'VIAJE_FINALIZADO') return 'CLIENTE';
    return 'DESTINATARIO';
  }

  resultado(registro: RegistroNotificacion): string {
    if (registro.tipoEvento === 'ABASTECIMIENTO_CONFIRMADO') {
      return 'Se generó la comunicación con el punto de abastecimiento y los galones autorizados.';
    }
    if (registro.tipoEvento === 'VIAJE_FINALIZADO') {
      return 'Se generó la comunicación de cierre y la constancia PDF para el cliente.';
    }
    return registro.detalle;
  }

  etiquetaEstado(estado: EstadoNotificacion): string {
    switch (estado) {
      case 'ENVIADA': return 'Correo enviado';
      case 'SIMULADA': return 'Generada · modo demo';
      case 'OMITIDA': return 'No enviada';
      case 'ERROR': return 'Error';
    }
  }

  claseEstado(estado: EstadoNotificacion): string {
    return estado.toLowerCase();
  }
}
