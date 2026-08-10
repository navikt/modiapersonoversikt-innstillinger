FROM gradle:8-jdk21 AS builder

ADD / /source
WORKDIR /source
RUN ./gradlew installDist

FROM gcr.io/distroless/java21-debian12

COPY --from=builder /source/build/install/modiapersonoversikt-innstillinger/lib /app/lib

CMD ["java", "-cp", "/app/lib/*", "no.nav.modiapersonoversikt.MainKt"]
