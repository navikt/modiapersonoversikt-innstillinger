package no.nav.modiapersonoversikt

import no.nav.personoversikt.ktor.utils.Security.AuthCookie
import no.nav.personoversikt.ktor.utils.Security.AuthProviderConfig
import no.nav.personoversikt.utils.ConditionalUtils.ifNotNull
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
        jwksUrl = getRequiredConfig("ISSO_JWKS_URL", defaultValues),
        cookies = listOf(AuthCookie("modia_ID_token"), AuthCookie("ID_token"))
    ),
    val azuread: AuthProviderConfig? = ifNotNull(
        getConfig("AZURE_OPENID_CONFIG_JWKS_URI", defaultValues),
        getConfig("SECRET", defaultValues)
    ) { jwksurl, secret ->
        AuthProviderConfig(
            name = AzureAD,
            jwksUrl = jwksurl,
            cookies = listOf(
                AuthCookie(
                    name = "modiapersonoversikt_tokens",
                    encryptionKey = secret
                )
            )
        )
    },
    val databaseConfig: DatabaseConfig = DatabaseConfig(
        jdbcUrl = getRequiredConfig("DATABASE_JDBC_URL", defaultValues),
        vaultMountpath = getRequiredConfig("VAULT_MOUNTPATH", defaultValues),
    )
)
