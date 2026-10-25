export type EstadoNotificacion = 'ENVIADA' | 'SIMULADA' | 'OMITIDA' | 'ERROR';

export interface RegistroNotificacion {
  id: number;
  tipoEvento: string;
  referencia: string;
  destinatario: string;
  asunto: string;
  estado: EstadoNotificacion;
  detalle: string;
  tieneAdjunto: boolean;
  fechaRegistro: string;
}
