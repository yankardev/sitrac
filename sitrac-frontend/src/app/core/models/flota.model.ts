export type EstadoUnidad = 'DISPONIBLE' | 'ASIGNADO' | 'MANTENIMIENTO' | 'INACTIVO';
export type TipoSemirremolque = 'BOMBONA' | 'PLATAFORMA' | 'CAMA_BAJA' | 'OTRO';

export interface Tracto {
  id: number;
  placa: string;
  marca: string;
  modelo: string;
  anio: number;
  capacidadToneladas: number;
  estado: EstadoUnidad;
  activo: boolean;
}

export interface TractoForm {
  placa: string;
  marca: string;
  modelo: string;
  anio: number | null;
  capacidadToneladas: number | null;
  estado: EstadoUnidad;
  activo: boolean;
}

export interface Semirremolque {
  id: number;
  placa: string;
  tipo: TipoSemirremolque;
  capacidadToneladas: number;
  estado: EstadoUnidad;
  activo: boolean;
}

export interface SemirremolqueForm {
  placa: string;
  tipo: TipoSemirremolque;
  capacidadToneladas: number | null;
  estado: EstadoUnidad;
  activo: boolean;
}
