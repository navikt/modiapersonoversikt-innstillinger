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
import io.ktor.server.plugins.forwardedheaders.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.request.*
import io.ktor.server.routing.*
import io.micrometer.prometheus.PrometheusConfig
import io.micrometer.prometheus.PrometheusMeterRegistry
import no.nav.modiapersonoversikt.ObjectMapperProvider.Companion.objectMapper
import no.nav.modiapersonoversikt.infrastructure.Security
import no.nav.modiapersonoversikt.infrastructure.SubjectPrincipal
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
    install(XForwardedHeaders)
    install(StatusPages) {
        notFoundHandler()
        exceptionHandler()
    }

    install(CORS) {
        anyHost()
        allowMethod(HttpMethod.Post)
        allowMethod(HttpMethod.Delete)
    }

    val security = Security(
        listOfNotNull(configuration.openam, configuration.azuread)
    )
    install(Authentication) {
        if (useMock) {
            security.setupMock(SubjectPrincipal("Z999999"))
        } else {
            security.setupJWT()
        }
    }

    install(ContentNegotiation) {
        register(ContentType.Application.Json, JacksonConverter(objectMapper))
    }

    install(CallLogging) {
        level = Level.INFO
        disableDefaultColors()
        filter { call -> call.request.path().startsWith("/modiapersonoversikt-innstillinger/api") }
        mdc("userId") { security.getSubject(it).joinToString(";") }
    }

    install(MicrometerMetrics) {
        registry = metricsRegistry
    }

    val storageProvider = JdbcStorageProvider(dataSource)

    routing {
        route("modiapersonoversikt-innstillinger") {
            naisRoutes(readinessCheck = { applicationState.initialized }, livenessCheck = { applicationState.running })
            route("/api") {
                settingsRoutes(configuration, storageProvider)
            }
        }
    }

    applicationState.initialized = true
}
