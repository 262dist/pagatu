import { Categoria } from './categoria.model';

export interface Producto {
  id: number;
  nombre: string;
  descripcion: string;
  precio: number;
  activo: boolean;
  stock: number;
  categoria: Categoria;
}
