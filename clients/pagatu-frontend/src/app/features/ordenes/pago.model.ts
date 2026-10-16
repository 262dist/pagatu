export interface Pago {
  id: number;
  ordenId: number;
  monto: number;
  metodoPago: string;
  estado: string;
  fechaPago: string;
}
