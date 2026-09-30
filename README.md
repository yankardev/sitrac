# SITRAC

Sistema Integral de Transporte de Carga desarrollado para el curso **Desarrollo de Servicios Web II**.

## Arquitectura

- Arquitectura general: microservicios.
- Arquitectura interna: hexagonal.
- Exposición REST: Spring MVC.
- Persistencia: Spring Data JPA.
- Base de datos: MySQL.
- Seguridad: Spring Security + BCrypt en auth-service.
- IDE: IntelliJ IDEA.
- Pruebas de endpoints: Postman.

## Microservicios implementados

### auth-service — puerto 8081

Responsable de autenticación, usuarios y roles.

- `POST /api/auth/registro`
- `POST /api/auth/login`

Base de datos: `sitrac_auth`.

### cliente-service — puerto 8082

Primer CRUD REST completo del proyecto.

- `POST /api/clientes`
- `GET /api/clientes`
- `GET /api/clientes/{id}`
- `PUT /api/clientes/{id}`
- `DELETE /api/clientes/{id}`

Base de datos: `sitrac_clientes`.

## Ejecutar desde la raíz

Auth:

```powershell
mvnw.cmd -pl auth-service spring-boot:run
```

Clientes:

```powershell
mvnw.cmd -pl cliente-service spring-boot:run
```

Ambos pueden ejecutarse al mismo tiempo porque usan puertos diferentes.

## Variables de entorno

- `MYSQL_USER`
- `MYSQL_PASSWORD`
- `MYSQL_URL` para auth-service
- `MYSQL_CLIENTE_URL` para cliente-service
