import { CommonModule } from '@angular/common';
import { ChangeDetectorRef, Component, OnInit, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { finalize } from 'rxjs';

import { RolUsuario } from '../../core/models/auth.model';
import { UsuarioSeguridad, UsuarioSeguridadForm } from '../../core/models/seguridad.model';
import { AuthService } from '../../core/services/auth.service';
import { SeguridadService } from '../../core/services/seguridad.service';

@Component({
  selector: 'app-seguridad',
  imports: [CommonModule, FormsModule],
  templateUrl: './seguridad.html',
  styleUrl: './seguridad.scss'
})
export class Seguridad implements OnInit {
  private readonly service = inject(SeguridadService);
  private readonly authService = inject(AuthService);
  private readonly cdr = inject(ChangeDetectorRef);

  readonly roles: Array<{ valor: RolUsuario; etiqueta: string }> = [
    { valor: 'ADMIN', etiqueta: 'Administrador' },
    { valor: 'OPERADOR', etiqueta: 'Operador' },
    { valor: 'SOMMA', etiqueta: 'SOMMA' },
    { valor: 'MANTENIMIENTO', etiqueta: 'Mantenimiento' },
    { valor: 'SUPERVISOR', etiqueta: 'Supervisor' }
  ];

  usuarios: UsuarioSeguridad[] = [];
  busqueda = '';
  filtroRol: RolUsuario | 'TODOS' = 'TODOS';
  filtroEstado: 'TODOS' | 'ACTIVO' | 'BLOQUEADO' = 'TODOS';

  cargando = false;
  guardando = false;
  mostrarFormulario = false;
  mostrarReset = false;
  editandoId: number | null = null;
  usuarioReset: UsuarioSeguridad | null = null;
  passwordTemporal = '';
  mensaje = '';
  error = '';

  formulario: UsuarioSeguridadForm = this.formularioVacio();

  ngOnInit(): void {
    this.cargar();
  }

  get usuariosFiltrados(): UsuarioSeguridad[] {
    const termino = this.busqueda.trim().toLowerCase();

    return this.usuarios.filter(usuario => {
      if (this.filtroRol !== 'TODOS' && usuario.rol !== this.filtroRol) return false;
      if (this.filtroEstado === 'ACTIVO' && !usuario.activo) return false;
      if (this.filtroEstado === 'BLOQUEADO' && usuario.activo) return false;

      if (!termino) return true;

      return usuario.username.toLowerCase().includes(termino)
        || usuario.nombreCompleto.toLowerCase().includes(termino)
        || this.rolEtiqueta(usuario.rol).toLowerCase().includes(termino);
    });
  }

  get totalActivos(): number {
    return this.usuarios.filter(usuario => usuario.activo).length;
  }

  get totalBloqueados(): number {
    return this.usuarios.filter(usuario => !usuario.activo).length;
  }

  get totalAdministradores(): number {
    return this.usuarios.filter(usuario => usuario.rol === 'ADMIN').length;
  }

  cargar(): void {
    this.cargando = true;
    this.error = '';

    this.service.listar()
      .pipe(finalize(() => {
        this.cargando = false;
        this.cdr.detectChanges();
      }))
      .subscribe({
        next: usuarios => {
          this.usuarios = [...usuarios].sort((a, b) => b.id - a.id);
        },
        error: error => {
          this.error = this.mensajeError(error, 'No se pudo cargar la gestión de usuarios.');
        }
      });
  }

  abrirNuevo(): void {
    this.editandoId = null;
    this.formulario = this.formularioVacio();
    this.mensaje = '';
    this.error = '';
    this.mostrarFormulario = true;
  }

  editar(usuario: UsuarioSeguridad): void {
    this.editandoId = usuario.id;
    this.formulario = {
      username: usuario.username,
      nombreCompleto: usuario.nombreCompleto,
      rol: usuario.rol,
      password: ''
    };
    this.mensaje = '';
    this.error = '';
    this.mostrarFormulario = true;
  }

  cerrarFormulario(): void {
    if (this.guardando) return;
    this.mostrarFormulario = false;
    this.editandoId = null;
    this.formulario = this.formularioVacio();
  }

  guardar(): void {
    if (!this.formulario.username.trim() || !this.formulario.nombreCompleto.trim()) {
      this.error = 'Completa el usuario, nombre completo y rol.';
      return;
    }

    if (this.editandoId === null && this.formulario.password.length < 6) {
      this.error = 'La contraseña temporal debe tener al menos 6 caracteres.';
      return;
    }

    this.guardando = true;
    this.error = '';
    this.mensaje = '';

    const operacion = this.editandoId === null
      ? this.service.crear({
          username: this.formulario.username.trim(),
          nombreCompleto: this.formulario.nombreCompleto.trim(),
          rol: this.formulario.rol,
          password: this.formulario.password
        })
      : this.service.actualizar(this.editandoId, {
          username: this.formulario.username.trim(),
          nombreCompleto: this.formulario.nombreCompleto.trim(),
          rol: this.formulario.rol
        });

    operacion
      .pipe(finalize(() => {
        this.guardando = false;
        this.cdr.detectChanges();
      }))
      .subscribe({
        next: usuario => {
          this.aplicarLocal(usuario);
          this.mensaje = this.editandoId === null
            ? `Usuario ${usuario.username} creado correctamente.`
            : `Usuario ${usuario.username} actualizado correctamente.`;
          this.mostrarFormulario = false;
          this.editandoId = null;
          this.formulario = this.formularioVacio();
        },
        error: error => {
          this.error = this.mensajeError(error, 'No se pudo guardar el usuario.');
        }
      });
  }

  cambiarEstado(usuario: UsuarioSeguridad): void {
    const usuarioSesion = this.authService.usuarioActual();

    if (usuarioSesion?.id === usuario.id && usuario.activo) {
      this.error = 'No puedes bloquear tu propio usuario mientras tienes la sesión iniciada.';
      return;
    }

    const accion = usuario.activo ? 'bloquear' : 'activar';
    const confirmado = window.confirm(
      usuario.activo
        ? `¿Bloquear el acceso de ${usuario.nombreCompleto}? No podrá iniciar sesión.`
        : `¿Activar nuevamente el acceso de ${usuario.nombreCompleto}?`
    );

    if (!confirmado) return;

    this.service.cambiarEstado(usuario.id, !usuario.activo).subscribe({
      next: actualizado => {
        this.aplicarLocal(actualizado);
        this.mensaje = actualizado.activo
          ? `Acceso de ${actualizado.username} activado.`
          : `Acceso de ${actualizado.username} bloqueado.`;
        this.error = '';
        this.cdr.detectChanges();
      },
      error: error => {
        this.error = this.mensajeError(error, `No se pudo ${accion} el usuario.`);
      }
    });
  }

  abrirReset(usuario: UsuarioSeguridad): void {
    this.usuarioReset = usuario;
    this.passwordTemporal = '';
    this.error = '';
    this.mostrarReset = true;
  }

  cerrarReset(): void {
    if (this.guardando) return;
    this.mostrarReset = false;
    this.usuarioReset = null;
    this.passwordTemporal = '';
  }

  confirmarReset(): void {
    if (!this.usuarioReset) return;
    if (this.passwordTemporal.length < 6) {
      this.error = 'La nueva contraseña debe tener al menos 6 caracteres.';
      return;
    }

    this.guardando = true;
    const usuario = this.usuarioReset;

    this.service.restablecerPassword(usuario.id, this.passwordTemporal)
      .pipe(finalize(() => {
        this.guardando = false;
        this.cdr.detectChanges();
      }))
      .subscribe({
        next: () => {
          this.mensaje = `Contraseña de ${usuario.username} restablecida correctamente.`;
          this.error = '';
          this.cerrarReset();
        },
        error: error => {
          this.error = this.mensajeError(error, 'No se pudo restablecer la contraseña.');
        }
      });
  }

  rolEtiqueta(rol: RolUsuario): string {
    return this.roles.find(item => item.valor === rol)?.etiqueta ?? rol;
  }

  private aplicarLocal(usuario: UsuarioSeguridad): void {
    const existe = this.usuarios.some(item => item.id === usuario.id);
    this.usuarios = existe
      ? this.usuarios.map(item => item.id === usuario.id ? usuario : item)
      : [usuario, ...this.usuarios];
  }

  private formularioVacio(): UsuarioSeguridadForm {
    return {
      username: '',
      nombreCompleto: '',
      rol: 'OPERADOR',
      password: ''
    };
  }

  private mensajeError(error: any, fallback: string): string {
    const respuesta = error?.error;
    if (typeof respuesta === 'string' && respuesta.trim()) return respuesta;
    if (respuesta?.error) return respuesta.error;
    if (respuesta?.message) return respuesta.message;
    if (respuesta?.campos) {
      const mensaje = Object.values(respuesta.campos)[0];
      if (typeof mensaje === 'string') return mensaje;
    }
    return fallback;
  }
}
