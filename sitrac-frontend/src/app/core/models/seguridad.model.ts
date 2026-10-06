import { RolUsuario } from './auth.model';

export interface UsuarioSeguridad {
  id: number;
  username: string;
  nombreCompleto: string;
  rol: RolUsuario;
  activo: boolean;
}

export interface UsuarioSeguridadForm {
  username: string;
  nombreCompleto: string;
  rol: RolUsuario;
  password: string;
}
