import { CommonModule } from '@angular/common';
import { Component, inject } from '@angular/core';
import { Router, RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';

import { RolUsuario } from '../../core/models/auth.model';
import { AuthService } from '../../core/services/auth.service';

@Component({
  selector: 'app-main-layout',
  imports: [CommonModule, RouterOutlet, RouterLink, RouterLinkActive],
  templateUrl: './main-layout.html',
  styleUrl: './main-layout.scss'
})
export class MainLayout {
  private readonly authService = inject(AuthService);
  private readonly router = inject(Router);

  get usuario() {
    return this.authService.usuarioActual();
  }

  get inicialUsuario(): string {
    return this.usuario?.nombreCompleto?.charAt(0).toUpperCase() || 'U';
  }

  puedeAcceder(...roles: RolUsuario[]): boolean {
    const rol = this.usuario?.rol;
    return !!rol && roles.includes(rol);
  }

  cerrarSesion(): void {
    this.authService.logout();
    this.router.navigateByUrl('/login');
  }
}
