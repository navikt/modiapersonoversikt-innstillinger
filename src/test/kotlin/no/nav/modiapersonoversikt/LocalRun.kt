package no.nav.modiapersonoversikt

import io.ktor.server.netty.*
import no.nav.personoversikt.common.ktor.utils.KtorServer

fun runLocally(useMock: Boolean) {
    val configuration = Configuration()
    val dbConfig = DataSourceConfiguration(configuration)

    dbConfig.runFlyway()

    KtorServer.create(Netty, 7070) {
        innstillingerApp(
            configuration = Configuration(),
            dataSource = dbConfig.userDataSource(),
            useMock = useMock
        )
    }.start(wait = true)
}

fun main() {
    runLocally(false)
}
