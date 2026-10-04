# SITRAC — Sistema Integral de Transporte de Carga

SITRAC es una aplicación orientada a digitalizar la preparación, programación, ejecución y control de operaciones de transporte de carga pesada.

El proyecto se desarrolla para el curso **Desarrollo de Servicios Web II** utilizando una arquitectura distribuida basada en **microservicios REST**, con **Java 21, Spring Boot, Spring MVC, Spring Data JPA, MySQL, Maven, Lombok y Postman**.

Cada microservicio mantiene una organización interna inspirada en **arquitectura hexagonal**, separando dominio, casos de uso, entrada REST y persistencia.

> Estado actual: están implementados diez microservicios base. Ya están integrados programacion-service con pedido/conductor/flota y viaje-service con programacion-service.

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

Antes de crear una programación, el servicio consulta por REST a:

- pedido-service (:8083)
- conductor-service (:8084)
- flota-service (:8085)

Se validan:

- existencia y estado del pedido;
- conductor activo, disponible y con licencia vigente;
- tracto activo y disponible;
- semirremolque activo y disponible;
- capacidad del tracto y semirremolque frente al tonelaje solicitado;
- compatibilidad de tipo de carga con semirremolque;
- que pedido, conductor, tracto y semirremolque no tengan otra programación activa.

Compatibilidad:

| Tipo de carga | Semirremolque requerido |
|---|---|
| CAL_GRANEL | BOMBONA |
| CEMENTO_BOLSA | PLATAFORMA |
| MAQUINARIA | CAMA_BAJA |
| CARGA_ANCHA | CAMA_BAJA |
| ESPECIAL | CAMA_BAJA |

URLs configurables:

```text
PEDIDO_SERVICE_URL=http://localhost:8083
CONDUCTOR_SERVICE_URL=http://localhost:8084
FLOTA_SERVICE_URL=http://localhost:8085
```

## Integración de viaje-service

Antes de crear un viaje, viaje-service consulta por REST a programacion-service (:8086).

Valida:

- que la programación exista;
- que la programación esté en estado PROGRAMADA;
- que no exista otro viaje asociado a la misma programación;
- coherencia de kilometraje inicial y final.

URL configurable:

```text
PROGRAMACION_SERVICE_URL=http://localhost:8086
```

## combustible-service — :8090

```http
POST   /api/combustible/abastecimientos
GET    /api/combustible/abastecimientos
GET    /api/combustible/abastecimientos/{id}
PUT    /api/combustible/abastecimientos/{id}
DELETE /api/combustible/abastecimientos/{id}
```

Tipos:

```text
INTERNO
TERCERO
```

La siguiente fase conectará SOMMA, mantenimiento y combustible con la programación validada.

**SITRAC — Sistema Integral de Transporte de Carga**  
Desarrollo de Servicios Web II  
YANKARDEV
