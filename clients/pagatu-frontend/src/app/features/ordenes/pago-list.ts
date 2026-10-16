import { Component, OnInit, inject, signal } from '@angular/core';
import { PagoService } from './pago-service';
import { Pago } from './pago.model';

@Component({
  selector: 'app-pago-list',
  templateUrl: './pago-list.html',
})
export class PagoList implements OnInit {
  private readonly pagoService = inject(PagoService);
  protected readonly pagos = signal<Pago[]>([]);
  protected readonly cargando = signal(true);
  protected readonly error = signal<string | null>(null);

  ngOnInit(): void {
    this.pagoService.listar().subscribe({
      next: (data) => {
        this.pagos.set(data);
        this.cargando.set(false);
      },
      error: () => {
        this.error.set('No se pudo cargar la lista de pagos.');
        this.cargando.set(false);
      },
    });
  }
}
