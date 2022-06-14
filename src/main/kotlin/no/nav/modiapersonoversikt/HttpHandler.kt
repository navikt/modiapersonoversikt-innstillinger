package no.nav.modiapersonoversikt

import io.ktor.http.ContentType
import io.ktor.http.HttpMethod
import io.ktor.serialization.jackson.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.engine.ApplicationEngine
import io.ktor.server.engine.embeddedServer
import io.ktor.server.metrics.micrometer.*
import io.ktor.server.netty.Netty
import io.ktor.server.plugins.callloging.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.request.*
import io.ktor.server.routing.*
import io.micrometer.prometheus.PrometheusConfig
import io.micrometer.prometheus.PrometheusMeterRegistry
import no.nav.modiapersonoversikt.ObjectMapperProvider.Companion.objectMapper
import no.nav.modiapersonoversikt.infrastructure.Security
import no.nav.modiapersonoversikt.infrastructure.SubjectPrincipal
import no.nav.modiapersonoversikt.infrastructure.setupJWT
import no.nav.modiapersonoversikt.infrastructure.setupMock
import no.nav.modiapersonoversikt.routes.naisRoutes
import no.nav.modiapersonoversikt.routes.settingsRoutes
import no.nav.modiapersonoversikt.storage.JdbcStorageProvider
import org.slf4j.event.Level
import javax.sql.DataSource

val metricsRegistry = PrometheusMeterRegistry(PrometheusConfig.DEFAULT)

fun createHttpServer(
    applicationState: ApplicationState,
    configuration: Configuration,
    dataSource: DataSource,
    port: Int = 7070,
    useMock: Boolean
): ApplicationEngine = embeddedServer(Netty, port) {
    install(StatusPages) {
        notFoundHandler()
        exceptionHandler()
    }

    install(CORS) {
        anyHost()
        allowMethod(HttpMethod.Post)
        allowMethod(HttpMethod.Delete)
    }

    install(Authentication) {
        if (useMock) {
            setupMock(principal = SubjectPrincipal("Z999999"))
        } else {
            setupJWT(configuration.jwksUrl, configuration.jwtIssuer)
        }
    }

    install(ContentNegotiation) {
        register(ContentType.Application.Json, JacksonConverter(objectMapper))
    }

    install(CallLogging) {
        level = Level.INFO
        filter { call -> call.request.path().startsWith("/modiapersonoversikt-innstillinger/api") }
        mdc("userId", Security::getSubject)
    }

    install(MicrometerMetrics) {
        registry = metricsRegistry
    }

    val storageProvider = JdbcStorageProvider(dataSource)

    routing {
        route("modiapersonoversikt-innstillinger") {
            naisRoutes(readinessCheck = { applicationState.initialized }, livenessCheck = { applicationState.running })
            route("/api") {
                settingsRoutes(storageProvider)
            }
        }
    }

    applicationState.initialized = true
}
