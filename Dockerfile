FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /build

# Resolve dependencies in their own layer so code changes don't re-download them.
COPY pom.xml .
RUN mvn -B -q dependency:go-offline

COPY src ./src
RUN mvn -B -q clean package -DskipTests

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

RUN addgroup -S spring && adduser -S spring -G spring
USER spring

COPY --from=build /build/target/*.jar app.jar

# Render injects PORT; application.properties defaults it to 8080 locally.
EXPOSE 8080

# Render's free instances cap at 512MB, so size the heap off the container limit
# rather than letting the JVM assume it owns the host.
ENTRYPOINT ["java", "-XX:MaxRAMPercentage=75.0", "-jar", "/app/app.jar"]
