import { Component, OnInit, inject, signal } from '@angular/core';
import { HttpErrorResponse } from '@angular/common/http';
import { RouterLink } from '@angular/router';
import { CategoriaService } from './categoria-service';
import { Categoria } from './categoria.model';

@Component({
  selector: 'app-categoria-list',
  imports: [RouterLink],
  templateUrl: './categoria-list.html',
})
export class CategoriaList implements OnInit {
  private readonly categoriaService = inject(CategoriaService);
  protected readonly categorias = signal<Categoria[]>([]);
  protected readonly cargando = signal(true);
  protected readonly error = signal<string | null>(null);
  protected readonly errorAccion = signal<string | null>(null);

  ngOnInit(): void {
    this.cargar();
  }

  private cargar(): void {
    this.cargando.set(true);
    this.categoriaService.listar().subscribe({
      next: (data) => {
        this.categorias.set(data);
        this.cargando.set(false);
      },
      error: () => {
        this.error.set('No se pudo cargar la lista de categorias.');
        this.cargando.set(false);
      },
    });
  }

  eliminar(id: number): void {
    if (!confirm(`¿Seguro que quieres eliminar la categoría ${id}?`)) {
      return;
    }
    this.errorAccion.set(null);
    this.categoriaService.eliminar(id).subscribe({
      next: () => this.cargar(),
      error: (err: HttpErrorResponse) => {
        if (err.status === 500) {
          this.errorAccion.set('No se puede eliminar: la categoría tiene productos asociados.');
        } else {
          this.errorAccion.set('No se pudo eliminar la categoría.');
        }
      },
    });
  }
}
