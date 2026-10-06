import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

import { Viaje, ViajeForm } from '../models/viaje.model';

@Injectable({
  providedIn: 'root'
})
export class ViajeService {
  private readonly http = inject(HttpClient);
  private readonly apiUrl = '/api/viajes';

  listar(): Observable<Viaje[]> {
    return this.http.get<Viaje[]>(this.apiUrl);
  }

  crear(viaje: ViajeForm): Observable<Viaje> {
    return this.http.post<Viaje>(this.apiUrl, viaje);
  }

  actualizar(id: number, viaje: ViajeForm): Observable<Viaje> {
    return this.http.put<Viaje>(`${this.apiUrl}/${id}`, viaje);
  }

  eliminar(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
