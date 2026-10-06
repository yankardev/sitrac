export type RolUsuario = 'ADMIN' | 'OPERADOR' | 'SOMMA' | 'MANTENIMIENTO' | 'SUPERVISOR';

export interface LoginRequest {
  username: string;
  password: string;
}

export interface UsuarioSesion {
  id: number;
  username: string;
  nombreCompleto: string;
  rol: RolUsuario;
}

export interface RegistroUsuarioRequest {
  username: string;
  password: string;
  nombreCompleto: string;
  rol: RolUsuario;
}

export interface UsuarioRegistrado extends UsuarioSesion {
  activo: boolean;
}
