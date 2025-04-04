package no.nav.modiapersonoversikt

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.*
import io.ktor.server.plugins.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import org.slf4j.LoggerFactory

private val exceptionLog = LoggerFactory.getLogger("modiapersonoversikt-innstillinger.ExceptionHandler")

fun StatusPagesConfig.exceptionHandler() {
    exception<Throwable> { call, cause ->
        call.logErrorAndRespond(cause) { "An internal error occurred during routing" }
    }

    exception<IllegalArgumentException> { call, cause ->
        call.logErrorAndRespond(cause, HttpStatusCode.BadRequest) {
            "The request was either invalid or lacked required parameters"
        }
    }
}

fun StatusPagesConfig.notFoundHandler() {
    status(HttpStatusCode.NotFound) { call, _ ->
        call.respond(
            HttpStatusCode.NotFound,
            HttpErrorResponse(
                message = "The page or operation requested does not exist.",
                code = HttpStatusCode.NotFound,
                url = call.request.url(),
            ),
        )
    }
}

private suspend inline fun ApplicationCall.logErrorAndRespond(
    cause: Throwable,
    status: HttpStatusCode = HttpStatusCode.InternalServerError,
    lazyMessage: () -> String,
) {
    val message = lazyMessage()
    exceptionLog.error(message, cause)
    val response =
        HttpErrorResponse(
            url = this.request.url(),
            cause = cause.toString(),
            message = message,
            code = status,
        )
    exceptionLog.error("Status Page Response: $response")
    this.respond(status, response)
}

internal data class HttpErrorResponse(
    val url: String,
    val message: String? = null,
    val cause: String? = null,
    val code: HttpStatusCode = HttpStatusCode.InternalServerError,
)

internal fun ApplicationRequest.url(): String {
    val port =
        when (origin.serverPort) {
            in listOf(80, 443) -> ""
            else -> ":${origin.serverPort}"
        }
    return "${origin.scheme}://${origin.serverHost}$port${origin.uri}"
}
