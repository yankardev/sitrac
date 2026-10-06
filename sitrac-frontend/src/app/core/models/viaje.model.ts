export type EstadoViaje = 'PROGRAMADO' | 'EN_VIAJE' | 'FINALIZADO' | 'CANCELADO';

export interface Viaje {
  id: number;
  programacionId: number;
  fechaInicio: string | null;
  fechaFin: string | null;
  kilometrajeInicial: number | null;
  kilometrajeFinal: number | null;
  observacion: string | null;
  estado: EstadoViaje;
}

export interface ViajeForm {
  programacionId: number;
  fechaInicio: string | null;
  fechaFin: string | null;
  kilometrajeInicial: number | null;
  kilometrajeFinal: number | null;
  observacion: string;
  estado: EstadoViaje | null;
}
