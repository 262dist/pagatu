import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { OrdenRequest, OrdenResponse } from './orden.model';

@Injectable({ providedIn: 'root' })
export class OrdenService {
  private readonly http = inject(HttpClient);

  crear(request: OrdenRequest): Observable<OrdenResponse> {
    return this.http.post<OrdenResponse>(`${environment.apiBaseUrl}/api/v1/ordenes`, request);
  }

  obtener(id: number): Observable<OrdenResponse> {
    return this.http.get<OrdenResponse>(`${environment.apiBaseUrl}/api/v1/ordenes/${id}`);
  }

  listar(): Observable<OrdenResponse[]> {
    return this.http.get<OrdenResponse[]>(`${environment.apiBaseUrl}/api/v1/ordenes`);
  }
}
