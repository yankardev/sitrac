import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';

import { RolUsuario } from '../models/auth.model';
import { AuthService } from '../services/auth.service';

export const roleGuard: CanActivateFn = route => {
  const authService = inject(AuthService);
  const router = inject(Router);
  const usuario = authService.usuarioActual();
  const rolesPermitidos = (route.data?.['roles'] ?? []) as RolUsuario[];

  if (!usuario) {
    return router.createUrlTree(['/login']);
  }

  if (rolesPermitidos.length === 0 || rolesPermitidos.includes(usuario.rol)) {
    return true;
  }

  return router.createUrlTree(['/dashboard']);
};
