package no.nav.modiapersonoversikt.infrastructure

import com.auth0.jwk.JwkProvider
import com.auth0.jwk.JwkProviderBuilder
import com.auth0.jwt.JWT
import com.auth0.jwt.interfaces.Payload
import io.ktor.http.*
import io.ktor.http.auth.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import no.nav.modiapersonoversikt.AuthProviderConfig
import org.slf4j.LoggerFactory
import java.net.URL
import java.util.concurrent.TimeUnit

fun AuthenticationConfig.setupMock(name: String? = null, principal: SubjectPrincipal) {
    val config = object : AuthenticationProvider.Config(name) {}
    register(
        object : AuthenticationProvider(config) {
            override suspend fun onAuthenticate(context: AuthenticationContext) {
                context.principal = principal
            }
        }
    )
}

fun AuthenticationConfig.setupJWT(config: AuthProviderConfig) {
    jwt(config.name) {
        if (config.usesCookies) {
            authHeader {
                Security.getToken(it)?.let(::parseAuthorizationHeader)
            }
        }
        verifier(Security.makeJwkProvider(config.jwksUrl))
        validate { Security.validateJWT(it) }
    }
}

object Security {
    const val OpenAM = "openam"
    const val AzureAD = "azuread"
    private val log = LoggerFactory.getLogger("modiapersonoversikt-innstillinger.Security")
    private val cookieNames = listOf("modia_ID_token", "ID_token")

    fun getSubject(call: ApplicationCall): String {
        return try {
            getToken(call)
                ?.let(JWT::decode)
                ?.getIdent()
                ?: "Unauthenticated"
        } catch (e: Throwable) {
            "Invalid JWT"
        }
    }

    fun getToken(call: ApplicationCall): String? {
        return call.request.header(HttpHeaders.Authorization)
            ?: cookieNames
                .find { !call.request.cookies[it].isNullOrEmpty() }
                ?.let { call.request.cookies[it] }
                ?.let { "Bearer $it" }
    }

    internal fun makeJwkProvider(jwksUrl: String): JwkProvider =
        JwkProviderBuilder(URL(jwksUrl))
            .cached(10, 24, TimeUnit.HOURS)
            .rateLimited(10, 1, TimeUnit.MINUTES)
            .build()

    internal fun validateJWT(credentials: JWTCredential): Principal? {
        return try {
            requireNotNull(credentials.payload.audience) { "Audience not present" }
            SubjectPrincipal(credentials.payload.getIdent())
        } catch (e: Exception) {
            log.error("Failed to validateJWT token", e)
            null
        }
    }

    private fun Payload.getIdent(): String {
        return this.getClaim("NAVident")?.asString() ?: this.subject
    }
}

class SubjectPrincipal(val subject: String) : Principal
