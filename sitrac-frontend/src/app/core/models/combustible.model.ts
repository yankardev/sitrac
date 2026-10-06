export type TipoAbastecimiento = 'INTERNO' | 'TERCERO';

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
