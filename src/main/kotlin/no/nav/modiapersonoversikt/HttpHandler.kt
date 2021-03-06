package no.nav.modiapersonoversikt

import io.ktor.application.install
import io.ktor.auth.Authentication
import io.ktor.auth.jwt.jwt
import io.ktor.features.CORS
import io.ktor.features.CallLogging
import io.ktor.features.ContentNegotiation
import io.ktor.features.StatusPages
import io.ktor.http.ContentType
import io.ktor.http.HttpMethod
import io.ktor.jackson.JacksonConverter
import io.ktor.metrics.dropwizard.DropwizardMetrics
import io.ktor.request.path
import io.ktor.routing.route
import io.ktor.routing.routing
import io.ktor.server.engine.ApplicationEngine
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.prometheus.client.dropwizard.DropwizardExports
import no.nav.modiapersonoversikt.ObjectMapperProvider.Companion.objectMapper
import no.nav.modiapersonoversikt.routes.naisRoutes
import no.nav.modiapersonoversikt.routes.settingsRoutes
import no.nav.modiapersonoversikt.storage.JdbcStorageProvider
import org.slf4j.event.Level
import javax.sql.DataSource
import no.nav.modiapersonoversikt.JwtUtil.Companion as JwtUtil

fun createHttpServer(
    applicationState: ApplicationState,
    configuration: Configuration,
    dataSource: DataSource,
    port: Int = 7070,
    useAuthentication: Boolean
): ApplicationEngine = embeddedServer(Netty, port) {

    install(StatusPages) {
        notFoundHandler()
        exceptionHandler()
    }

    install(CORS) {
        anyHost()
        method(HttpMethod.Post)
        method(HttpMethod.Delete)
    }

    if (useAuthentication) {
        install(Authentication) {
            jwt {
                authHeader(JwtUtil::useJwtFromCookie)
                realm = "modiapersonoversikt-skrivestøtte"
                verifier(configuration.jwksUrl, configuration.jwtIssuer)
                validate { JwtUtil.validateJWT(it) }
            }
        }
    }

    install(ContentNegotiation) {
        register(ContentType.Application.Json, JacksonConverter(objectMapper))
    }

    install(CallLogging) {
        level = Level.INFO
        filter { call -> call.request.path().startsWith("/modiapersonoversikt-innstillinger/api") }
        mdc("userId", JwtUtil::getSubject)
    }

    install(DropwizardMetrics) {
        io.prometheus.client.CollectorRegistry.defaultRegistry.register(DropwizardExports(registry))
    }

    val storageProvider = JdbcStorageProvider(dataSource)

    routing {
        route("modiapersonoversikt-innstillinger") {
            naisRoutes(readinessCheck = { applicationState.initialized }, livenessCheck = { applicationState.running })
            route("/api") {
                settingsRoutes(storageProvider, useAuthentication)
            }
        }
    }

    applicationState.initialized = true
}
