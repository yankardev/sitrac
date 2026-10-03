# SITRAC — Sistema Integral de Transporte de Carga

SITRAC es una aplicación orientada a digitalizar la preparación, programación, ejecución y control de operaciones de transporte de carga pesada.

El proyecto se desarrolla para el curso **Desarrollo de Servicios Web II** utilizando una arquitectura distribuida basada en **microservicios REST**, con **Java 21, Spring Boot, Spring MVC, Spring Data JPA, MySQL, Maven, Lombok y Postman**.

Cada microservicio mantiene una organización interna inspirada en **arquitectura hexagonal**, separando dominio, casos de uso, entrada REST y persistencia.

> Estado actual: están implementados los diez microservicios base. La siguiente fase es completar pruebas CRUD e integrar las reglas de negocio entre servicios.

---

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

## combustible-service — :8090

Registra abastecimientos de combustible asociados a una programación de transporte.

```http
POST   /api/combustible/abastecimientos
GET    /api/combustible/abastecimientos
GET    /api/combustible/abastecimientos/{id}
PUT    /api/combustible/abastecimientos/{id}
DELETE /api/combustible/abastecimientos/{id}
```

Tipos de abastecimiento:

```text
INTERNO
TERCERO
```

Reglas base:

- Todo abastecimiento registra programación, conductor y tracto.
- INTERNO exige tanque de origen.
- TERCERO exige proveedor y número de comprobante.
- El costo total se calcula con cantidad de galones por precio unitario cuando se informa precio.
- La validación distribuida de programación, conductor, tracto y viaje se implementará en la fase de integración.

## Variables de entorno

```text
MYSQL_USER
MYSQL_PASSWORD
MYSQL_URL
MYSQL_CLIENTE_URL
MYSQL_PEDIDO_URL
MYSQL_CONDUCTOR_URL
MYSQL_FLOTA_URL
MYSQL_PROGRAMACION_URL
MYSQL_VIAJE_URL
MYSQL_MANTENIMIENTO_URL
MYSQL_SOMMA_URL
MYSQL_COMBUSTIBLE_URL
```

## Ejecución

```powershell
mvnw.cmd -pl combustible-service spring-boot:run
```

**SITRAC — Sistema Integral de Transporte de Carga**  
Desarrollo de Servicios Web II  
YANKARDEV
