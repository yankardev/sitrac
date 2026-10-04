import { CommonModule } from '@angular/common';
import { Component, OnInit, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';

import { Cliente, ClienteForm, TipoDocumento } from '../../core/models/cliente.model';
import { ClienteService } from '../../core/services/cliente.service';

@Component({
  selector: 'app-clientes',
  imports: [CommonModule, FormsModule],
  templateUrl: './clientes.html',
  styleUrl: './clientes.scss'
})
export class Clientes implements OnInit {
  private readonly clienteService = inject(ClienteService);

  clientes: Cliente[] = [];
  busqueda = '';
  cargando = false;
  guardando = false;
  mostrarFormulario = false;
  clienteEditandoId: number | null = null;
  mensaje = '';
  error = '';

  formulario: ClienteForm = this.formularioVacio();

  ngOnInit(): void {
    this.cargarClientes();
  }

  get clientesFiltrados(): Cliente[] {
    const termino = this.busqueda.trim().toLowerCase();

    if (!termino) {
      return this.clientes;
    }

    return this.clientes.filter(cliente =>
      cliente.nombreRazonSocial.toLowerCase().includes(termino) ||
      cliente.numeroDocumento.toLowerCase().includes(termino) ||
      (cliente.email ?? '').toLowerCase().includes(termino)
    );
  }

  get totalActivos(): number {
    return this.clientes.filter(cliente => cliente.activo).length;
  }

  get totalInactivos(): number {
    return this.clientes.filter(cliente => !cliente.activo).length;
  }

  cargarClientes(): void {
    this.cargando = true;
    this.error = '';

    this.clienteService.listar().subscribe({
      next: clientes => {
        this.clientes = clientes;
        this.cargando = false;
      },
      error: error => {
        this.error = this.obtenerMensajeError(
          error,
          'No se pudo cargar la lista de clientes. Verifica que cliente-service esté ejecutándose.'
        );
        this.cargando = false;
      }
    });
  }

  abrirNuevo(): void {
    this.clienteEditandoId = null;
    this.formulario = this.formularioVacio();
    this.mensaje = '';
    this.error = '';
    this.mostrarFormulario = true;
  }

  editar(cliente: Cliente): void {
    this.clienteEditandoId = cliente.id;
    this.formulario = {
      tipoDocumento: cliente.tipoDocumento,
      numeroDocumento: cliente.numeroDocumento,
      nombreRazonSocial: cliente.nombreRazonSocial,
      telefono: cliente.telefono ?? '',
      email: cliente.email ?? '',
      direccion: cliente.direccion ?? '',
      activo: cliente.activo
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
    this.clienteEditandoId = null;
    this.formulario = this.formularioVacio();
  }

  guardarCliente(): void {
    this.guardando = true;
    this.error = '';
    this.mensaje = '';

    const payload: ClienteForm = {
      ...this.formulario,
      numeroDocumento: this.formulario.numeroDocumento.trim(),
      nombreRazonSocial: this.formulario.nombreRazonSocial.trim(),
      telefono: this.formulario.telefono.trim(),
      email: this.formulario.email.trim(),
      direccion: this.formulario.direccion.trim()
    };

    const operacion = this.clienteEditandoId === null
      ? this.clienteService.crear({
          tipoDocumento: payload.tipoDocumento,
          numeroDocumento: payload.numeroDocumento,
          nombreRazonSocial: payload.nombreRazonSocial,
          telefono: payload.telefono,
          email: payload.email,
          direccion: payload.direccion
        })
      : this.clienteService.actualizar(this.clienteEditandoId, payload);

    operacion.subscribe({
      next: cliente => {
        this.mensaje = this.clienteEditandoId === null
          ? `Cliente ${cliente.nombreRazonSocial} registrado correctamente.`
          : `Cliente ${cliente.nombreRazonSocial} actualizado correctamente.`;

        this.guardando = false;
        this.mostrarFormulario = false;
        this.clienteEditandoId = null;
        this.formulario = this.formularioVacio();
        this.cargarClientes();
      },
      error: error => {
        this.error = this.obtenerMensajeError(error, 'No se pudo guardar el cliente.');
        this.guardando = false;
      }
    });
  }

  eliminar(cliente: Cliente): void {
    const confirmado = window.confirm(
      `¿Eliminar al cliente "${cliente.nombreRazonSocial}"?`
    );

    if (!confirmado) {
      return;
    }

    this.error = '';
    this.mensaje = '';

    this.clienteService.eliminar(cliente.id).subscribe({
      next: () => {
        this.mensaje = 'Cliente eliminado correctamente.';
        this.cargarClientes();
      },
      error: error => {
        this.error = this.obtenerMensajeError(error, 'No se pudo eliminar el cliente.');
      }
    });
  }

  etiquetaDocumento(tipo: TipoDocumento): string {
    const etiquetas: Record<TipoDocumento, string> = {
      DNI: 'DNI',
      RUC: 'RUC',
      CE: 'Carné de extranjería',
      OTRO: 'Otro'
    };

    return etiquetas[tipo];
  }

  private formularioVacio(): ClienteForm {
    return {
      tipoDocumento: 'RUC',
      numeroDocumento: '',
      nombreRazonSocial: '',
      telefono: '',
      email: '',
      direccion: '',
      activo: true
    };
  }

  private obtenerMensajeError(error: any, fallback: string): string {
    const respuesta = error?.error;

    if (typeof respuesta === 'string' && respuesta.trim()) {
      return respuesta;
    }

    if (respuesta?.message) {
      return respuesta.message;
    }

    if (respuesta?.mensaje) {
      return respuesta.mensaje;
    }

    return fallback;
  }
}
