package no.nav.modiapersonoversikt

import org.slf4j.LoggerFactory

private val log = LoggerFactory.getLogger("modiapersonoversikt-innstillinger.LocalRun")

fun main() {
    val configuration = Configuration()
    val dbConfig = DataSourceConfiguration(configuration)
    val applicationState = ApplicationState()

    DataSourceConfiguration.migrateDb(dbConfig.adminDataSource())

    val applicationServer = createHttpServer(
            applicationState = applicationState,
            port = 7070,
            configuration = Configuration(),
            dataSource = dbConfig.userDataSource()
    )

    Runtime.getRuntime().addShutdownHook(Thread {
        log.info("Shutdown hook called, shutting down gracefully")
        applicationState.initialized = false
        applicationServer.stop(1000, 1000)
    })

    applicationServer.start(wait = true)
}
