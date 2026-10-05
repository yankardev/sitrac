import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

import { Conductor, ConductorForm } from '../models/conductor.model';

@Injectable({
  providedIn: 'root'
})
export class ConductorService {
  private readonly http = inject(HttpClient);
  private readonly apiUrl = '/api/conductores';

  listar(): Observable<Conductor[]> {
    return this.http.get<Conductor[]>(this.apiUrl);
  }

  obtenerPorId(id: number): Observable<Conductor> {
    return this.http.get<Conductor>(`${this.apiUrl}/${id}`);
  }

  crear(conductor: Omit<ConductorForm, 'disponible' | 'activo'>): Observable<Conductor> {
    return this.http.post<Conductor>(this.apiUrl, conductor);
  }

  actualizar(id: number, conductor: ConductorForm): Observable<Conductor> {
    return this.http.put<Conductor>(`${this.apiUrl}/${id}`, conductor);
  }

  cambiarDisponibilidad(id: number, disponible: boolean): Observable<Conductor> {
    return this.http.put<Conductor>(`${this.apiUrl}/${id}/disponibilidad`, { disponible });
  }

  eliminar(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
