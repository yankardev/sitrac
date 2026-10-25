export interface Conductor {
  id: number;
  dni: string;
  nombres: string;
  apellidos: string;
  numeroLicencia: string;
  categoriaLicencia: string;
  fechaVencimientoLicencia: string;
  telefono: string | null;
  email: string | null;
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
  email: string;
  disponible: boolean;
  activo: boolean;
}
