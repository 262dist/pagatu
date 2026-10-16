import { HttpInterceptorFn } from '@angular/common/http';

export const traceInterceptor: HttpInterceptorFn = (req, next) => {
  const conTraceId = req.clone({ setHeaders: { 'X-Trace-ID': crypto.randomUUID() } });
  return next(conTraceId);
};
