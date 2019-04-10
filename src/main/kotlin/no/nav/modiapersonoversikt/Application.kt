package no.nav.modiapersonoversikt

import no.nav.modiapersonoversikt.storage.DataCache
import no.nav.modiapersonoversikt.storage.S3StorageProvider
import org.slf4j.LoggerFactory
import java.util.concurrent.TimeUnit

private val log = LoggerFactory.getLogger("modiapersonoversikt-innstillinger.Application")
data class ApplicationState(var running: Boolean = true, var initialized: Boolean = false)
val configuration = Configuration()

fun main() {
    val applicationState = ApplicationState()
    val datacache = DataCache(S3StorageProvider())
    val applicationServer = createHttpServer(applicationState, datacache)

    Runtime.getRuntime().addShutdownHook(Thread {
        log.info("Shutdown hook called, shutting down gracefully")
        datacache.saveCache()
        applicationState.initialized = false
        applicationServer.stop(5, 5, TimeUnit.SECONDS)
    })

    applicationServer.start(wait = true)
}