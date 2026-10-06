# SITRAC — Sistema Integral de Transporte de Carga

SITRAC es una aplicación orientada a digitalizar la preparación, programación, ejecución y control de operaciones de transporte de carga pesada.

El proyecto se desarrolla para el curso **Desarrollo de Servicios Web II** utilizando una arquitectura distribuida basada en **microservicios REST**, con **Java 21, Spring Boot, Spring MVC, Spring Data JPA, MySQL, Maven, Lombok y Postman**.

Cada microservicio mantiene una organización interna inspirada en **arquitectura hexagonal**, separando dominio, casos de uso, entrada REST y persistencia.

> Estado actual: están implementados diez microservicios. El flujo principal ya integra pedidos, conductores, flota, programación, SOMMA, viajes, mantenimiento y combustible.

## Microservicios

| Microservicio | Puerto | Base de datos |
|---|---:|---|
| auth-service | 8081 | sitrac_auth |
| cliente-service | 8082 | sitrac_clientes |
| pedido-service | 8083 | sitrac_pedidos |
| conductor-service | 8084 | sitrac_conductores |
| flota-service | 8085 | sitrac_flota |
| programacion-service | 8086 | sitrac_programaciones |
| viaje-service | 8087 | sitrac_viajes |
| mantenimiento-service | 8088 | sitrac_mantenimiento |
| somma-service | 8089 | sitrac_somma |
| combustible-service | 8090 | sitrac_combustible |

## Flujo operativo actual

```text
Cliente
  ↓
Pedido REGISTRADO
  ↓
Programación
  ↓
Validación de conductor + tracto + semirremolque
  ↓
Pedido PROGRAMADO + recursos ASIGNADOS
  ↓
Charla SOMMA CERRADA
  ↓
Viaje PROGRAMADO
  ↓
Inicio del viaje
  ↓
Pedido EN_VIAJE
  ↓
Combustible / controles operativos
  ↓
Finalización del viaje
  ↓
Pedido FINALIZADO + programación FINALIZADA
  ↓
Conductor y unidades nuevamente DISPONIBLES
```

La cancelación también sincroniza el estado de la programación y libera los recursos. Si el viaje aún no había iniciado, el pedido vuelve a `REGISTRADO`; si ya estaba en ejecución, pasa a `CANCELADO`.

## Integración de programacion-service

Antes de crear una programación, el servicio consulta por REST a `pedido-service`, `conductor-service` y `flota-service`.

Valida:

- existencia y estado del pedido;
- conductor activo y disponible;
- licencia vigente;
- tracto y semirremolque disponibles;
- capacidad suficiente para la carga;
- compatibilidad entre tipo de carga y semirremolque;
- ausencia de otra programación activa para el pedido o los recursos asignados.

Al crear la programación:

- el pedido pasa a `PROGRAMADO`;
- el conductor deja de estar disponible;
- tracto y semirremolque pasan a `ASIGNADO`.

Al finalizar el viaje, `programacion-service` finaliza el pedido, marca la programación como `FINALIZADA` y libera conductor, tracto y semirremolque.

## Integración de viaje-service

Antes de crear un viaje, `viaje-service` consulta por REST a `programacion-service` (:8086).

Valida que:

- la programación exista;
- esté en estado `PROGRAMADA`;
- no tenga otro viaje asociado.

El ciclo de vida del viaje es:

```text
PROGRAMADO → EN_VIAJE → FINALIZADO
        └──────────────→ CANCELADO
```

Para pasar a `EN_VIAJE` se exige una charla SOMMA cerrada para la programación. Los cambios de estado del viaje se sincronizan con `programacion-service`, que a su vez actualiza el pedido y los recursos.

Para no romper la trazabilidad operativa, un viaje solo puede eliminarse después de haber sido cancelado correctamente.

## Integración de somma-service

Para registros de tipo `CHARLA`, `somma-service` consulta por REST a `programacion-service` (:8086).

Valida:

- que la charla tenga una programación asociada;
- que exista la programación;
- que esté en estado `PROGRAMADA`;
- que se indique un conductor;
- que el conductor corresponda al asignado en la programación.

La columna `programacion_id` mantiene la trazabilidad entre seguridad y operación.

## Integración de mantenimiento-service

`mantenimiento-service` consulta a `flota-service` antes de trabajar con una unidad.

Cuando un mantenimiento pasa a `EN_PROCESO`, la unidad pasa a `MANTENIMIENTO`. Al finalizar o cancelar el mantenimiento, la unidad vuelve a `DISPONIBLE`.

Para proteger el flujo operativo, una unidad que ya se encuentra asignada no puede ser tomada para iniciar un mantenimiento, y no se permite cambiar de unidad mientras el mantenimiento está en proceso.

## Integración de combustible-service

`combustible-service` valida por REST la programación asociada antes de registrar un abastecimiento.

Comprueba:

- que la programación exista;
- que esté `PROGRAMADA`;
- que el conductor corresponda a la programación;
- que el tracto corresponda a la programación.

Endpoints principales:

```http
POST   /api/combustible/abastecimientos
GET    /api/combustible/abastecimientos
GET    /api/combustible/abastecimientos/{id}
PUT    /api/combustible/abastecimientos/{id}
DELETE /api/combustible/abastecimientos/{id}
```

## Protección de integridad operativa

Para evitar dejar referencias rotas entre microservicios:

- no se puede eliminar un pedido que ya está dentro de una operación activa o finalizada;
- no se puede eliminar un conductor mientras esté asignado;
- no se puede eliminar un tracto o semirremolque mientras esté asignado o en mantenimiento;
- no se puede eliminar directamente un viaje activo: primero debe cancelarse para que SITRAC libere correctamente los recursos.

## Ejecución local

Cada microservicio utiliza su propio puerto y su propia base MySQL. Las URL entre servicios se pueden sobrescribir mediante variables de entorno y, en desarrollo local, apuntan a `localhost`.

Ejemplo de ejecución desde la raíz:

```powershell
mvnw.cmd -pl auth-service spring-boot:run
mvnw.cmd -pl cliente-service spring-boot:run
mvnw.cmd -pl pedido-service spring-boot:run
mvnw.cmd -pl conductor-service spring-boot:run
mvnw.cmd -pl flota-service spring-boot:run
mvnw.cmd -pl programacion-service spring-boot:run
mvnw.cmd -pl viaje-service spring-boot:run
mvnw.cmd -pl mantenimiento-service spring-boot:run
mvnw.cmd -pl somma-service spring-boot:run
mvnw.cmd -pl combustible-service spring-boot:run
```

**SITRAC — Sistema Integral de Transporte de Carga**  
Desarrollo de Servicios Web II  
YANKARDEV
