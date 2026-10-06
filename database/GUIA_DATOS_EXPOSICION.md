# SITRAC - Guía de datos para exposición

Esta guía acompaña al archivo `sitrac_demo_exposicion.sql`.

## Objetivo

Dejar SITRAC con una base pequeña, coherente y fácil de explicar durante la exposición. El script elimina y recrea las 10 bases del proyecto y carga datos mínimos representativos.

## Volumen de datos

- 5 usuarios del sistema, uno por rol principal.
- 10 clientes.
- 10 pedidos.
- 10 conductores.
- 10 unidades de flota: 5 tractos y 5 semirremolques.
- 3 programaciones.
- 3 viajes.
- 2 mantenimientos.
- 5 registros SOMMA.
- 2 abastecimientos de combustible.

## Credenciales

| Rol | Usuario | Contraseña |
|---|---|---|
| ADMIN | `admin.sitrac` | `Admin123*` |
| OPERADOR | `operador.sitrac` | `Operador123*` |
| SOMMA | `somma.sitrac` | `Somma123*` |
| MANTENIMIENTO | `mantenimiento.sitrac` | `Mantenimiento123*` |
| SUPERVISOR | `supervisor.sitrac` | `Supervisor123*` |

## Escenarios listos para demostrar

### 1. Flujo cerrado completo

- Pedido: `PED-0001`
- Cliente: CEMENTOS PACASMAYO S.A.A.
- Carga: CAL_GRANEL
- Programación: `PROG-0001`
- Viaje: `VIA-0001`
- Estado final: FINALIZADO
- Tracto: `T1A-201`
- Semirremolque: `S1A-301` BOMBONA
- Conductor: JUAN CARLOS RAMIREZ TORRES

Sirve para explicar todo el ciclo ya concluido y cómo los recursos vuelven a DISPONIBLE.

### 2. Operación programada, lista para iniciar

- Pedido: `PED-0002`
- Carga: CEMENTO_BOLSA
- Programación: `PROG-0002`
- Viaje: `VIA-0002`
- Estado del viaje: PROGRAMADO
- Tracto: `T1A-202`
- Semirremolque: `S1A-302` PLATAFORMA
- Charla SOMMA: CERRADA

Sirve para demostrar que CEMENTO_BOLSA requiere PLATAFORMA y que el viaje ya puede iniciarse porque la charla SOMMA está cerrada.

### 3. Viaje actualmente en ejecución

- Pedido: `PED-0003`
- Carga: MAQUINARIA
- Programación: `PROG-0003`
- Viaje: `VIA-0003`
- Estado: EN_VIAJE
- Tracto: `T1A-203`
- Semirremolque: `S1A-303` CAMA_BAJA
- Conductor: MIGUEL ALEJANDRO ROJAS MENDOZA

Sirve para demostrar combustible y finalización de viaje.

### 4. Mantenimiento enlazado con Flota

- Tracto: `T1A-204`
- Estado de flota: MANTENIMIENTO
- Mantenimiento: `MANT-0001`
- Tipo: CORRECTIVO
- Estado: EN_PROCESO

Sirve para explicar que una unidad en mantenimiento deja de estar disponible para una programación.

### 5. Pedidos libres para demostración en vivo

`PED-0004` a `PED-0010` permanecen REGISTRADOS. Se pueden usar para crear una nueva programación durante la exposición.

Hay recursos disponibles para nuevas pruebas:

- Tracto disponible: `T1A-205`
- Semirremolque BOMBONA disponible: `S1A-304`
- Semirremolque CAMA_BAJA disponible: `S1A-305`
- Conductores disponibles: IDs 4 al 10.

## Reglas de negocio fáciles de explicar

- CAL_GRANEL -> BOMBONA.
- CEMENTO_BOLSA -> PLATAFORMA.
- MAQUINARIA / CARGA_ANCHA / ESPECIAL -> CAMA_BAJA.
- Una programación reserva conductor, tracto y semirremolque.
- Para iniciar el viaje debe existir una CHARLA SOMMA CERRADA.
- Al finalizar el viaje se finalizan el pedido y la programación y se liberan los recursos.
- Una unidad EN_PROCESO de mantenimiento pasa a estado MANTENIMIENTO y no puede ser programada.

## Restauración en Windows / Git Bash

Desde la raíz del proyecto:

```bash
"/c/Program Files/MySQL/MySQL Server 9.4/bin/mysql.exe" -u root -p < database/sitrac_demo_exposicion.sql
```

Después verificar:

```bash
"/c/Program Files/MySQL/MySQL Server 9.4/bin/mysql.exe" -u root -p -e "SELECT COUNT(*) clientes FROM sitrac_clientes.clientes; SELECT COUNT(*) pedidos FROM sitrac_pedidos.pedidos; SELECT (SELECT COUNT(*) FROM sitrac_flota.tractos) + (SELECT COUNT(*) FROM sitrac_flota.semirremolques) AS unidades; SELECT COUNT(*) conductores FROM sitrac_conductores.conductores;"
```

Resultado esperado:

- clientes = 10
- pedidos = 10
- unidades = 10
- conductores = 10

## Nota sobre privacidad

Los nombres de empresas se usan como ejemplos del sector. Los datos personales de conductores, DNI, teléfonos y licencias son ficticios para no publicar información personal real en el repositorio.
