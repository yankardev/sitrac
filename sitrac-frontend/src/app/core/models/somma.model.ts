export type TipoRegistroSomma = 'CHARLA' | 'CAPACITACION' | 'ACCIDENTE' | 'INCIDENTE' | 'INSPECCION';
export type EstadoRegistroSomma = 'REGISTRADO' | 'CERRADO' | 'CANCELADO';

export interface RegistroSomma {
  id: number;
  tipo: TipoRegistroSomma;
  programacionId: number | null;
  conductorId: number | null;
  fecha: string;
  titulo: string;
  descripcion: string;
  lugar: string | null;
  estado: EstadoRegistroSomma;
}

export interface RegistroSommaForm {
  tipo: TipoRegistroSomma;
  programacionId: number | null;
  conductorId: number | null;
  fecha: string;
  titulo: string;
  descripcion: string;
  lugar: string;
  estado: EstadoRegistroSomma | null;
}
