import { OrdenResponse } from './orden.model';
import { Pago } from './pago.model';

export interface PedidoConPago {
  orden: OrdenResponse;
  pago: Pago | null;
}
