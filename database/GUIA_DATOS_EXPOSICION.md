# SITRAC — Guía de datos y demostración

Esta guía describe el flujo recomendado para demostrar SITRAC con el alcance final del proyecto.

## Inicio del entorno

Desde la raíz del repositorio:

```bash
docker compose down -v
docker compose up -d --build
```

Verificar:

```bash
docker compose ps
```

Accesos principales:

- Frontend: http://localhost:4200
- API Gateway: http://localhost:8080
- Eureka: http://localhost:8761
- RabbitMQ Management: http://localhost:15672

RabbitMQ:

- Usuario: `sitrac`
- Contraseña: `sitracmq`

## Usuarios de demostración

| Rol | Usuario | Contraseña |
|---|---|---|
| ADMIN | `admin.sitrac` | `Admin123*` |
| OPERADOR | `operador.sitrac` | `Operador123*` |
| COMBUSTIBLE | `combustible.sitrac` | `Combustible123*` |
| SUPERVISOR | `supervisor.sitrac` | `Supervisor123*` |

## Flujo recomendado para la exposición

### 1. Operaciones

Ingresar como `operador.sitrac`.

1. Mostrar clientes y pedidos.
2. Seleccionar o crear un pedido en estado disponible.
3. Ir a Programación.
4. Seleccionar conductor, tracto y semirremolque.
5. Confirmar la programación.

Durante este paso explicar que `programacion-service` consulta de forma síncrona a pedido, conductor y flota mediante OpenFeign antes de guardar la asignación.

### 2. Evento de programación

Después de crear la programación:

```text
programacion-service
        ↓ PROGRAMACION_CREADA
     RabbitMQ
        ↓
combustible-service
```

`combustible-service` crea automáticamente una solicitud en estado `PENDIENTE`.

### 3. Área de Combustible

Cerrar sesión e ingresar como:

```text
combustible.sitrac / Combustible123*
```

1. Abrir Combustible.
2. Localizar la solicitud pendiente.
3. Pulsar **Atender**.
4. Seleccionar el punto de abastecimiento.
5. Ingresar la cantidad autorizada de galones.
6. Confirmar.

La aplicación publica `ABASTECIMIENTO_CONFIRMADO` y `notification-service` genera la notificación al conductor.

Si `SITRAC_MAIL_ENABLED=false`, el correo se observa en los logs del servicio de notificaciones.

### 4. Viaje

Volver a ingresar como Operador.

1. Abrir Viajes.
2. Crear o localizar el viaje asociado a la programación.
3. Iniciar el viaje registrando fecha y kilometraje inicial.
4. Mostrar el estado `EN_VIAJE`.
5. Finalizar registrando fecha y kilometraje final.

Al finalizar:

- pedido y programación actualizan su estado;
- conductor, tracto y semirremolque son liberados;
- `viaje-service` publica `VIAJE_FINALIZADO`;
- `notification-service` genera una constancia PDF;
- el cliente recibe el correo de cierre con la constancia adjunta, o se registra la evidencia en logs si el correo real está desactivado.

## Evidencias técnicas recomendadas

Durante la exposición mostrar brevemente:

1. **Eureka Server:** servicios activos registrados.
2. **RabbitMQ:** exchange `sitrac.events` y colas `sitrac.combustible` / `sitrac.notificaciones`.
3. **Docker:** contenedores levantados con `docker compose ps`.
4. **Código:** `programacion-service` para OpenFeign y publicación del evento.
5. **Código:** `combustible-service` para consumo de `PROGRAMACION_CREADA`.
6. **Código:** `notification-service` para correo al conductor y constancia al cliente.

## Datos disponibles

El dataset histórico conserva clientes, pedidos, conductores, flota, programaciones, viajes y registros de combustible útiles para la demostración. El archivo `sitrac_alcance_final.sql` se ejecuta después del dataset base para retirar roles y bases que ya no pertenecen al alcance y para agregar correo a los conductores.

Los pedidos que permanezcan en estado `REGISTRADO` pueden utilizarse para crear una programación durante la sustentación.

## Reglas principales para explicar

- CAL_GRANEL → BOMBONA.
- CEMENTO_BOLSA → PLATAFORMA.
- MAQUINARIA / CARGA_ANCHA / ESPECIAL → CAMA_BAJA.
- Una programación reserva conductor, tracto y semirremolque.
- La programación genera una solicitud de abastecimiento pendiente.
- Al finalizar el viaje se finalizan pedido y programación y se liberan los recursos.
- El conductor recibe la información del abastecimiento sin ingresar a SITRAC.
- El cliente recibe la constancia al finalizar el servicio sin ingresar a SITRAC.

## Reinicio de datos

Para volver al dataset inicial:

```bash
docker compose down -v
docker compose up -d --build
```

## Nota sobre privacidad

Los datos personales usados para conductores en el dataset son ficticios y tienen fines de demostración. Para una presentación con correo real, se debe modificar únicamente el email del conductor y del cliente que participen en la prueba.
