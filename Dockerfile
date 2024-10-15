FROM gradle:8-jdk21 as builder

ADD / /source
WORKDIR /source
RUN ./gradlew build

FROM gcr.io/distroless/java21-debian12

COPY --from=builder /source/build/libs/app.jar app.jar

CMD ["app.jar"]
