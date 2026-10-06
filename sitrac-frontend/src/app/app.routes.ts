import { Routes } from '@angular/router';

import { authGuard } from './core/guards/auth.guard';
import { roleGuard } from './core/guards/role.guard';
import { MainLayout } from './layout/main-layout/main-layout';
import { Login } from './pages/login/login';

import { Dashboard } from './pages/dashboard/dashboard';
import { Clientes } from './pages/clientes/clientes';
import { Pedidos } from './pages/pedidos/pedidos';
import { Conductores } from './pages/conductores/conductores';
import { Flota } from './pages/flota/flota';
import { Programacion } from './pages/programacion/programacion';
import { Viajes } from './pages/viajes/viajes';
import { Combustible } from './pages/combustible/combustible';
import { Mantenimiento } from './pages/mantenimiento/mantenimiento';
import { Somma } from './pages/somma/somma';

const TODOS = ['ADMIN', 'OPERADOR', 'SOMMA'];
const OPERACION = ['ADMIN', 'OPERADOR'];
const SOLO_ADMIN = ['ADMIN'];
const SEGURIDAD = ['ADMIN', 'SOMMA'];

export const routes: Routes = [
  {
    path: '',
    redirectTo: 'login',
    pathMatch: 'full'
  },
  {
    path: 'login',
    component: Login
  },
  {
    path: '',
    component: MainLayout,
    canActivate: [authGuard],
    children: [
      {
        path: 'dashboard',
        component: Dashboard,
        canActivate: [roleGuard],
        data: { roles: TODOS }
      },
      {
        path: 'clientes',
        component: Clientes,
        canActivate: [roleGuard],
        data: { roles: OPERACION }
      },
      {
        path: 'pedidos',
        component: Pedidos,
        canActivate: [roleGuard],
        data: { roles: OPERACION }
      },
      {
        path: 'programacion',
        component: Programacion,
        canActivate: [roleGuard],
        data: { roles: OPERACION }
      },
      {
        path: 'conductores',
        component: Conductores,
        canActivate: [roleGuard],
        data: { roles: OPERACION }
      },
      {
        path: 'flota',
        component: Flota,
        canActivate: [roleGuard],
        data: { roles: OPERACION }
      },
      {
        path: 'viajes',
        component: Viajes,
        canActivate: [roleGuard],
        data: { roles: OPERACION }
      },
      {
        path: 'combustible',
        component: Combustible,
        canActivate: [roleGuard],
        data: { roles: OPERACION }
      },
      {
        path: 'mantenimiento',
        component: Mantenimiento,
        canActivate: [roleGuard],
        data: { roles: SOLO_ADMIN }
      },
      {
        path: 'somma',
        component: Somma,
        canActivate: [roleGuard],
        data: { roles: SEGURIDAD }
      }
    ]
  },
  {
    path: '**',
    redirectTo: 'login'
  }
];
