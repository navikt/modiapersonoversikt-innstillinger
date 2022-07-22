package no.nav.modiapersonoversikt

import io.ktor.server.netty.*
import no.nav.personoversikt.ktor.utils.KtorServer

fun main() {
    val configuration = Configuration()
    val dbConfig = DataSourceConfiguration(configuration)

    DataSourceConfiguration.migrateDb(dbConfig.adminDataSource())

    KtorServer.create(Netty, 7070) {
        innstillingerApp(
            configuration = configuration,
            dataSource = dbConfig.userDataSource(),
            useMock = false
        )
    }.start(wait = true)
}
