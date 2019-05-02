package no.nav.modiapersonoversikt.routes

import io.ktor.application.ApplicationCall
import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.*
import no.nav.modiapersonoversikt.API_COUNTER
import no.nav.modiapersonoversikt.storage.StorageProvider

fun Routing.settingsRoutes(provider: StorageProvider) {
    route("/innstillinger/{navident}") {

        get {
            API_COUNTER.labels("GET").inc()
            val ident = call.getNavident()
            provider.getData(ident)?.let { call.respond(it) } ?: call.respond(HttpStatusCode.NotFound)
        }

        post {
            API_COUNTER.labels("POST").inc()
            val ident = call.getNavident()
            provider.storeData(ident, call.receive())
            call.respond(HttpStatusCode.OK)
        }

        delete {
            API_COUNTER.labels("DELETE").inc()
            val ident = call.getNavident()
            provider.clearData(ident)
            call.respond(HttpStatusCode.OK)
        }
    }

}

private suspend inline fun ApplicationCall.getNavident(): String {
    val ident = this.parameters["navident"] ?: this.respond(HttpStatusCode.BadRequest)
    return ident as String
}
