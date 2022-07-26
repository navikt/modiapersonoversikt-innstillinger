package no.nav.modiapersonoversikt

import io.ktor.http.*
import no.nav.personoversikt.ktor.utils.Security
import no.nav.personoversikt.ktor.utils.Security.AuthProviderConfig
import no.nav.personoversikt.utils.EnvUtils.getConfig
import no.nav.personoversikt.utils.EnvUtils.getRequiredConfig

private val defaultValues = mapOf(
    "NAIS_CLUSTER_NAME" to "local",
    "ISSO_JWKS_URL" to "https://isso-q.adeo.no/isso/oauth2/connect/jwk_uri",
    "ISSO_ISSUER" to "https://isso-q.adeo.no:443/isso/oauth2",
    "DATABASE_JDBC_URL" to "jdbc:h2:mem:modiapersonoversikt-innstillinger;MODE=PostgreSQL;DB_CLOSE_DELAY=-1",
//                "DATABASE_JDBC_URL" to "jdbc:h2:tcp://localhost:8090/./modiapersonoversikt-innstillinger",
//                "DATABASE_JDBC_URL" to "jdbc:postgresql://localhost:5432/modiapersonoversikt-innstillinger",
    "VAULT_MOUNTPATH" to ""
)

data class DatabaseConfig(
    val jdbcUrl: String,
    val vaultMountpath: String,
)

class Configuration(
    val clusterName: String = getRequiredConfig("NAIS_CLUSTER_NAME", defaultValues),
    val openam: AuthProviderConfig = AuthProviderConfig(
        name = OpenAM,
        jwksConfig = Security.JwksConfig.JwksUrl(
            getRequiredConfig("ISSO_JWKS_URL", defaultValues),
            getRequiredConfig("ISSO_ISSUER", defaultValues)
        ),
        tokenLocations = listOf(
            Security.TokenLocation.Cookie(name = "modia_ID_token"),
            Security.TokenLocation.Cookie(name = "ID_token")
        )
    ),
    val azuread: AuthProviderConfig? =
        getConfig("AZURE_APP_WELL_KNOWN_URL", defaultValues)?.let { jwksurl ->
            AuthProviderConfig(
                name = AzureAD,
                jwksConfig = Security.JwksConfig.OidcWellKnownUrl(jwksurl),
                tokenLocations = listOf(
                    Security.TokenLocation.Header(HttpHeaders.Authorization)
                )
            )
        },
    val databaseConfig: DatabaseConfig = DatabaseConfig(
        jdbcUrl = getRequiredConfig("DATABASE_JDBC_URL", defaultValues),
        vaultMountpath = getRequiredConfig("VAULT_MOUNTPATH", defaultValues),
    )
)
