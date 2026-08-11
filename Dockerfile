FROM gcr.io/distroless/java21-debian12

COPY build/install/modiapersonoversikt-innstillinger/lib /lib

ENTRYPOINT ["java", "-cp", "/lib/*", "no.nav.modiapersonoversikt.MainKt"]
