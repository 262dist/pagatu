export interface DetalleOrdenRequest {
  idProducto: number;
  cantidad: number;
}

export interface OrdenRequest {
  metodoPago: string;
  detalles: DetalleOrdenRequest[];
}

export interface DetalleOrdenResponse {
  idProducto: number;
  nombreProducto: string | null;
  cantidad: number;
  precioUnitario: number | null;
  subtotal: number | null;
}

export interface OrdenResponse {
  id: number;
  idCliente: string;
  fechaCreacion: string;
  estado: string;
  total: number | null;
  detalles: DetalleOrdenResponse[];
}
