import { CommonModule } from '@angular/common';
import { Component, OnInit, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';

import { AuthService } from '../../core/services/auth.service';

@Component({
  selector: 'app-login',
  imports: [CommonModule, FormsModule],
  templateUrl: './login.html',
  styleUrl: './login.scss'
})
export class Login implements OnInit {
  private readonly authService = inject(AuthService);
  private readonly router = inject(Router);

  username = '';
  password = '';
  recordar = false;
  cargando = false;
  error = '';

  ngOnInit(): void {
    if (this.authService.estaAutenticado()) {
      this.router.navigateByUrl('/dashboard');
    }
  }

  ingresar(): void {
    if (!this.username.trim() || !this.password) {
      this.error = 'Ingresa tu usuario y contraseña.';
      return;
    }

    this.cargando = true;
    this.error = '';

    this.authService.login(
      {
        username: this.username.trim(),
        password: this.password
      },
      this.recordar
    ).subscribe({
      next: () => {
        this.cargando = false;
        this.router.navigateByUrl('/dashboard');
      },
      error: error => {
        this.error = this.obtenerMensajeError(error);
        this.cargando = false;
      }
    });
  }

  private obtenerMensajeError(error: any): string {
    const respuesta = error?.error;

    if (typeof respuesta === 'string' && respuesta.trim()) {
      return respuesta;
    }

    if (respuesta?.error) {
      return respuesta.error;
    }

    if (respuesta?.campos) {
      const primerMensaje = Object.values(respuesta.campos)[0];
      if (typeof primerMensaje === 'string') {
        return primerMensaje;
      }
    }

    return 'No se pudo iniciar sesión. Verifica tus credenciales y que auth-service esté ejecutándose.';
  }
}
