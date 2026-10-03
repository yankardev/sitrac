# SITRAC — Sistema Integral de Transporte de Carga

SITRAC es una aplicación orientada a digitalizar la preparación, programación, ejecución y control de operaciones de transporte de carga pesada.

El proyecto se desarrolla para el curso **Desarrollo de Servicios Web II** utilizando una arquitectura distribuida basada en **microservicios REST**, con **Java 21, Spring Boot, Spring MVC, Spring Data JPA, MySQL, Maven, Lombok y Postman**.

Cada microservicio mantiene una organización interna inspirada en **arquitectura hexagonal**, separando dominio, casos de uso, entrada REST y persistencia.

> Estado actual: están implementados los nueve microservicios base. Los primeros módulos ya fueron probados y los nuevos módulos quedan listos para la fase de ejecución, pruebas e integración entre servicios.

---

## 1. Flujo general de SITRAC

```text
Usuario
   ↓
Autenticación
   ↓
Cliente
   ↓
Pedido de transporte
   ↓
Conductor + Tracto + Semirremolque
   ↓
Programación
   ↓
Viaje
   ↓
Mantenimiento / SOMMA
   ↓
Trazabilidad operativa
```

---

## 2. Microservicios

| Microservicio | Puerto | Base de datos | Estado |
|---|---:|---|---|
| auth-service | 8081 | sitrac_auth | Implementado y probado |
| cliente-service | 8082 | sitrac_clientes | Implementado y probado |
| pedido-service | 8083 | sitrac_pedidos | Implementado y probado |
| conductor-service | 8084 | sitrac_conductores | Implementado, pendiente de prueba |
| flota-service | 8085 | sitrac_flota | Implementado, pendiente de prueba |
| programacion-service | 8086 | sitrac_programaciones | Implementado, pendiente de prueba e integración |
| viaje-service | 8087 | sitrac_viajes | Implementado, pendiente de prueba e integración |
| mantenimiento-service | 8088 | sitrac_mantenimiento | Implementado, pendiente de prueba e integración |
| somma-service | 8089 | sitrac_somma | Implementado, pendiente de prueba |

---

## 3. Estructura del repositorio

```text
sitrac
├── auth-service
├── cliente-service
├── pedido-service
├── conductor-service
├── flota-service
├── programacion-service
├── viaje-service
├── mantenimiento-service
├── somma-service
├── docs
├── pom.xml
└── README.md
```

El `pom.xml` raíz funciona como agregador Maven de todos los módulos.

---

## 4. Arquitectura interna de cada microservicio

```text
REST Controller
      ↓
Puerto de entrada
      ↓
Application Service
      ↓
Dominio
      ↓
Puerto de salida
      ↓
Persistence Adapter
      ↓
Spring Data JPA
      ↓
MySQL
```

Estructura típica:

```text
domain
├── model
└── port
    ├── in
    └── out

application
├── service
└── exception

infrastructure
└── adapter
    ├── in
    │   └── rest
    │       └── dto
    └── out
        └── persistence
```

---

## 5. auth-service — :8081

Responsable de autenticación y usuarios.

Funciones principales:

- Registro de usuarios.
- Inicio de sesión.
- Roles básicos.
- Contraseñas cifradas con BCrypt.
- Validación de usuario activo.

Endpoints principales:

```http
POST /api/auth/registro
POST /api/auth/login
```

---

## 6. cliente-service — :8082

Gestiona los clientes que solicitan servicios de transporte.

```http
POST   /api/clientes
GET    /api/clientes
GET    /api/clientes/{id}
PUT    /api/clientes/{id}
DELETE /api/clientes/{id}
```

Incluye datos de documento, razón social, teléfono, correo, dirección y estado.

---

## 7. pedido-service — :8083

Registra las solicitudes de transporte.

```http
POST   /api/pedidos
GET    /api/pedidos
GET    /api/pedidos/{id}
PUT    /api/pedidos/{id}
DELETE /api/pedidos/{id}
```

Tipos de carga contemplados:

```text
CAL_GRANEL
CEMENTO_BOLSA
MAQUINARIA
CARGA_ANCHA
ESPECIAL
OTRO
```

Estados:

```text
REGISTRADO
PROGRAMADO
EN_VIAJE
FINALIZADO
CANCELADO
```

---

## 8. conductor-service — :8084

Administra conductores, licencias y disponibilidad.

```http
POST   /api/conductores
GET    /api/conductores
GET    /api/conductores/{id}
PUT    /api/conductores/{id}
DELETE /api/conductores/{id}
```

Campos principales:

- DNI.
- Nombres.
- Apellidos.
- Número de licencia.
- Categoría.
- Fecha de vencimiento.
- Teléfono.
- Disponible.
- Activo.

Incluye control de DNI y licencia duplicados.

---

## 9. flota-service — :8085

Administra **tractos** y **semirremolques**.

### Tractos

```http
POST   /api/tractos
GET    /api/tractos
GET    /api/tractos/{id}
PUT    /api/tractos/{id}
DELETE /api/tractos/{id}
```

### Semirremolques

```http
POST   /api/semirremolques
GET    /api/semirremolques
GET    /api/semirremolques/{id}
PUT    /api/semirremolques/{id}
DELETE /api/semirremolques/{id}
```

Tipos de semirremolque:

```text
BOMBONA
PLATAFORMA
CAMA_BAJA
OTRO
```

Estados de unidad:

```text
DISPONIBLE
ASIGNADO
MANTENIMIENTO
INACTIVO
```

Compatibilidad prevista:

| Tipo de carga | Semirremolque |
|---|---|
| CAL_GRANEL | BOMBONA |
| CEMENTO_BOLSA | PLATAFORMA |
| MAQUINARIA | CAMA_BAJA |
| CARGA_ANCHA / ESPECIAL | CAMA_BAJA |

---

## 10. programacion-service — :8086

Relaciona el pedido con los recursos necesarios para ejecutar el servicio.

Una programación contiene:

```text
pedidoId
conductorId
tractoId
semirremolqueId
fechaProgramada
estado
```

Endpoints:

```http
POST   /api/programaciones
GET    /api/programaciones
GET    /api/programaciones/{id}
PUT    /api/programaciones/{id}
DELETE /api/programaciones/{id}
```

Estado inicial:

```text
PROGRAMADA
```

Actualmente almacena las referencias por ID. La validación distribuida contra pedido-service, conductor-service y flota-service se realizará en la fase de integración.

---

## 11. viaje-service — :8087

Administra el ciclo de vida del viaje asociado a una programación.

```http
POST   /api/viajes
GET    /api/viajes
GET    /api/viajes/{id}
PUT    /api/viajes/{id}
DELETE /api/viajes/{id}
```

Estados:

```text
PROGRAMADO
EN_VIAJE
FINALIZADO
CANCELADO
```

Incluye kilometraje inicial/final, fechas y observaciones.

---

## 12. mantenimiento-service — :8088

Registra mantenimientos de tractos y semirremolques.

```http
POST   /api/mantenimientos
GET    /api/mantenimientos
GET    /api/mantenimientos/{id}
PUT    /api/mantenimientos/{id}
DELETE /api/mantenimientos/{id}
```

Tipos:

```text
PREVENTIVO
CORRECTIVO
```

Estados:

```text
PROGRAMADO
EN_PROCESO
FINALIZADO
CANCELADO
```

---

## 13. somma-service — :8089

Gestiona registros de seguridad operacional.

```http
POST   /api/somma
GET    /api/somma
GET    /api/somma/{id}
PUT    /api/somma/{id}
DELETE /api/somma/{id}
```

Tipos de registro:

```text
CHARLA
CAPACITACION
ACCIDENTE
INCIDENTE
INSPECCION
```

---

## 14. Bases de datos

Cada microservicio utiliza su propia base lógica dentro del mismo servidor MySQL.

```text
sitrac_auth
sitrac_clientes
sitrac_pedidos
sitrac_conductores
sitrac_flota
sitrac_programaciones
sitrac_viajes
sitrac_mantenimiento
sitrac_somma
```

Con `createDatabaseIfNotExist=true`, MySQL puede crear la base correspondiente al iniciar el servicio si el usuario configurado tiene permisos suficientes.

---

## 15. Variables de entorno

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
```

Las credenciales reales no deben almacenarse en Git.

---

## 16. Ejecución con Maven

Desde la raíz del repositorio:

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
```

También pueden ejecutarse desde las clases `*ServiceApplication` de IntelliJ IDEA.

---

## 17. Reglas de negocio que se integrarán

La fase siguiente conectará los microservicios para aplicar reglas como:

- Un conductor no debe tener dos viajes activos simultáneamente.
- Una unidad no debe participar en dos viajes activos.
- Un tracto en mantenimiento no debe ser programado.
- La licencia del conductor debe estar vigente.
- El semirremolque debe ser compatible con el tipo de carga.
- La capacidad debe ser suficiente para el tonelaje solicitado.
- Un viaje debe provenir de una programación válida.
- Al iniciar/finalizar un viaje deberán actualizarse los estados operativos relacionados.

Estas reglas requieren comunicación entre microservicios; no se consideran todavía verificadas solo por tener los CRUD separados.

---

## 18. Próxima fase

1. Ejecutar cada microservicio.
2. Confirmar creación automática de sus bases/tablas.
3. Probar POST, GET, PUT y DELETE en Postman.
4. Corregir errores encontrados.
5. Integrar programacion-service con pedidos, conductores y flota.
6. Integrar viaje-service con programación.
7. Integrar mantenimiento-service con el estado de las unidades.
8. Completar documentación, evidencias y despliegue.

---

**SITRAC — Sistema Integral de Transporte de Carga**  
Desarrollo de Servicios Web II  
YANKARDEV
