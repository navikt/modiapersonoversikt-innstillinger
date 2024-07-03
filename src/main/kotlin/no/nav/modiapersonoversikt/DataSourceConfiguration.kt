package no.nav.modiapersonoversikt

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.flywaydb.core.Flyway
import javax.sql.DataSource

class DataSourceConfiguration(val env: Configuration) {
    private var userDataSource = createDatasource("user")
    private var adminDataSource = createDatasource("admin")

    fun userDataSource() = userDataSource

    fun runFlyway() {
        Flyway
            .configure()
            .dataSource(adminDataSource)
            .also {
                if (adminDataSource is HikariDataSource) {
                    val dbUser = dbRole(env.databaseConfig.dbName, "admin")
                    it.initSql("SET ROLE '$dbUser'")
                }
            }
            .load()
            .migrate()
    }

    private fun createDatasource(user: String): DataSource {
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

        return HikariDataSource(config)
    }

    private fun dbRole(dbName: String, user: String): String = "$dbName-$user"
}
