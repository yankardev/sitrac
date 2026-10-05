import { Routes } from '@angular/router';

import { authGuard } from './core/guards/auth.guard';
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
        component: Dashboard
      },
      {
        path: 'clientes',
        component: Clientes
      },
      {
        path: 'pedidos',
        component: Pedidos
      },
      {
        path: 'programacion',
        component: Programacion
      },
      {
        path: 'conductores',
        component: Conductores
      },
      {
        path: 'flota',
        component: Flota
      },
      {
        path: 'viajes',
        component: Viajes
      },
      {
        path: 'combustible',
        component: Combustible
      },
      {
        path: 'mantenimiento',
        component: Mantenimiento
      },
      {
        path: 'somma',
        component: Somma
      }
    ]
  },
  {
    path: '**',
    redirectTo: 'login'
  }
];
