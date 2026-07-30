# syntax=docker/dockerfile:1

# ============================================================================
# Etapa 1 — build: compila y empaqueta el .jar con Maven + JDK 21.
# ============================================================================
FROM maven:3.9-eclipse-temurin-21 AS build

WORKDIR /app

# Copiamos primero SOLO el pom para cachear la descarga de dependencias:
# mientras el pom no cambie, Docker reutiliza esta capa y no vuelve a bajar deps.
COPY pom.xml ./
RUN mvn -B dependency:go-offline

# Ahora el código fuente y empaquetado.
# -DskipTests por decisión del proyecto (ver CLAUDE.md del backend): los tests
# están deliberadamente aplazados; la verificación es compilar + arrancar.
COPY src ./src
RUN mvn -B clean package -DskipTests

# ============================================================================
# Etapa 2 — runtime: solo JRE 21 + el .jar. Imagen final ligera, sin Maven
# ni JDK ni código fuente.
# ============================================================================
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# PDFBox (módulo de IA que extrae texto de pliegos) usa AWT; estas libs del
# sistema evitan fallos de fuentes/freetype en imágenes Alpine sin display.
RUN apk add --no-cache freetype fontconfig

# Usuario no-root por seguridad: el proceso Java no corre como root.
RUN addgroup -S app && adduser -S app -G app

# Copiamos el jar empaquetado (nombre = artifactId-version del pom.xml).
COPY --from=build /app/target/licitapp-0.0.1-SNAPSHOT.jar app.jar

USER app

# El backend escucha en 8080, solo hacia la red interna de Docker (no se publica).
EXPOSE 8080

# Healthcheck contra el actuator (bajo el context-path /api/v1). Permite que
# docker-compose espere al backend sano antes de darlo por arriba
# (depends_on: condition: service_healthy). start-period cubre el arranque de
# Spring + la migración Flyway inicial.
HEALTHCHECK --interval=15s --timeout=5s --start-period=90s --retries=5 \
  CMD wget -qO- http://localhost:8080/api/v1/actuator/health || exit 1

# -Djava.awt.headless=true: PDFBox/AWT en un servidor sin entorno gráfico.
ENTRYPOINT ["java", "-Djava.awt.headless=true", "-jar", "app.jar"]
