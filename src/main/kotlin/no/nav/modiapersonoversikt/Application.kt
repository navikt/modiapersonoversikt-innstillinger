package no.nav.modiapersonoversikt

import io.ktor.http.ContentType
import io.ktor.serialization.jackson.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.plugins.calllogging.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.forwardedheaders.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.request.*
import io.ktor.server.routing.*
import no.nav.modiapersonoversikt.ObjectMapperProvider.Companion.objectMapper
import no.nav.modiapersonoversikt.routes.settingsRoutes
import no.nav.modiapersonoversikt.storage.JdbcStorageProvider
import no.nav.personoversikt.common.ktor.utils.Metrics
import no.nav.personoversikt.common.ktor.utils.Security
import no.nav.personoversikt.common.ktor.utils.Selftest
import org.slf4j.event.Level
import javax.sql.DataSource

fun Application.innstillingerApp(
    configuration: Configuration,
    dataSource: DataSource,
    useMock: Boolean,
) {
    val security =
        Security(
            listOfNotNull(
                configuration.azuread,
            ),
        )

    install(XForwardedHeaders)
    install(StatusPages) {
        notFoundHandler()
        exceptionHandler()
    }

    install(Metrics.Plugin)

    install(Selftest.Plugin) {
        appname = appName
        version = appImage
    }

    install(Authentication) {
        if (useMock) {
            security.setupMock(this, "Z999999")
        } else {
            security.setupJWT(this)
        }
    }

    install(ContentNegotiation) {
        register(ContentType.Application.Json, JacksonConverter(objectMapper))
    }

    install(CallLogging) {
        level = Level.INFO
        disableDefaultColors()
        filter { call -> call.request.path().startsWith("/api") }
        mdc("userId") { security.getSubject(it).joinToString(";") }
    }

    val storageProvider = JdbcStorageProvider(dataSource)

    routing {
        route("/api") {
            settingsRoutes(security.authproviders, storageProvider)
        }
    }
}
