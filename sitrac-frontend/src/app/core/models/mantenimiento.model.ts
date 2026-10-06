export type TipoUnidad = 'TRACTO' | 'SEMIRREMOLQUE';
export type TipoMantenimiento = 'PREVENTIVO' | 'CORRECTIVO';
export type EstadoMantenimiento = 'PROGRAMADO' | 'EN_PROCESO' | 'FINALIZADO' | 'CANCELADO';

export interface Mantenimiento {
  id: number;
  tipoUnidad: TipoUnidad;
  unidadId: number;
  tipoMantenimiento: TipoMantenimiento;
  fechaInicio: string;
  fechaFin: string | null;
  descripcion: string;
  costo: number | null;
  estado: EstadoMantenimiento;
}

export interface MantenimientoForm {
  tipoUnidad: TipoUnidad;
  unidadId: number | null;
  tipoMantenimiento: TipoMantenimiento;
  fechaInicio: string;
  fechaFin: string | null;
  descripcion: string;
  costo: number | null;
  estado: EstadoMantenimiento | null;
}
