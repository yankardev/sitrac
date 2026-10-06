import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

import {
  EstadoUnidad,
  Semirremolque,
  SemirremolqueForm,
  Tracto,
  TractoForm
} from '../models/flota.model';

@Injectable({ providedIn: 'root' })
export class FlotaService {
  private readonly http = inject(HttpClient);
  private readonly tractosUrl = '/api/tractos';
  private readonly semirremolquesUrl = '/api/semirremolques';

  listarTractos(): Observable<Tracto[]> {
    return this.http.get<Tracto[]>(this.tractosUrl);
  }

  crearTracto(data: TractoForm): Observable<Tracto> {
    return this.http.post<Tracto>(this.tractosUrl, data);
  }

  actualizarTracto(id: number, data: TractoForm): Observable<Tracto> {
    return this.http.put<Tracto>(`${this.tractosUrl}/${id}`, data);
  }

  cambiarEstadoTracto(id: number, estado: EstadoUnidad): Observable<Tracto> {
    return this.http.put<Tracto>(`${this.tractosUrl}/${id}/estado`, { estado });
  }

  eliminarTracto(id: number): Observable<void> {
    return this.http.delete<void>(`${this.tractosUrl}/${id}`);
  }

  listarSemirremolques(): Observable<Semirremolque[]> {
    return this.http.get<Semirremolque[]>(this.semirremolquesUrl);
  }

  crearSemirremolque(data: SemirremolqueForm): Observable<Semirremolque> {
    return this.http.post<Semirremolque>(this.semirremolquesUrl, data);
  }

  actualizarSemirremolque(id: number, data: SemirremolqueForm): Observable<Semirremolque> {
    return this.http.put<Semirremolque>(`${this.semirremolquesUrl}/${id}`, data);
  }

  cambiarEstadoSemirremolque(id: number, estado: EstadoUnidad): Observable<Semirremolque> {
    return this.http.put<Semirremolque>(`${this.semirremolquesUrl}/${id}/estado`, { estado });
  }

  eliminarSemirremolque(id: number): Observable<void> {
    return this.http.delete<void>(`${this.semirremolquesUrl}/${id}`);
  }
}
