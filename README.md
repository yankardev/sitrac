# SITRAC

Sistema Integral de Transporte de Carga desarrollado para el curso **Desarrollo de Servicios Web II**.

## Arquitectura

- Arquitectura general: microservicios.
- Arquitectura interna: hexagonal.
- Exposición REST: Spring MVC.
- Persistencia: Spring Data JPA.
- Base de datos: MySQL.
- Seguridad: Spring Security + BCrypt.
- IDE: IntelliJ IDEA.
- Pruebas de endpoints: Postman.

## Microservicios

El desarrollo comienza por:

- `auth-service`: autenticación, usuarios y roles.

Los demás servicios se incorporarán progresivamente conforme avance el proyecto.

## Ejecutar auth-service

1. Tener MySQL iniciado.
2. Configurar las variables de entorno si son necesarias:
   - `MYSQL_URL`
   - `MYSQL_USER`
   - `MYSQL_PASSWORD`
3. Desde la raíz:

```bash
./mvnw -pl auth-service spring-boot:run
```

En Windows:

```powershell
mvnw.cmd -pl auth-service spring-boot:run
```

El servicio se ejecuta en:

```text
http://localhost:8081
```

### Registrar usuario

```http
POST /api/auth/registro
```

### Login

```http
POST /api/auth/login
```
