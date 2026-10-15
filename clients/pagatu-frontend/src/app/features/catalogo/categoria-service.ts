import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Categoria } from './categoria.model';

@Injectable({ providedIn: 'root' })
export class CategoriaService {
  private readonly http = inject(HttpClient);

  listar(): Observable<Categoria[]> {
    return this.http.get<Categoria[]>(`${environment.apiBaseUrl}/api/v1/categorias`);
  }

  obtener(id: number): Observable<Categoria> {
    return this.http.get<Categoria>(`${environment.apiBaseUrl}/api/v1/categorias/${id}`);
  }

  crear(categoria: Categoria): Observable<Categoria> {
    return this.http.post<Categoria>(`${environment.apiBaseUrl}/api/v1/categorias`, categoria);
  }

  actualizar(id: number, categoria: Categoria): Observable<Categoria> {
    return this.http.put<Categoria>(`${environment.apiBaseUrl}/api/v1/categorias/${id}`, categoria);
  }

  eliminar(id: number): Observable<void> {
    return this.http.delete<void>(`${environment.apiBaseUrl}/api/v1/categorias/${id}`);
  }
    
}
