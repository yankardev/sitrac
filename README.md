# SITRAC — Sistema Integral de Transporte de Carga

SITRAC es una aplicación web para apoyar la gestión de servicios de transporte de carga de **Transportes Hermanos Carranza S.A.C.** El flujo principal cubre pedido, programación, asignación de recursos, coordinación del combustible, seguimiento del viaje y cierre del servicio.

## Arquitectura

- **Frontend:** Angular.
- **Backend:** Java 21 + Spring Boot.
- **Entrada única:** Spring Cloud Gateway.
- **Descubrimiento:** Eureka Server.
- **Comunicación síncrona:** REST / OpenFeign.
- **Comunicación asíncrona:** RabbitMQ.
- **Persistencia:** MySQL + Spring Data JPA.
- **Seguridad:** Spring Security + BCrypt.
- **Ejecución integrada:** Docker Compose.

## Microservicios funcionales

| Servicio | Puerto | Responsabilidad |
|---|---:|---|
| auth-service | 8081 | Usuarios, roles e inicio de sesión |
| cliente-service | 8082 | Clientes |
| pedido-service | 8083 | Solicitudes de transporte |
| conductor-service | 8084 | Conductores, disponibilidad y licencia |
| flota-service | 8085 | Tractos y semirremolques |
| programacion-service | 8086 | Validación y asignación de recursos |
| viaje-service | 8087 | Inicio, ejecución y finalización del viaje |
| combustible-service | 8090 | Solicitudes y confirmación de abastecimiento |
| notification-service | 8091 | Correos y constancia PDF |

## Flujo principal

```text
Cliente / Pedido
      ↓
Operaciones programa el servicio
      ↓
programacion-service
      ├─ OpenFeign → pedido-service
      ├─ OpenFeign → conductor-service
      └─ OpenFeign → flota-service
      ↓
PROGRAMACION_CREADA
      ↓ RabbitMQ
combustible-service
      ↓
Solicitud PENDIENTE
      ↓
Personal de Combustible confirma punto + galones
      ↓
ABASTECIMIENTO_CONFIRMADO
      ↓ RabbitMQ
notification-service → correo al conductor
      ↓
Viaje PROGRAMADO → EN_VIAJE → FINALIZADO
      ↓
VIAJE_FINALIZADO
      ↓ RabbitMQ
notification-service → constancia PDF + correo al cliente
```

Al finalizar un viaje, `programacion-service` actualiza el pedido, finaliza la programación y libera conductor, tracto y semirremolque.

## Roles internos

- `ADMIN`
- `OPERADOR`
- `COMBUSTIBLE`
- `SUPERVISOR`

Conductores y clientes no ingresan a SITRAC; reciben comunicaciones generadas por el sistema.

## Ejecución con Docker

Para reconstruir una demostración limpia (elimina el volumen de datos anterior):

```powershell
docker compose down -v
docker compose up -d --build
```

Accesos:

- Frontend: `http://localhost:4200`
- API Gateway: `http://localhost:8080`
- Eureka: `http://localhost:8761`
- RabbitMQ Management: `http://localhost:15672`
- MySQL desde Windows: `localhost:3307`

Credenciales RabbitMQ de desarrollo:

- usuario: `sitrac`
- contraseña: `sitracmq`

### Usuarios de demostración

Todos usan la contraseña: `Sitrac2026*`

| Rol | Usuario |
|---|---|
| Administrador | `admin.sitrac` |
| Operador | `operador.sitrac` |
| Combustible | `combustible.sitrac` |
| Supervisor | `supervisor.sitrac` |

## Correo real (opcional para la demostración)

Por defecto `notification-service` trabaja en modo demostración y registra los correos en el log. Para enviar correos reales configure las variables de entorno:

```text
SITRAC_MAIL_ENABLED=true
MAIL_USERNAME=correo@dominio.com
MAIL_PASSWORD=clave-o-app-password
MAIL_FROM=correo@dominio.com
```

No se deben guardar contraseñas reales en GitHub.

## Escenario de sustentación recomendado

1. Iniciar sesión como Operador.
2. Registrar/seleccionar cliente y pedido.
3. Programar el viaje y demostrar las validaciones de conductor y flota.
4. Mostrar Eureka y la cola/evento en RabbitMQ.
5. Iniciar sesión como Combustible y atender la solicitud generada automáticamente.
6. Verificar la notificación al conductor (correo real o log de demostración).
7. Iniciar y finalizar el viaje.
8. Verificar la constancia PDF y la notificación final al cliente.

## Construcción

Backend:

```powershell
mvnw.cmd -B -DskipTests package
```

Frontend:

```powershell
cd sitrac-frontend
npm ci
npm run build
```

---
SITRAC — Sistema Integral de Transporte de Carga
