export interface Conductor {
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

export interface ConductorForm {
  dni: string;
  nombres: string;
  apellidos: string;
  numeroLicencia: string;
  categoriaLicencia: string;
  fechaVencimientoLicencia: string;
  telefono: string;
  disponible: boolean;
  activo: boolean;
}
