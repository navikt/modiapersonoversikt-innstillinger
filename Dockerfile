FROM maven:3.9.6-eclipse-temurin-17-alpine as builder

ADD / /source
WORKDIR /source
RUN ./gradlew build

FROM ghcr.io/navikt/baseimages/temurin:17-appdynamics

COPY --from=builder /source/build/libs/app.jar app.jar
