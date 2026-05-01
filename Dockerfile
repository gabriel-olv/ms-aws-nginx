FROM maven:4.0.0-rc-5-eclipse-temurin-25-alpine AS builder

WORKDIR /app

COPY src ./src
COPY pom.xml .

RUN mvn -DskipTests package

FROM eclipse-temurin:25-alpine

WORKDIR /usr/src/app

COPY --from=builder /app/target/*.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]
