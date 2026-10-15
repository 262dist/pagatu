import { inject } from '@angular/core';
import { CanActivateFn } from '@angular/router';
import { AuthService } from '../auth/auth-service';

export const authGuard: CanActivateFn = async (_route, state) => {
  const auth = inject(AuthService);
  if (auth.autenticado()) {
    return true;
  }
  await auth.iniciarLogin(state.url);
  return false;
};