import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Pago } from './pago.model';

@Injectable({ providedIn: 'root' })
export class PagoService {
  private readonly http = inject(HttpClient);

  obtenerPorOrden(ordenId: number): Observable<Pago> {
    return this.http.get<Pago>(`${environment.apiBaseUrl}/api/v1/pagos/por-orden/${ordenId}`);
  }

  listar(): Observable<Pago[]> {
    return this.http.get<Pago[]>(`${environment.apiBaseUrl}/api/v1/pagos`);
  }
}
