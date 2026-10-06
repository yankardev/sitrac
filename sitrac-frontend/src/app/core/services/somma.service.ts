import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

import { RegistroSomma, RegistroSommaForm } from '../models/somma.model';

@Injectable({ providedIn: 'root' })
export class SommaService {
  private readonly http = inject(HttpClient);
  private readonly apiUrl = '/api/somma';

  listar(): Observable<RegistroSomma[]> {
    return this.http.get<RegistroSomma[]>(this.apiUrl);
  }

  crear(registro: RegistroSommaForm): Observable<RegistroSomma> {
    return this.http.post<RegistroSomma>(this.apiUrl, registro);
  }

  actualizar(id: number, registro: RegistroSommaForm): Observable<RegistroSomma> {
    return this.http.put<RegistroSomma>(`${this.apiUrl}/${id}`, registro);
  }

  eliminar(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
