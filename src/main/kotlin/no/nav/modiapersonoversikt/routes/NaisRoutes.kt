package no.nav.modiapersonoversikt.routes

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import no.nav.modiapersonoversikt.metricsRegistry

fun Route.naisRoutes(
    readinessCheck: () -> Boolean,
    livenessCheck: () -> Boolean = { true }
) {

    get("/isAlive") {
        if (livenessCheck()) {
            call.respondText("Alive")
        } else {
            call.respondText("Not alive", status = HttpStatusCode.InternalServerError)
        }
    }

    get("/isReady") {
        if (readinessCheck()) {
            call.respondText("Ready")
        } else {
            call.respondText("Not ready", status = HttpStatusCode.InternalServerError)
        }
    }

    get("/metrics") {
        call.respondText(metricsRegistry.scrape())
    }
}
