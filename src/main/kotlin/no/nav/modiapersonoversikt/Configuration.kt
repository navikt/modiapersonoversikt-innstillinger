package no.nav.modiapersonoversikt

import io.ktor.http.*
import no.nav.personoversikt.ktor.utils.Security
import no.nav.personoversikt.ktor.utils.Security.AuthProviderConfig
import no.nav.personoversikt.utils.EnvUtils.getConfig
import no.nav.personoversikt.utils.EnvUtils.getRequiredConfig

private val defaultValues = mapOf(
    "NAIS_CLUSTER_NAME" to "local",
    "DATABASE_JDBC_URL" to "jdbc:h2:mem:modiapersonoversikt-innstillinger;MODE=PostgreSQL;DB_CLOSE_DELAY=-1",
    "VAULT_MOUNTPATH" to "",
    "DB_NAME" to "modiapersonoversikt-innstillinger-pg15"
)

data class DatabaseConfig(
    val jdbcUrl: String,
    val vaultMountpath: String,
    val dbName: String
)

class Configuration(
    val clusterName: String = getRequiredConfig("NAIS_CLUSTER_NAME", defaultValues),
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
        dbName = getRequiredConfig("DB_NAME", defaultValues)
    )
)
