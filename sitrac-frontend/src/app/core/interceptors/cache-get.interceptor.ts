import { ApplicationRef, inject } from '@angular/core';
import {
  HttpEvent,
  HttpInterceptorFn,
  HttpResponse
} from '@angular/common/http';
import { Observable, finalize, of, shareReplay, tap } from 'rxjs';

interface CacheEntry {
  expiresAt: number;
  response: HttpResponse<unknown>;
}

const CACHE_TTL_MS = 5 * 60_000;
const cache = new Map<string, CacheEntry>();
const inFlight = new Map<string, Observable<HttpEvent<unknown>>>();

function refrescarVista(appRef: ApplicationRef): void {
  // Angular 22 funciona sin Zone.js por defecto. Después de una respuesta HTTP
  // forzamos un ciclo de render en el siguiente microtask para que loaders,
  // tablas, modales y mensajes se actualicen de inmediato.
  queueMicrotask(() => appRef.tick());
}

export const cacheGetInterceptor: HttpInterceptorFn = (req, next) => {
  const appRef = inject(ApplicationRef);

  if (req.method !== 'GET') {
    // Cualquier operación que cambie datos invalida la caché para que la
    // siguiente lectura muestre información actualizada.
    cache.clear();
    inFlight.clear();

    return next(req).pipe(
      finalize(() => refrescarVista(appRef))
    );
  }

  const key = req.urlWithParams;
  const now = Date.now();
  const cached = cache.get(key);

  if (cached && cached.expiresAt > now) {
    return of(cached.response.clone()).pipe(
      finalize(() => refrescarVista(appRef))
    );
  }

  if (cached) {
    cache.delete(key);
  }

  const pending = inFlight.get(key);
  if (pending) {
    return pending;
  }

  const request$ = next(req).pipe(
    tap(event => {
      if (event instanceof HttpResponse) {
        cache.set(key, {
          expiresAt: Date.now() + CACHE_TTL_MS,
          response: event.clone()
        });
      }
    }),
    finalize(() => {
      inFlight.delete(key);
      refrescarVista(appRef);
    }),
    shareReplay({ bufferSize: 1, refCount: false })
  );

  inFlight.set(key, request$);
  return request$;
};
