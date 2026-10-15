import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from '../auth/auth-service';

export function rolGuard(...rolesPermitidos: string[]): CanActivateFn {
  return () => {
    const auth = inject(AuthService);
    const router = inject(Router);
    if (auth.tieneRol(...rolesPermitidos)) {
      return true;
    }
    router.navigateByUrl('/');
    return false;
  };
}