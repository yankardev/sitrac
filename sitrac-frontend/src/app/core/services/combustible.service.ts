import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

import {
  AbastecimientoCombustible,
  AbastecimientoForm,
  ConfirmarSolicitudAbastecimiento,
  SolicitudAbastecimiento
} from '../models/combustible.model';

@Injectable({ providedIn: 'root' })
export class CombustibleService {
  private readonly http = inject(HttpClient);
  private readonly abastecimientosUrl = '/api/combustible/abastecimientos';
  private readonly solicitudesUrl = '/api/combustible/solicitudes';

  listarSolicitudes(): Observable<SolicitudAbastecimiento[]> {
    return this.http.get<SolicitudAbastecimiento[]>(this.solicitudesUrl);
  }

  confirmarSolicitud(
    id: number,
    payload: ConfirmarSolicitudAbastecimiento
  ): Observable<SolicitudAbastecimiento> {
    return this.http.put<SolicitudAbastecimiento>(`${this.solicitudesUrl}/${id}/confirmar`, payload);
  }

  listar(): Observable<AbastecimientoCombustible[]> {
    return this.http.get<AbastecimientoCombustible[]>(this.abastecimientosUrl);
  }

  crear(abastecimiento: AbastecimientoForm): Observable<AbastecimientoCombustible> {
    return this.http.post<AbastecimientoCombustible>(this.abastecimientosUrl, abastecimiento);
  }

  actualizar(id: number, abastecimiento: AbastecimientoForm): Observable<AbastecimientoCombustible> {
    return this.http.put<AbastecimientoCombustible>(`${this.abastecimientosUrl}/${id}`, abastecimiento);
  }

  eliminar(id: number): Observable<void> {
    return this.http.delete<void>(`${this.abastecimientosUrl}/${id}`);
  }
}
