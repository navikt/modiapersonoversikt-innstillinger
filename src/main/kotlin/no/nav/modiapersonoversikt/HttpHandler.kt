package no.nav.modiapersonoversikt

import io.ktor.application.install
import io.ktor.features.ContentNegotiation
import io.ktor.features.StatusPages
import io.ktor.gson.gson
import io.ktor.routing.routing
import io.ktor.server.engine.ApplicationEngine
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import no.nav.modiapersonoversikt.routes.naisRoutes
import no.nav.modiapersonoversikt.routes.settingsRoutes

fun createHttpServer(applicationState: ApplicationState, port: Int = 7070): ApplicationEngine = embeddedServer(Netty, port) {
    install(StatusPages) {
        notFoundHandler()
        exceptionHandler()
    }

    install(ContentNegotiation) {
        gson {
            setPrettyPrinting()
            disableHtmlEscaping()
        }
    }

    routing {
        naisRoutes(readinessCheck = { applicationState.initialized }, livenessCheck = { applicationState.running })
        settingsRoutes()
    }

    applicationState.initialized = true
}