export type TipoDocumento = 'DNI' | 'RUC' | 'CE' | 'OTRO';

export interface Cliente {
  id: number;
  tipoDocumento: TipoDocumento;
  numeroDocumento: string;
  nombreRazonSocial: string;
  telefono: string | null;
  email: string | null;
  direccion: string | null;
  activo: boolean;
}

export interface ClienteForm {
  tipoDocumento: TipoDocumento;
  numeroDocumento: string;
  nombreRazonSocial: string;
  telefono: string;
  email: string;
  direccion: string;
  activo: boolean;
}
