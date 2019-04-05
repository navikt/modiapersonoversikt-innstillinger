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
        addToCache(ident as String, call.receive())
        call.respond(HttpStatusCode.OK)
    }

    get("/innstillinger/{navident}") {
        val ident = call.parameters["navident"] ?: call.respond(HttpStatusCode.BadRequest)
        call.respond(getFromCache(ident as String))
    }

    get("/innstillinger/{navident}/{innstilling}") {
        val ident = call.parameters["navident"] ?: call.respond(HttpStatusCode.BadRequest)
        val settingKey = call.parameters["innstilling"] ?: call.respond(HttpStatusCode.BadRequest)
        val setting = getFromCache(ident as String, settingKey as String) ?: call.respond(HttpStatusCode.NotFound)
        call.respond(setting)
    }
}

fun addToCache(ident: String, setting: UserSetting) {
    val settingsMap = cache.getOrPut(ident) { mutableMapOf() }
    settingsMap[setting.key] = setting.value
}

fun getFromCache(ident: String): MutableMap<String, Any> = cache.getOrDefault(ident, mutableMapOf())

fun getFromCache(ident: String, settingsKey: String): Any? = cache[ident]?.get(settingsKey)