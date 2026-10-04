# SITRAC — Sistema Integral de Transporte de Carga

SITRAC es una aplicación orientada a digitalizar la preparación, programación, ejecución y control de operaciones de transporte de carga pesada.

El proyecto se desarrolla para el curso **Desarrollo de Servicios Web II** utilizando una arquitectura distribuida basada en **microservicios REST**, con **Java 21, Spring Boot, Spring MVC, Spring Data JPA, MySQL, Maven, Lombok y Postman**.

Cada microservicio mantiene una organización interna inspirada en **arquitectura hexagonal**, separando dominio, casos de uso, entrada REST y persistencia.

> Estado actual: están implementados diez microservicios base. Ya están integrados programacion-service con pedido/conductor/flota, viaje-service con programacion-service y SOMMA con la programación operativa.

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

## Flujo operativo previsto

```text
Cliente
  ↓
Pedido
  ↓
Programación
  ↓
Validación de conductor + tracto + semirremolque
  ↓
Charla SOMMA
  ↓
Abastecimiento de combustible / gastos
  ↓
Documentación
  ↓
Viaje
  ↓
Cierre y trazabilidad
```

## Integración de programacion-service

Antes de crear una programación, el servicio consulta por REST a pedido-service, conductor-service y flota-service.

Valida existencia y estado del pedido, conductor activo/disponible con licencia vigente, disponibilidad y capacidad de tracto y semirremolque, compatibilidad de carga y ausencia de otra programación activa para los recursos asignados.

## Integración de viaje-service

Antes de crear un viaje, viaje-service consulta por REST a programacion-service (:8086).

Valida que la programación exista, esté en estado PROGRAMADA y no tenga otro viaje asociado.

## Integración de somma-service

Para registros de tipo CHARLA, somma-service consulta por REST a programacion-service (:8086).

Valida:

- que la charla tenga una programación asociada;
- que exista la programación;
- que esté en estado PROGRAMADA;
- que se indique un conductor;
- que el conductor corresponda al asignado en la programación.

La columna `programacion_id` se incorpora a `registros_somma` para mantener trazabilidad entre seguridad y operación.

## combustible-service — :8090

```http
POST   /api/combustible/abastecimientos
GET    /api/combustible/abastecimientos
GET    /api/combustible/abastecimientos/{id}
PUT    /api/combustible/abastecimientos/{id}
DELETE /api/combustible/abastecimientos/{id}
```

La siguiente fase conectará mantenimiento y combustible con la programación validada.

**SITRAC — Sistema Integral de Transporte de Carga**  
Desarrollo de Servicios Web II  
YANKARDEV
