export type TipoAbastecimiento = 'INTERNO' | 'TERCERO';
export type EstadoSolicitudAbastecimiento = 'PENDIENTE' | 'CONFIRMADA';

export interface SolicitudAbastecimiento {
  id: number;
  programacionId: number;
  conductorId: number;
  tractoId: number;
  fechaGeneracion: string;
  estado: EstadoSolicitudAbastecimiento;
  puntoAbastecimiento: string | null;
  cantidadGalones: number | null;
  observacion: string | null;
  fechaConfirmacion: string | null;
}

export interface ConfirmarSolicitudAbastecimiento {
  puntoAbastecimiento: string;
  cantidadGalones: number;
  observacion: string;
}

// Se mantienen para compatibilidad con los registros históricos del servicio.
export interface AbastecimientoCombustible {
  id: number;
  programacionId: number;
  viajeId: number | null;
  conductorId: number;
  tractoId: number;
  tipoAbastecimiento: TipoAbastecimiento;
  fechaHora: string;
  cantidadGalones: number;
  kilometraje: number;
  precioUnitario: number | null;
  costoTotal: number | null;
  tanqueOrigen: string | null;
  proveedor: string | null;
  numeroComprobante: string | null;
  observacion: string | null;
}

export interface AbastecimientoForm {
  programacionId: number | null;
  viajeId: number | null;
  conductorId: number | null;
  tractoId: number | null;
  tipoAbastecimiento: TipoAbastecimiento;
  fechaHora: string;
  cantidadGalones: number | null;
  kilometraje: number | null;
  precioUnitario: number | null;
  tanqueOrigen: string;
  proveedor: string;
  numeroComprobante: string;
  observacion: string;
}
