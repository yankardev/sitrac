import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

import {
  AbastecimientoCombustible,
  AbastecimientoForm
} from '../models/combustible.model';

@Injectable({
  providedIn: 'root'
})
export class CombustibleService {
  private readonly http = inject(HttpClient);
  private readonly apiUrl = '/api/combustible/abastecimientos';

  listar(): Observable<AbastecimientoCombustible[]> {
    return this.http.get<AbastecimientoCombustible[]>(this.apiUrl);
  }

  crear(abastecimiento: AbastecimientoForm): Observable<AbastecimientoCombustible> {
    return this.http.post<AbastecimientoCombustible>(this.apiUrl, abastecimiento);
  }

  actualizar(id: number, abastecimiento: AbastecimientoForm): Observable<AbastecimientoCombustible> {
    return this.http.put<AbastecimientoCombustible>(`${this.apiUrl}/${id}`, abastecimiento);
  }

  eliminar(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
