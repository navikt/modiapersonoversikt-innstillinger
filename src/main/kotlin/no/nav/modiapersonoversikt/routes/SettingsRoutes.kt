package no.nav.modiapersonoversikt.routes

import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.Routing
import io.ktor.routing.get
import io.ktor.routing.post
import no.nav.modiapersonoversikt.model.UserSetting
import no.nav.modiapersonoversikt.storage.DataCache
import no.nav.modiapersonoversikt.storage.S3StorageProvider

private val cache = DataCache(S3StorageProvider())

fun Routing.settingsRoutes() {
    post("/innstillinger/{navident}") {
        val ident = call.parameters["navident"] ?: call.respond(HttpStatusCode.BadRequest)
        cache.addToCache(ident as String, call.receive())
        call.respond(HttpStatusCode.OK)
    }

    get("/innstillinger/{navident}") {
        val ident = call.parameters["navident"] ?: call.respond(HttpStatusCode.BadRequest)
        call.respond(cache.getFromCache(ident as String))
    }

    get("/innstillinger/{navident}/{innstilling}") {
        val ident = call.parameters["navident"] ?: call.respond(HttpStatusCode.BadRequest)
        val settingKey = call.parameters["innstilling"] ?: call.respond(HttpStatusCode.BadRequest)
        val setting = cache.getFromCache(ident as String, settingKey as String) ?: call.respond(HttpStatusCode.NotFound)
        call.respond(setting)
    }
}