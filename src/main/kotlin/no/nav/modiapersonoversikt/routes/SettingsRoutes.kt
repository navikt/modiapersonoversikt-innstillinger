package no.nav.modiapersonoversikt.routes

import io.ktor.application.ApplicationCall
import io.ktor.application.ApplicationCallPipeline
import io.ktor.application.call
import io.ktor.auth.*
import io.ktor.auth.jwt.JWTPrincipal
import io.ktor.http.HttpStatusCode
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.*
import io.ktor.util.pipeline.PipelinePhase
import no.nav.modiapersonoversikt.MockPayload
import no.nav.modiapersonoversikt.storage.StorageProvider

fun Route.conditionalAuthenticate(useAuthentication: Boolean, build: Route.() -> Unit): Route {
    if (useAuthentication) {
        return authenticate(build = build)
    }
    val route = createChild(AuthenticationRouteSelector(listOf<String?>(null)))
    route.insertPhaseAfter(ApplicationCallPipeline.Features, Authentication.AuthenticatePhase)
    route.intercept(Authentication.AuthenticatePhase) {
        this.context.authentication.principal = JWTPrincipal(MockPayload("Z999999"))
    }
    route.build()
    return route
}

fun Route.settingsRoutes(provider: StorageProvider, useAuthentication: Boolean) {
    conditionalAuthenticate(useAuthentication) {
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
    return this.principal<JWTPrincipal>()
            ?.payload
            ?.subject
}
