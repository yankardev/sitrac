export function confirmarAccionDestructiva(
  alerta: string,
  confirmacion: string
): boolean {
  window.alert(alerta);
  return window.confirm(confirmacion);
}
