package no.nav.modiapersonoversikt

import com.typesafe.config.ConfigResolver
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import no.nav.vault.jdbc.hikaricp.HikariCPVaultUtil
import org.flywaydb.core.Flyway
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import javax.sql.DataSource

class DataSourceConfiguration(val env: Configuration) {
    private var userDataSource = createDatasource("user")
    private var adminDataSource = createDatasource("admin")

    fun userDataSource() = userDataSource
    fun adminDataSource() = adminDataSource

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

        return HikariCPVaultUtil.createHikariDataSourceWithVaultIntegration(
            config,
            mountPath,
            dbRole(env.databaseConfig.dbName, user)
        )
    }

    companion object {
        private val log: Logger = LoggerFactory.getLogger(DataSourceConfiguration::class.java)
        private fun dbRole(dbName: String, user: String): String = "$dbName-$user"

        fun migrateDb(configuration: Configuration, dataSource: DataSource) {
            Flyway
                .configure()
                .dataSource(dataSource)
                .also {
                    if (dataSource is HikariDataSource && !dataSource.jdbcUrl.contains(":h2:")) {
                        val dbUser = dbRole(configuration.databaseConfig.dbName, "admin")
                        it.initSql("SET ROLE '$dbUser'")
                    }
                }
                .load()
                .migrate()
        }
    }
}
