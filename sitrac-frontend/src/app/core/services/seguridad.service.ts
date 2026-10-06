import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';

import { RolUsuario } from '../models/auth.model';
import { UsuarioSeguridad, UsuarioSeguridadForm } from '../models/seguridad.model';

@Injectable({ providedIn: 'root' })
export class SeguridadService {
  private readonly http = inject(HttpClient);
  private readonly apiUrl = '/api/auth/usuarios';

  listar(): Observable<UsuarioSeguridad[]> {
    return this.http.get<UsuarioSeguridad[]>(this.apiUrl);
  }

  crear(data: UsuarioSeguridadForm): Observable<UsuarioSeguridad> {
    return this.http.post<UsuarioSeguridad>(this.apiUrl, data);
  }

  actualizar(
    id: number,
    data: { username: string; nombreCompleto: string; rol: RolUsuario }
  ): Observable<UsuarioSeguridad> {
    return this.http.put<UsuarioSeguridad>(`${this.apiUrl}/${id}`, data);
  }

  cambiarEstado(id: number, activo: boolean): Observable<UsuarioSeguridad> {
    return this.http.put<UsuarioSeguridad>(`${this.apiUrl}/${id}/estado`, { activo });
  }

  restablecerPassword(id: number, password: string): Observable<UsuarioSeguridad> {
    return this.http.put<UsuarioSeguridad>(`${this.apiUrl}/${id}/password`, { password });
  }
}
