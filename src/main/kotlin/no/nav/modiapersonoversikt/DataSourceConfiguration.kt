package no.nav.modiapersonoversikt

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import no.nav.vault.jdbc.hikaricp.HikariCPVaultUtil
import org.flywaydb.core.Flyway
import javax.sql.DataSource

class DataSourceConfiguration(val env: Configuration) {
    private var userDataSource = createDatasource("user")
    private var adminDataSource = createDatasource("admin")

    fun userDataSource() = userDataSource
    fun adminDataSource() = adminDataSource

    fun runFlyway() {
        Flyway
            .configure()
            .dataSource(adminDataSource)
            .also {
                if (adminDataSource is HikariDataSource && (env.clusterName == "dev-fss" || env.clusterName == "prod-fss")) {
                    val dbUser = dbRole(env.databaseConfig.dbName, "admin")
                    it.initSql("SET ROLE '$dbUser'")
                }
            }
            .load()
            .migrate()
    }

    private fun createDatasource(user: String): DataSource {
        val mountPath = env.databaseConfig.vaultMountpath
        val config = HikariConfig()
        config.jdbcUrl = env.databaseConfig.jdbcUrl
        config.minimumIdle = 0
        config.maximumPoolSize = 4
        config.connectionTimeout = 5000
        config.maxLifetime = 30000
        config.isAutoCommit = false

        log.info("Creating DataSource to: ${env.databaseConfig.jdbcUrl}")

        if (env.clusterName == "local") {
            config.username = "sa"
            config.password = "sa"
            return HikariDataSource(config)
        }

        if (env.clusterName == "dev-gcp" || env.clusterName == "prod-gcp") {
            return HikariDataSource(config)
        }

        return HikariCPVaultUtil.createHikariDataSourceWithVaultIntegration(
            config,
            mountPath,
            dbRole(env.databaseConfig.dbName, user)
        )
    }

    private fun dbRole(dbName: String, user: String): String = "$dbName-$user"
}
