package no.nav.modiapersonoversikt.routes

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import no.nav.modiapersonoversikt.storage.StorageProvider
import no.nav.personoversikt.ktor.utils.Security.SubjectPrincipal

fun Route.settingsRoutes(authproviders: Array<String?>, provider: StorageProvider) {
    authenticate(*authproviders) {
        route("/innstillinger") {
            get {
                val response = call.getNavident()
                    ?.let { ident -> provider.getData(ident) }
                    ?: HttpStatusCode.BadRequest
                call.respond(response)
            }

            post {
                val response = call.getNavident()
                    ?.let { ident -> provider.storeData(ident, call.receive()) }
                    ?: HttpStatusCode.BadRequest
                call.respond(response)
            }

            delete {
                val response = call.getNavident()
                    ?.let { ident -> provider.clearData(ident) }
                    ?: HttpStatusCode.BadRequest
                call.respond(response)
            }
        }
    }
}

private fun ApplicationCall.getNavident(): String? {
    return this.principal<SubjectPrincipal>()?.subject
}
