export type EstadoProgramacion = 'PROGRAMADA' | 'FINALIZADA' | 'CANCELADA';

export interface Programacion {
  id: number;
  pedidoId: number;
  conductorId: number;
  tractoId: number;
  semirremolqueId: number;
  fechaProgramada: string;
  observacion: string | null;
  estado: EstadoProgramacion;
}

export interface ProgramacionForm {
  pedidoId: number | null;
  conductorId: number | null;
  tractoId: number | null;
  semirremolqueId: number | null;
  fechaProgramada: string;
  observacion: string;
  estado: EstadoProgramacion | null;
}

export interface ConductorOperacion {
  id: number;
  dni: string;
  nombres: string;
  apellidos: string;
  numeroLicencia: string;
  categoriaLicencia: string;
  fechaVencimientoLicencia: string;
  telefono: string | null;
  disponible: boolean;
  activo: boolean;
}

export interface TractoOperacion {
  id: number;
  placa: string;
  marca: string;
  modelo: string;
  anio: number;
  capacidadToneladas: number;
  estado: 'DISPONIBLE' | 'ASIGNADO' | 'MANTENIMIENTO' | 'INACTIVO';
  activo: boolean;
}

export interface SemirremolqueOperacion {
  id: number;
  placa: string;
  tipo: string;
  capacidadToneladas: number;
  estado: 'DISPONIBLE' | 'ASIGNADO' | 'MANTENIMIENTO' | 'INACTIVO';
  activo: boolean;
}
