import { Routes } from '@angular/router';
import { authGuard } from './core/guards/auth-guard';
import { rolGuard } from './core/guards/rol-guard';

export const routes: Routes = [
  {
    path: 'auth/callback',
    loadComponent: () => import('./core/auth/auth-callback').then((m) => m.AuthCallback),
  },
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
        //canActivate: [authGuard],
        loadComponent: () => import('./features/catalogo/categoria-list').then((m) => m.CategoriaList),
      },
      {
        path: 'categorias/nueva',
        canActivate: [authGuard, rolGuard('ADMIN')],
        loadComponent: () => import('./features/catalogo/categoria-form').then((m) => m.CategoriaForm),
      },
      {
        path: 'categorias/:id/editar',
        canActivate: [authGuard, rolGuard('ADMIN')],
        loadComponent: () => import('./features/catalogo/categoria-form').then((m) => m.CategoriaForm),
      },
    ],
  },
];
