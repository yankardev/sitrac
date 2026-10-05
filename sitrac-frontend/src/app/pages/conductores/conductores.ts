import { CommonModule } from '@angular/common';
import { Component, OnInit, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';

import { Conductor, ConductorForm } from '../../core/models/conductor.model';
import { ConductorService } from '../../core/services/conductor.service';

@Component({
  selector: 'app-conductores',
  imports: [CommonModule, FormsModule],
  templateUrl: './conductores.html',
  styleUrl: './conductores.scss'
})
export class Conductores implements OnInit {
  private readonly conductorService = inject(ConductorService);

  conductores: Conductor[] = [];
  busqueda = '';
  filtroDisponibilidad: 'TODOS' | 'DISPONIBLE' | 'OCUPADO' = 'TODOS';

  cargando = false;
  guardando = false;
  mostrarFormulario = false;
  conductorEditandoId: number | null = null;

  mensaje = '';
  error = '';

  formulario: ConductorForm = this.formularioVacio();

  ngOnInit(): void {
    this.cargarConductores();
  }

  get conductoresFiltrados(): Conductor[] {
    const termino = this.busqueda.trim().toLowerCase();

    return this.conductores.filter(conductor => {
      const coincideDisponibilidad =
        this.filtroDisponibilidad === 'TODOS' ||
        (this.filtroDisponibilidad === 'DISPONIBLE' && conductor.disponible) ||
        (this.filtroDisponibilidad === 'OCUPADO' && !conductor.disponible);

      if (!coincideDisponibilidad) {
        return false;
      }

      if (!termino) {
        return true;
      }

      return (
        conductor.dni.includes(termino) ||
        `${conductor.nombres} ${conductor.apellidos}`.toLowerCase().includes(termino) ||
        conductor.numeroLicencia.toLowerCase().includes(termino) ||
        conductor.categoriaLicencia.toLowerCase().includes(termino)
      );
    });
  }

  get totalActivos(): number {
    return this.conductores.filter(conductor => conductor.activo).length;
  }

  get totalDisponibles(): number {
    return this.conductores.filter(conductor => conductor.activo && conductor.disponible).length;
  }

  get totalOcupados(): number {
    return this.conductores.filter(conductor => conductor.activo && !conductor.disponible).length;
  }

  cargarConductores(): void {
    this.cargando = true;
    this.error = '';

    this.conductorService.listar().subscribe({
      next: conductores => {
        this.conductores = [...conductores].sort((a, b) => b.id - a.id);
        this.cargando = false;
      },
      error: error => {
        this.error = this.obtenerMensajeError(
          error,
          'No se pudo cargar la lista de conductores. Verifica que conductor-service esté ejecutándose.'
        );
        this.cargando = false;
      }
    });
  }

  abrirNuevo(): void {
    this.conductorEditandoId = null;
    this.formulario = this.formularioVacio();
    this.mensaje = '';
    this.error = '';
    this.mostrarFormulario = true;
  }

  editar(conductor: Conductor): void {
    this.conductorEditandoId = conductor.id;
    this.formulario = {
      dni: conductor.dni,
      nombres: conductor.nombres,
      apellidos: conductor.apellidos,
      numeroLicencia: conductor.numeroLicencia,
      categoriaLicencia: conductor.categoriaLicencia,
      fechaVencimientoLicencia: conductor.fechaVencimientoLicencia,
      telefono: conductor.telefono ?? '',
      disponible: conductor.disponible,
      activo: conductor.activo
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
    this.conductorEditandoId = null;
    this.formulario = this.formularioVacio();
  }

  guardar(): void {
    this.guardando = true;
    this.error = '';
    this.mensaje = '';

    const payload: ConductorForm = {
      dni: this.formulario.dni.trim(),
      nombres: this.formulario.nombres.trim(),
      apellidos: this.formulario.apellidos.trim(),
      numeroLicencia: this.formulario.numeroLicencia.trim(),
      categoriaLicencia: this.formulario.categoriaLicencia.trim(),
      fechaVencimientoLicencia: this.formulario.fechaVencimientoLicencia,
      telefono: this.formulario.telefono.trim(),
      disponible: this.formulario.disponible,
      activo: this.formulario.activo
    };

    const operacion = this.conductorEditandoId === null
      ? this.conductorService.crear({
          dni: payload.dni,
          nombres: payload.nombres,
          apellidos: payload.apellidos,
          numeroLicencia: payload.numeroLicencia,
          categoriaLicencia: payload.categoriaLicencia,
          fechaVencimientoLicencia: payload.fechaVencimientoLicencia,
          telefono: payload.telefono
        })
      : this.conductorService.actualizar(this.conductorEditandoId, payload);

    operacion.subscribe({
      next: conductor => {
        this.mensaje = this.conductorEditandoId === null
          ? `Conductor ${conductor.nombres} ${conductor.apellidos} registrado correctamente.`
          : `Conductor ${conductor.nombres} ${conductor.apellidos} actualizado correctamente.`;

        this.guardando = false;
        this.mostrarFormulario = false;
        this.conductorEditandoId = null;
        this.formulario = this.formularioVacio();
        this.cargarConductores();
      },
      error: error => {
        this.error = this.obtenerMensajeError(error, 'No se pudo guardar el conductor.');
        this.guardando = false;
      }
    });
  }

  cambiarDisponibilidad(conductor: Conductor): void {
    if (!conductor.activo) {
      this.error = 'No se puede cambiar la disponibilidad de un conductor inactivo.';
      return;
    }

    this.error = '';
    this.mensaje = '';

    this.conductorService.cambiarDisponibilidad(conductor.id, !conductor.disponible).subscribe({
      next: actualizado => {
        this.mensaje = actualizado.disponible
          ? 'Conductor marcado como disponible.'
          : 'Conductor marcado como no disponible.';
        this.cargarConductores();
      },
      error: error => {
        this.error = this.obtenerMensajeError(error, 'No se pudo cambiar la disponibilidad.');
      }
    });
  }

  eliminar(conductor: Conductor): void {
    const confirmado = window.confirm(
      `¿Eliminar al conductor "${conductor.nombres} ${conductor.apellidos}"?`
    );

    if (!confirmado) {
      return;
    }

    this.error = '';
    this.mensaje = '';

    this.conductorService.eliminar(conductor.id).subscribe({
      next: () => {
        this.mensaje = 'Conductor eliminado correctamente.';
        this.cargarConductores();
      },
      error: error => {
        this.error = this.obtenerMensajeError(error, 'No se pudo eliminar el conductor.');
      }
    });
  }

  licenciaVigente(conductor: Conductor): boolean {
    const hoy = new Date().toISOString().slice(0, 10);
    return conductor.fechaVencimientoLicencia >= hoy;
  }

  diasParaVencimiento(conductor: Conductor): number {
    const hoy = new Date();
    const vencimiento = new Date(`${conductor.fechaVencimientoLicencia}T00:00:00`);
    const diferencia = vencimiento.getTime() - hoy.getTime();
    return Math.ceil(diferencia / 86_400_000);
  }

  estadoLicencia(conductor: Conductor): string {
    const dias = this.diasParaVencimiento(conductor);

    if (dias < 0) {
      return 'VENCIDA';
    }

    if (dias <= 30) {
      return 'POR VENCER';
    }

    return 'VIGENTE';
  }

  claseLicencia(conductor: Conductor): string {
    const estado = this.estadoLicencia(conductor);

    if (estado === 'VENCIDA') {
      return 'expired';
    }

    if (estado === 'POR VENCER') {
      return 'warning';
    }

    return 'valid';
  }

  nombreCompleto(conductor: Conductor): string {
    return `${conductor.nombres} ${conductor.apellidos}`;
  }

  private formularioVacio(): ConductorForm {
    return {
      dni: '',
      nombres: '',
      apellidos: '',
      numeroLicencia: '',
      categoriaLicencia: 'AIIIC',
      fechaVencimientoLicencia: '',
      telefono: '',
      disponible: true,
      activo: true
    };
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
