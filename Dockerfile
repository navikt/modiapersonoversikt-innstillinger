FROM gcr.io/distroless/java21-debian13

COPY build/install/modiapersonoversikt-innstillinger/lib /lib

ENTRYPOINT ["java", "-cp", "/lib/*", "no.nav.modiapersonoversikt.MainKt"]
