# ===== STAGE 1: BUILD =====
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn -q -B -DskipTests dependency:go-offline
COPY src ./src
RUN mvn -q -B -DskipTests package

# ===== STAGE 2: RUNTIME =====
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENV JAVA_OPTS=""
RUN useradd -ms /bin/bash appuser
USER appuser
ENTRYPOINT ["sh","-c","java $JAVA_OPTS -jar /app/app.jar"]
