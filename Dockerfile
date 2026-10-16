# Imagen única para los 10 microservicios de SITRAC.
# La etapa "build" compila todo el proyecto Maven una sola vez (Docker la reutiliza
# desde caché para cada servicio) y la etapa final copia solo el JAR del servicio
# indicado en el argumento SERVICE.

FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /workspace
COPY pom.xml .
COPY auth-service/pom.xml auth-service/
COPY cliente-service/pom.xml cliente-service/
COPY pedido-service/pom.xml pedido-service/
COPY conductor-service/pom.xml conductor-service/
COPY flota-service/pom.xml flota-service/
COPY programacion-service/pom.xml programacion-service/
COPY viaje-service/pom.xml viaje-service/
COPY mantenimiento-service/pom.xml mantenimiento-service/
COPY somma-service/pom.xml somma-service/
COPY combustible-service/pom.xml combustible-service/
RUN mvn -B -q dependency:go-offline -DskipTests || true
COPY . .
RUN mvn -B -q -DskipTests package

FROM eclipse-temurin:21-jre
ARG SERVICE
WORKDIR /app
COPY --from=build /workspace/${SERVICE}/target/${SERVICE}-*.jar app.jar
ENV TZ=America/Lima
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
