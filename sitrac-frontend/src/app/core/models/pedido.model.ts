export type TipoCarga =
  | 'CAL_GRANEL'
  | 'CEMENTO_BOLSA'
  | 'MAQUINARIA'
  | 'CARGA_ANCHA'
  | 'ESPECIAL'
  | 'OTRO';

export type EstadoPedido =
  | 'REGISTRADO'
  | 'PROGRAMADO'
  | 'EN_VIAJE'
  | 'FINALIZADO'
  | 'CANCELADO';

export interface Pedido {
  id: number;
  clienteId: number;
  tipoCarga: TipoCarga;
  descripcionCarga: string | null;
  toneladas: number;
  origen: string;
  destino: string;
  fechaSolicitud: string;
  estado: EstadoPedido;
}

export interface PedidoForm {
  clienteId: number | null;
  tipoCarga: TipoCarga;
  descripcionCarga: string;
  toneladas: number | null;
  origen: string;
  destino: string;
  fechaSolicitud: string;
  estado: EstadoPedido | null;
}
