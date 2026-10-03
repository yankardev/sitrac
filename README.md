# SITRAC — Sistema Integral de Transporte de Carga

Proyecto académico desarrollado para el curso **Desarrollo de Servicios Web II**.

SITRAC busca digitalizar la preparación, programación, ejecución y control de operaciones de transporte de carga pesada. La aplicación se está construyendo con una arquitectura de **microservicios REST**, usando **Spring Boot**, **Spring MVC**, **Spring Data JPA**, **MySQL** y una organización interna basada en **arquitectura hexagonal**.

> Estado actual: el proyecto ya cuenta con los microservicios de autenticación, clientes, pedidos y conductores. Los demás módulos se incorporarán progresivamente.

---

## 1. ¿Qué problema resuelve?

En una operación de transporte pesado intervienen clientes, pedidos, conductores, tractos, semirremolques, viajes, documentos, gastos, mantenimiento y controles de seguridad. Cuando esta información se administra de forma dispersa, es difícil tener trazabilidad y validar que una unidad pueda realizar un viaje.

SITRAC centraliza ese proceso para que una operación pueda seguir un flujo como:

```text
Cliente
   ↓
Pedido de transporte
   ↓
Programación
   ↓
Asignación de conductor + tracto + semirremolque
   ↓
Validación de disponibilidad y documentos
   ↓
Viaje
   ↓
Control operativo
   ↓
Cierre y trazabilidad
```

---

## 2. Arquitectura general

La solución se plantea como un conjunto de microservicios independientes.

```text
                    CLIENTE / POSTMAN / FRONTEND
                               │
                               ▼
                    APIs REST de SITRAC
                               │
        ┌──────────────────────┼──────────────────────┐
        ▼                      ▼                      ▼
  auth-service          cliente-service        pedido-service
     :8081                  :8082                  :8083
        │                      │                      │
        ▼                      ▼                      ▼
  sitrac_auth          sitrac_clientes         sitrac_pedidos
        │                      │                      │
        └──────────────────── MySQL ─────────────────┘
```

Cada microservicio organiza internamente su código con **arquitectura hexagonal**:

```text
REST / Controller
       ↓
Puerto de entrada
       ↓
Caso de uso / Application Service
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

### Tecnologías

- Java 21
- Spring Boot
- Spring MVC / REST
- Spring Data JPA
- Spring Security
- BCryptPasswordEncoder
- MySQL
- Maven
- Lombok
- Jakarta Validation
- Postman
- IntelliJ IDEA
- Git / GitHub

---

## 3. Estructura del repositorio

```text
sitrac
│
├── auth-service
│   └── Autenticación, usuarios y roles
│
├── cliente-service
│   └── Gestión CRUD de clientes
│
├── pedido-service
│   └── Gestión CRUD de pedidos de transporte
│
├── conductor-service
│   └── Gestión CRUD de conductores, licencias y disponibilidad
│
├── docs
│   └── Diagramas de arquitectura
│
├── pom.xml
└── README.md
```

El `pom.xml` de la raíz funciona como agregador Maven de los microservicios.

---

## 4. Microservicios implementados

### 4.1 auth-service — puerto 8081

Responsable del acceso al sistema.

Funciones actuales:

- Registro de usuarios.
- Inicio de sesión.
- Roles básicos.
- Contraseñas almacenadas con BCrypt.
- Validación de usuario activo.

Endpoints:

```http
POST /api/auth/registro
POST /api/auth/login
```

Base de datos:

```text
sitrac_auth
```

Ejemplo de registro:

```json
{
  "username": "admin",
  "password": "123456",
  "nombreCompleto": "Administrador SITRAC",
  "rol": "ADMIN"
}
```

La contraseña recibida nunca se devuelve en la respuesta REST y se almacena cifrada mediante BCrypt.

---

### 4.2 cliente-service — puerto 8082

Responsable de administrar los clientes que solicitan servicios de transporte.

CRUD REST disponible:

```http
POST   /api/clientes
GET    /api/clientes
GET    /api/clientes/{id}
PUT    /api/clientes/{id}
DELETE /api/clientes/{id}
```

Base de datos:

```text
sitrac_clientes
```

Datos principales del cliente:

- Tipo de documento.
- Número de documento.
- Nombre o razón social.
- Teléfono.
- Correo.
- Dirección.
- Estado activo/inactivo.

Incluye validaciones y control de documentos duplicados.

---

### 4.3 pedido-service — puerto 8083

Responsable de registrar las solicitudes de transporte realizadas por los clientes.

CRUD REST disponible:

```http
POST   /api/pedidos
GET    /api/pedidos
GET    /api/pedidos/{id}
PUT    /api/pedidos/{id}
DELETE /api/pedidos/{id}
```

Base de datos:

```text
sitrac_pedidos
```

Datos principales del pedido:

- Cliente asociado mediante `clienteId`.
- Tipo de carga.
- Descripción.
- Toneladas.
- Origen.
- Destino.
- Fecha de solicitud.
- Estado.

Estados contemplados:

```text
REGISTRADO
PROGRAMADO
EN_VIAJE
FINALIZADO
CANCELADO
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

Al crear un pedido, el estado inicial se asigna automáticamente como `REGISTRADO`.

---

### 4.4 conductor-service — puerto 8084

Responsable de administrar los conductores habilitados para las operaciones de transporte.

CRUD REST disponible:

```http
POST   /api/conductores
GET    /api/conductores
GET    /api/conductores/{id}
PUT    /api/conductores/{id}
DELETE /api/conductores/{id}
```

Base de datos:

```text
sitrac_conductores
```

Datos principales del conductor:

- DNI.
- Nombres y apellidos.
- Número y categoría de licencia.
- Fecha de vencimiento de licencia.
- Teléfono.
- Disponibilidad.
- Estado activo/inactivo.

Incluye validaciones para evitar DNI y números de licencia duplicados.

---

## 5. Flujo funcional previsto de SITRAC

El proyecto completo apunta a cubrir este flujo:

```text
1. Usuario inicia sesión
2. Se registra o selecciona un cliente
3. Se registra un pedido
4. Se identifica el tipo de carga
5. Se programa el servicio
6. Se asigna conductor
7. Se asigna tracto
8. Se asigna semirremolque compatible
9. Se validan disponibilidad y documentos
10. Se inicia el viaje
11. Se registran controles operativos
12. Se finaliza el viaje
13. La información queda disponible para trazabilidad y reportes
```

---

## 6. Reglas de negocio previstas

Entre las reglas que deberá controlar el sistema se encuentran:

- Un conductor no debe tener viajes simultáneos.
- Un vehículo no debe estar asignado a dos viajes activos al mismo tiempo.
- Un tracto en mantenimiento no debe poder programarse.
- Los documentos obligatorios deben estar vigentes.
- La capacidad disponible debe ser suficiente para la carga.
- El semirremolque debe ser compatible con el tipo de carga.
- Un viaje debe estar previamente programado antes de iniciarse.

Compatibilidad de carga prevista:

| Tipo de carga | Semirremolque esperado |
|---|---|
| Cal a granel | Bombona |
| Cemento en bolsa | Plataforma |
| Maquinaria | Cama baja |
| Carga ancha o especial | Cama baja |

Estas reglas se implementarán en los módulos de flota, programación y viaje.

---

## 7. Microservicios planificados

La arquitectura completa contempla continuar con:

```text
flota-service
programacion-service
viaje-service
mantenimiento-service
somma-service
```

### flota-service

Administrará tractos, semirremolques, capacidades, tipos y estados.

### programacion-service

Relacionará pedido, conductor, tracto y semirremolque y aplicará las principales reglas de asignación.

### viaje-service

Controlará el ciclo de vida del servicio:

```text
PROGRAMADO → EN_VIAJE → FINALIZADO
```

### mantenimiento-service

Controlará mantenimientos y disponibilidad mecánica de las unidades.

### somma-service

Módulo de seguridad operacional para registrar, entre otros:

- Charlas a conductores.
- Capacitaciones.
- Accidentes.
- Incidentes.
- Inspecciones.

---

## 8. Cómo ejecutar el proyecto

### Requisitos

- JDK 21 o superior compatible con el proyecto.
- MySQL.
- IntelliJ IDEA o IDE equivalente.
- Maven Wrapper incluido en el repositorio.
- Postman para pruebas REST.

### Variables de entorno

Para no guardar credenciales en Git se utilizan:

```text
MYSQL_USER
MYSQL_PASSWORD
MYSQL_URL
MYSQL_CLIENTE_URL
MYSQL_PEDIDO_URL
MYSQL_CONDUCTOR_URL
```

Ejemplo:

```text
MYSQL_USER=root
MYSQL_PASSWORD=tu_password
```

### Ejecutar auth-service

```powershell
mvnw.cmd -pl auth-service spring-boot:run
```

### Ejecutar cliente-service

```powershell
mvnw.cmd -pl cliente-service spring-boot:run
```

### Ejecutar pedido-service

```powershell
mvnw.cmd -pl pedido-service spring-boot:run
```

### Ejecutar conductor-service

```powershell
mvnw.cmd -pl conductor-service spring-boot:run
```

Los servicios pueden ejecutarse simultáneamente porque utilizan puertos diferentes.

---

## 9. Bases de datos actuales

| Microservicio | Puerto | Base de datos |
|---|---:|---|
| auth-service | 8081 | sitrac_auth |
| cliente-service | 8082 | sitrac_clientes |
| pedido-service | 8083 | sitrac_pedidos |
| conductor-service | 8084 | sitrac_conductores |

Las bases pueden crearse automáticamente mediante la configuración de Spring/JPA.

---

## 10. Convención interna de arquitectura

Ejemplo de organización de un microservicio:

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

### domain

Contiene el modelo y los contratos centrales del negocio.

### port/in

Define los casos de uso que la aplicación expone.

### application/service

Implementa los casos de uso y las reglas de negocio.

### port/out

Define qué necesita el dominio de sistemas externos, como persistencia.

### infrastructure/adapter/in

Expone los endpoints REST mediante Spring MVC.

### infrastructure/adapter/out

Implementa el acceso a MySQL mediante Spring Data JPA.

---

## 11. Trabajo con Git

Antes de programar:

```powershell
git pull
```

Después de un avance:

```powershell
git status
git add .
git commit -m "Describe el cambio realizado"
git push
```

Se utilizan ramas `feature/*` para desarrollar módulos sin afectar inmediatamente la versión principal.

---

## 12. Estado del desarrollo

| Módulo | Estado |
|---|---|
| Arquitectura base | Implementada |
| auth-service | Implementado y probado |
| cliente-service | Implementado y probado |
| pedido-service | Implementado y probado |
| conductor-service | Implementado |
| flota-service | Pendiente |
| programacion-service | Pendiente |
| viaje-service | Pendiente |
| mantenimiento-service | Pendiente |
| somma-service | Pendiente |

---

## 13. Objetivo académico

El proyecto permite aplicar de manera integrada los contenidos del curso:

- Servicios web REST.
- Métodos HTTP GET, POST, PUT y DELETE.
- Arquitectura por capas y separación de responsabilidades.
- Spring Boot.
- Persistencia con Spring Data JPA.
- Seguridad y cifrado de contraseñas.
- MySQL.
- Pruebas de APIs mediante Postman.
- Diseño de una solución distribuida orientada a servicios.

---

## 14. Documentación

Los diagramas del proyecto se encuentran en:

```text
docs/
docs/arquitectura/
```

Actualmente se incluyen diagramas de:

- Arquitectura de microservicios.
- Arquitectura hexagonal.
- Organización MVC / capas.

---

## 15. Próximo avance

El siguiente módulo de desarrollo será **flota-service**, para posteriormente implementar **programación**, donde se concentrarán varias de las reglas principales del negocio de transporte.

---

**SITRAC**  
Proyecto — Desarrollo de Servicios Web II  
YANKARDEV
