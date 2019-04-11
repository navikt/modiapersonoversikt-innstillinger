package no.nav.modiapersonoversikt.routes

import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.Routing
import io.ktor.routing.delete
import io.ktor.routing.get
import io.ktor.routing.post
import no.nav.modiapersonoversikt.storage.StorageProvider

fun Routing.settingsRoutes(provider: StorageProvider) {
    post("/innstillinger/{navident}") {
        val ident = call.parameters["navident"] ?: call.respond(HttpStatusCode.BadRequest)
        provider.storeData(ident as String, call.receive())
        call.respond(HttpStatusCode.OK)
    }

    get("/innstillinger/{navident}") {
        val ident = call.parameters["navident"] ?: call.respond(HttpStatusCode.BadRequest)
        provider.getData(ident as String)?.let { call.respond(it) } ?: call.respond(HttpStatusCode.NotFound)
    }

    delete("/innstillinger/{navident}") {
        val ident = call.parameters["navident"] ?: call.respond(HttpStatusCode.BadRequest)
        provider.clearData(ident as String)
        call.respond(HttpStatusCode.OK)
    }
}