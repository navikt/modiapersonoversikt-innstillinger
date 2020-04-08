package no.nav.modiapersonoversikt.routes

import io.ktor.application.ApplicationCall
import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.*
import no.nav.modiapersonoversikt.storage.StorageProvider

fun Route.settingsRoutes(provider: StorageProvider) {
    route("/innstillinger/{navident}") {

        get {
            val ident = call.getNavident()
            call.respond(provider.getData(ident))
        }

        post {
            val ident = call.getNavident()
            call.respond(provider.storeData(ident, call.receive()))
        }

        delete {
            val ident = call.getNavident()
            call.respond(provider.clearData(ident))
        }
    }

}

private suspend inline fun ApplicationCall.getNavident(): String {
    val ident = this.parameters["navident"] ?: this.respond(HttpStatusCode.BadRequest)
    return ident as String
}
