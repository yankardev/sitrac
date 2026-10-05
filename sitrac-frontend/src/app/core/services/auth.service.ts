import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap } from 'rxjs';

import {
  LoginRequest,
  RegistroUsuarioRequest,
  UsuarioRegistrado,
  UsuarioSesion
} from '../models/auth.model';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private readonly http = inject(HttpClient);
  private readonly apiUrl = '/api/auth';
  private readonly sessionKey = 'sitrac_usuario';

  login(credenciales: LoginRequest, recordar: boolean): Observable<UsuarioSesion> {
    return this.http
      .post<UsuarioSesion>(`${this.apiUrl}/login`, credenciales)
      .pipe(tap(usuario => this.guardarSesion(usuario, recordar)));
  }

  registrar(usuario: RegistroUsuarioRequest): Observable<UsuarioRegistrado> {
    return this.http.post<UsuarioRegistrado>(`${this.apiUrl}/registro`, usuario);
  }

  usuarioActual(): UsuarioSesion | null {
    const valor = sessionStorage.getItem(this.sessionKey)
      ?? localStorage.getItem(this.sessionKey);

    if (!valor) {
      return null;
    }

    try {
      return JSON.parse(valor) as UsuarioSesion;
    } catch {
      this.logout();
      return null;
    }
  }

  estaAutenticado(): boolean {
    return this.usuarioActual() !== null;
  }

  logout(): void {
    sessionStorage.removeItem(this.sessionKey);
    localStorage.removeItem(this.sessionKey);
  }

  private guardarSesion(usuario: UsuarioSesion, recordar: boolean): void {
    this.logout();
    const almacenamiento = recordar ? localStorage : sessionStorage;
    almacenamiento.setItem(this.sessionKey, JSON.stringify(usuario));
  }
}
