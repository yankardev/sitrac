import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

import { Mantenimiento, MantenimientoForm } from '../models/mantenimiento.model';
import { SemirremolqueOperacion, TractoOperacion } from '../models/programacion.model';

@Injectable({
  providedIn: 'root'
})
export class MantenimientoService {
  private readonly http = inject(HttpClient);
  private readonly apiUrl = '/api/mantenimientos';

  listar(): Observable<Mantenimiento[]> {
    return this.http.get<Mantenimiento[]>(this.apiUrl);
  }

  crear(mantenimiento: MantenimientoForm): Observable<Mantenimiento> {
    return this.http.post<Mantenimiento>(this.apiUrl, mantenimiento);
  }

  actualizar(id: number, mantenimiento: MantenimientoForm): Observable<Mantenimiento> {
    return this.http.put<Mantenimiento>(`${this.apiUrl}/${id}`, mantenimiento);
  }

  eliminar(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  listarTractos(): Observable<TractoOperacion[]> {
    return this.http.get<TractoOperacion[]>('/api/tractos');
  }

  listarSemirremolques(): Observable<SemirremolqueOperacion[]> {
    return this.http.get<SemirremolqueOperacion[]>('/api/semirremolques');
  }
}
