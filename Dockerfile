# build environment
FROM maven:3.9.4-eclipse-temurin-17-alpine AS build
WORKDIR /app

ARG SPRING_CONFIG_NAME=application-testing

COPY . /app
RUN mvn -U clean install

# production environment
FROM eclipse-temurin:17-jre-alpine
ARG SPRING_CONFIG_NAME=application-development
WORKDIR /app
COPY --from=build /app/target/quality-automacao-challenge-0.0.1-SNAPSHOT.jar /app/quality-automacao-challenge.jar
COPY --from=build /app/private.pem /app/private.pem
COPY --from=build /app/public.crt /app/public.crt
RUN ls -la /app
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/quality-automacao-challenge.jar"]