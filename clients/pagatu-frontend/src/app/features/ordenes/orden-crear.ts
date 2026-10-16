import { Component, OnInit, inject, signal, computed } from '@angular/core';
import { RouterLink } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { ProductoService } from '../catalogo/producto-service';
import { Producto } from '../catalogo/producto.model';
import { OrdenService } from './orden-service';
import { OrdenResponse } from './orden.model';

interface ItemCarrito {
  producto: Producto;
  cantidad: number;
}

@Component({
  selector: 'app-orden-crear',
  imports: [RouterLink, FormsModule],
  templateUrl: './orden-crear.html',
})
export class OrdenCrear implements OnInit {
  private readonly productoService = inject(ProductoService);
  private readonly ordenService = inject(OrdenService);

  protected readonly productos = signal<Producto[]>([]);
  protected readonly carrito = signal<ItemCarrito[]>([]);
  protected readonly cargando = signal(true);
  protected readonly enviando = signal(false);
  protected readonly error = signal<string | null>(null);
  protected readonly resultado = signal<OrdenResponse | null>(null);
  protected metodoPago = 'YAPE_PLIN';

  protected readonly total = computed(() =>
    this.carrito().reduce((suma, item) => suma + item.producto.precio * item.cantidad, 0),
  );

  ngOnInit(): void {
    this.productoService.listar().subscribe({
      next: (data) => {
        this.productos.set(data.filter((p) => p.activo));
        this.cargando.set(false);
      },
      error: () => {
        this.error.set('No se pudo cargar el catálogo de productos.');
        this.cargando.set(false);
      },
    });
  }

  agregar(producto: Producto, cantidad: number): void {
    if (cantidad < 1) return;
    const existente = this.carrito().find((item) => item.producto.id === producto.id);
    if (existente) {
      this.carrito.update((lista) =>
        lista.map((item) =>
          item.producto.id === producto.id ? { ...item, cantidad: item.cantidad + cantidad } : item,
        ),
      );
    } else {
      this.carrito.update((lista) => [...lista, { producto, cantidad }]);
    }
  }

  quitar(idProducto: number): void {
    this.carrito.update((lista) => lista.filter((item) => item.producto.id !== idProducto));
  }

  confirmar(): void {
    if (this.carrito().length === 0) return;
    this.enviando.set(true);
    this.error.set(null);
    this.ordenService
      .crear({
        metodoPago: this.metodoPago,
        detalles: this.carrito().map((item) => ({ idProducto: item.producto.id, cantidad: item.cantidad })),
      })
      .subscribe({
        next: (orden) => {
          this.resultado.set(orden);
          this.carrito.set([]);
          this.enviando.set(false);
        },
        error: () => {
          this.error.set('No se pudo crear la orden.');
          this.enviando.set(false);
        },
      });
  }
}
