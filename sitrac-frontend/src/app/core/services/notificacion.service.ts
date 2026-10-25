import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';

import { RegistroNotificacion } from '../models/notificacion.model';

@Injectable({
  providedIn: 'root'
})
export class NotificacionService {
  private readonly http = inject(HttpClient);
  private readonly apiUrl = '/api/notificaciones';

  listar(): Observable<RegistroNotificacion[]> {
    return this.http.get<RegistroNotificacion[]>(this.apiUrl);
  }
}
