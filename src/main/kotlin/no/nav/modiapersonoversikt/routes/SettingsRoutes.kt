package no.nav.modiapersonoversikt.routes

import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.Routing
import io.ktor.routing.get
import io.ktor.routing.post
import no.nav.modiapersonoversikt.model.UserSetting

private val cache: MutableMap<String, MutableMap<String, Any>> = mutableMapOf()

fun Routing.settingsRoutes() {
    post("/innstillinger/{navident}") {
        val ident = call.parameters["navident"] ?: call.respond(HttpStatusCode.BadRequest)
        val setting: UserSetting = call.receive()
        val settings = cache.getOrPut(ident as String) { mutableMapOf() }
        settings[setting.key] = setting.value
    }

    get("/innstillinger/{navident}") {
        val ident = call.parameters["navident"] ?: call.respond(HttpStatusCode.BadRequest)
        call.respond(cache.getOrDefault(ident as String, mutableMapOf()))
    }

    get("/innstillinger/{navident}/{innstilling}") {
        val ident = call.parameters["navident"]
        val settingKey = call.parameters["innstilling"]
        val setting = cache[ident]?.get(settingKey) ?: call.respond(HttpStatusCode.NotFound)
        call.respond(setting)
    }
}