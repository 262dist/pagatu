import { Component, OnDestroy, OnInit, inject, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { OrdenService } from './orden-service';
import { PagoService } from './pago-service';
import { AuthService } from '../../core/auth/auth-service';
import { PedidoConPago } from './pedido.model';

@Component({
  selector: 'app-mis-pedidos',
  imports: [RouterLink],
  templateUrl: './mis-pedidos.html',
})
export class MisPedidos implements OnInit, OnDestroy {
  private readonly ordenService = inject(OrdenService);
  private readonly pagoService = inject(PagoService);
  private readonly auth = inject(AuthService);

  protected readonly pedidos = signal<PedidoConPago[]>([]);
  protected readonly cargando = signal(true);
  private intervalo?: ReturnType<typeof setInterval>;

  ngOnInit(): void {
    this.cargar();
    this.intervalo = setInterval(() => this.refrescarPendientes(), 4000);
  }

  ngOnDestroy(): void {
    clearInterval(this.intervalo);
  }

  private cargar(): void {
    this.ordenService.listar().subscribe((ordenes) => {
      const propias = ordenes.filter((o) => o.idCliente === this.auth.idCliente());
      this.pedidos.set(propias.map((orden) => ({ orden, pago: null })));
      this.cargando.set(false);
      for (const pedido of this.pedidos()) {
        if (pedido.orden.estado !== 'PENDIENTE_PAGO') {
          this.cargarPago(pedido.orden.id);
        }
      }
    });
  }

  private refrescarPendientes(): void {
    const pendientes = this.pedidos().filter((p) => p.orden.estado === 'PENDIENTE_PAGO');
    for (const pendiente of pendientes) {
      this.ordenService.obtener(pendiente.orden.id).subscribe((actualizada) => {
        this.pedidos.update((lista) =>
          lista.map((p) => (p.orden.id === actualizada.id ? { ...p, orden: actualizada } : p)),
        );
        if (actualizada.estado !== 'PENDIENTE_PAGO') {
          this.cargarPago(actualizada.id);
        }
      });
    }
  }

  private cargarPago(ordenId: number): void {
    this.pagoService.obtenerPorOrden(ordenId).subscribe({
      next: (pago) => {
        this.pedidos.update((lista) => lista.map((p) => (p.orden.id === ordenId ? { ...p, pago } : p)));
      },
      error: () => {
        // 404 esperado (3.6): esta orden (p. ej. CARRITO, sin stock) nunca tuvo un pago que generar.
      },
    });
  }

  protected etiqueta(estado: string): string {
    switch (estado) {
      case 'PENDIENTE_PAGO':
        return 'Procesando pago…';
      case 'PAGADA':
        return 'Pagado';
      case 'CANCELADA':
        return 'Pago rechazado';
      case 'CARRITO':
        return 'Sin stock disponible';
      default:
        return estado;
    }
  }
}
