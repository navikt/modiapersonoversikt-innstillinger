package no.nav.modiapersonoversikt

import org.slf4j.LoggerFactory

private val log = LoggerFactory.getLogger("modiapersonoversikt-innstillinger.Application")
data class ApplicationState(var running: Boolean = true, var initialized: Boolean = false)

fun main() {
    val configuration = Configuration()
    val dbConfig = DataSourceConfiguration(configuration)
    val applicationState = ApplicationState()

    DataSourceConfiguration.migrateDb(dbConfig.adminDataSource())

    val applicationServer = createHttpServer(
        applicationState = applicationState,
        configuration = configuration,
        dataSource = dbConfig.userDataSource(),
        useMock = false
    )

    Runtime.getRuntime().addShutdownHook(
        Thread {
            log.info("Shutdown hook called, shutting down gracefully")
            applicationState.initialized = false
            applicationServer.stop(5000, 5000)
        }
    )

    applicationServer.start(wait = true)
}
