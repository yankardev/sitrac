import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

import {
  ConductorOperacion,
  Programacion,
  ProgramacionForm,
  SemirremolqueOperacion,
  TractoOperacion
} from '../models/programacion.model';

@Injectable({
  providedIn: 'root'
})
export class ProgramacionService {
  private readonly http = inject(HttpClient);

  listar(): Observable<Programacion[]> {
    return this.http.get<Programacion[]>('/api/programaciones');
  }

  crear(programacion: ProgramacionForm): Observable<Programacion> {
    return this.http.post<Programacion>('/api/programaciones', programacion);
  }

  actualizar(id: number, programacion: ProgramacionForm): Observable<Programacion> {
    return this.http.put<Programacion>(`/api/programaciones/${id}`, programacion);
  }

  eliminar(id: number): Observable<void> {
    return this.http.delete<void>(`/api/programaciones/${id}`);
  }

  listarConductores(): Observable<ConductorOperacion[]> {
    return this.http.get<ConductorOperacion[]>('/api/conductores');
  }

  listarTractos(): Observable<TractoOperacion[]> {
    return this.http.get<TractoOperacion[]>('/api/tractos');
  }

  listarSemirremolques(): Observable<SemirremolqueOperacion[]> {
    return this.http.get<SemirremolqueOperacion[]>('/api/semirremolques');
  }
}
