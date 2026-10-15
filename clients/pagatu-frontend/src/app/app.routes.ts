import { Routes } from '@angular/router';

export const routes: Routes = [
  {
    path: '',
    loadComponent: () => import('./core/layout/layout').then((m) => m.Layout),
    children: [
      {
        path: '',
        loadComponent: () => import('./core/inicio/inicio').then((m) => m.Inicio),
      },
      {
        path: 'categorias',
        loadComponent: () => import('./features/catalogo/categoria-list').then((m) => m.CategoriaList),
      },
      {
        path: 'categorias/nueva',
        loadComponent: () => import('./features/catalogo/categoria-form').then((m) => m.CategoriaForm),
      },
      {
        path: 'categorias/:id/editar',
        loadComponent: () => import('./features/catalogo/categoria-form').then((m) => m.CategoriaForm),
      },
    ],
  },
];