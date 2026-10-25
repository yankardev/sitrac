# SITRAC — Sistema Integral de Transporte de Carga

SITRAC es una aplicación web para apoyar la gestión de servicios de transporte de carga pesada, desde el registro del pedido hasta la finalización del viaje. La solución reduce verificaciones repetitivas, controla la asignación de recursos, coordina el abastecimiento de combustible y automatiza comunicaciones al conductor y al cliente.

## Arquitectura

La solución utiliza:

- **Angular** para la interfaz web.
- **Java 21 + Spring Boot** para los microservicios.
- **API Gateway** como punto de entrada.
- **Eureka Server** para registro y descubrimiento.
- **OpenFeign** para consultas síncronas en la programación.
- **RabbitMQ** para eventos asíncronos.
- **Spring Data JPA + MySQL** para persistencia.
- **Spring Security + BCrypt** para autenticación y protección de contraseñas.
- **Docker Compose** para ejecutar el entorno integrado.

## Microservicios funcionales

| Microservicio | Puerto | Responsabilidad |
|---|---:|---|
| auth-service | 8081 | Usuarios, roles e inicio de sesión |
| cliente-service | 8082 | Clientes |
| pedido-service | 8083 | Solicitudes de transporte |
| conductor-service | 8084 | Conductores, licencias y disponibilidad |
| flota-service | 8085 | Tractos y semirremolques |
| programacion-service | 8086 | Validación y asignación de recursos |
| viaje-service | 8087 | Ejecución y cierre del viaje |
| combustible-service | 8090 | Solicitudes y registros de abastecimiento |
| notification-service | 8091 | Correos y constancia final |

Componentes de infraestructura:

| Componente | Puerto |
|---|---:|
| API Gateway | 8080 |
| Eureka Server | 8761 |
| RabbitMQ | 5672 |
| RabbitMQ Management | 15672 |
| MySQL | 3307 host / 3306 contenedor |
| Angular | 4200 |

## Flujo operativo principal

```text
Cliente / Pedido
      ↓
Operaciones programa el servicio
      ↓
programacion-service
      ├─ valida pedido
      ├─ valida conductor y licencia
      └─ valida tracto, semirremolque y capacidad
      ↓
PROGRAMACION_CREADA
      ↓ RabbitMQ
combustible-service
      ↓
Solicitud de abastecimiento PENDIENTE
      ↓
Personal de Combustible confirma punto y galones
      ↓
ABASTECIMIENTO_CONFIRMADO
      ↓ RabbitMQ
notification-service
      ↓
Correo al conductor
      ↓
Viaje PROGRAMADO → EN_VIAJE → FINALIZADO
      ↓ RabbitMQ
notification-service
      ↓
Correo + constancia PDF al cliente
```

## Programación y validaciones

`programacion-service` consulta mediante OpenFeign a:

- `pedido-service`: estado del pedido, carga y tonelaje;
- `conductor-service`: estado, disponibilidad y vigencia de licencia;
- `flota-service`: disponibilidad, capacidad y tipo de unidad.

Antes de confirmar una programación se valida que:

- el pedido pueda ser atendido;
- el conductor esté activo, disponible y con licencia vigente;
- tracto y semirremolque estén disponibles;
- la capacidad sea suficiente;
- el semirremolque sea compatible con la carga;
- conductor y unidades no estén en otra programación activa.

Compatibilidades principales:

| Tipo de carga | Semirremolque |
|---|---|
| Cal a granel | Bombona |
| Cemento en bolsa | Plataforma |
| Maquinaria | Cama baja |
| Carga ancha | Cama baja |
| Carga especial | Cama baja |

## Combustible y RabbitMQ

Al crear una programación, `programacion-service` publica el evento:

```text
programacion.creada / PROGRAMACION_CREADA
```

`combustible-service` lo consume desde la cola `sitrac.combustible` y crea una solicitud en estado `PENDIENTE` reutilizando la programación, conductor y tracto.

Endpoints principales:

```http
GET /api/combustible/solicitudes
PUT /api/combustible/solicitudes/{id}/confirmar
```

Al confirmar la solicitud se publica:

```text
combustible.abastecimiento.confirmado / ABASTECIMIENTO_CONFIRMADO
```

`notification-service` consume el evento y envía al conductor el punto de abastecimiento, cantidad autorizada y datos relacionados con la operación.

## Cierre del viaje y constancia

Los estados principales del viaje son:

```text
PROGRAMADO → EN_VIAJE → FINALIZADO
       └──────────────→ CANCELADO
```

Al finalizar:

1. `viaje-service` actualiza la operación mediante `programacion-service`.
2. Pedido y programación quedan finalizados.
3. Conductor, tracto y semirremolque vuelven a estar disponibles.
4. Se publica `VIAJE_FINALIZADO` en RabbitMQ.
5. `notification-service` obtiene la información del cliente.
6. Se genera una constancia PDF del servicio.
7. El cliente recibe el correo de finalización con el PDF adjunto.

## Roles

| Rol | Acceso principal |
|---|---|
| ADMIN | Gestión general y usuarios |
| OPERADOR | Clientes, pedidos, conductores, flota, programación y viajes |
| COMBUSTIBLE | Solicitudes y abastecimientos |
| SUPERVISOR | Consulta y supervisión operativa |

Conductores y clientes no necesitan ingresar a SITRAC; reciben las comunicaciones generadas por la operación.

## Credenciales de demostración

Al iniciar el sistema, `auth-service` crea estos usuarios si no existen:

| Rol | Usuario | Contraseña |
|---|---|---|
| ADMIN | `admin.sitrac` | `Admin123*` |
| OPERADOR | `operador.sitrac` | `Operador123*` |
| COMBUSTIBLE | `combustible.sitrac` | `Combustible123*` |
| SUPERVISOR | `supervisor.sitrac` | `Supervisor123*` |

## Ejecución con Docker

Requisito: Docker Desktop iniciado.

```bash
docker compose up -d --build
```

En Windows también se puede ejecutar:

```text
run-all.bat
```

Verificar contenedores:

```bash
docker compose ps
```

Accesos:

- SITRAC: `http://localhost:4200`
- API Gateway: `http://localhost:8080`
- Eureka: `http://localhost:8761`
- RabbitMQ: `http://localhost:15672`

Credenciales RabbitMQ por defecto:

```text
usuario: sitrac
password: sitracmq
```

Detener el entorno:

```bash
docker compose down
```

Si se desea reconstruir también la base demo desde cero:

```bash
docker compose down -v
docker compose up -d --build
```

## Correo real o modo demostración

Por defecto `notification-service` trabaja con:

```text
SITRAC_MAIL_ENABLED=false
```

En ese modo las notificaciones se registran en los logs sin requerir credenciales SMTP.

Para envío real se pueden definir:

```text
SITRAC_MAIL_ENABLED=true
MAIL_USERNAME=correo@dominio.com
MAIL_PASSWORD=clave-de-aplicacion
MAIL_FROM=correo@dominio.com
```

No se deben almacenar contraseñas reales en el repositorio.

## Validación automática

GitHub Actions ejecuta en cada cambio de la rama final:

- compilación Maven con Java 21;
- validación de `docker compose config`;
- `npm ci`;
- compilación de Angular.

El flujo se encuentra en `.github/workflows/ci-spring-cloud.yml`.

## Estructura relevante

```text
sitrac/
├── api-gateway/
├── eureka-server/
├── auth-service/
├── cliente-service/
├── pedido-service/
├── conductor-service/
├── flota-service/
├── programacion-service/
├── viaje-service/
├── combustible-service/
├── notification-service/
├── sitrac-frontend/
├── database/
├── docs/
├── docker-compose.yml
└── Dockerfile
```

**SITRAC — Sistema Integral de Transporte de Carga**
