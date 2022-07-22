package no.nav.modiapersonoversikt.storage

import kotlinx.coroutines.runBlocking
import kotliquery.Session
import kotliquery.queryOf
import no.nav.modiapersonoversikt.model.UserSettings
import no.nav.modiapersonoversikt.model.UserSettingsMap
import no.nav.personoversikt.ktor.utils.Selftest
import java.time.LocalDateTime
import javax.sql.DataSource
import kotlin.concurrent.fixedRateTimer
import kotlin.time.Duration.Companion.seconds

private const val innstillingerTable = "innstillinger"
private const val sistOppdatertTable = "sist_oppdatert"

class JdbcStorageProvider(private val dataSource: DataSource) : StorageProvider {
    private val selftest = Selftest.Reporter("Database", true)

    init {
        fixedRateTimer("Database check", daemon = true, initialDelay = 0, period = 10.seconds.inWholeMilliseconds) {
            runBlocking {
                selftest.ping {
                    getData("Z999999")
                }
            }
        }
    }

    override suspend fun getData(ident: String): UserSettings {
        return transactional(dataSource) { tx -> getData(tx, ident) }
    }

    override suspend fun storeData(ident: String, settings: UserSettingsMap): UserSettings {
        return transactional(dataSource) { tx ->
            deleteData(tx, ident)

            settings.forEach { (navn, verdi) ->
                tx.run(
                    queryOf(
                        "INSERT INTO $innstillingerTable (ident, navn, verdi) VALUES(?, ?, ?)",
                        ident,
                        navn,
                        verdi
                    ).asUpdate
                )
            }

            tx.run(
                queryOf("INSERT INTO $sistOppdatertTable (ident) VALUES(?)", ident).asUpdate
            )

            getData(tx, ident)
        }
    }

    override suspend fun clearData(ident: String): UserSettings {
        return transactional(dataSource) { tx ->
            deleteData(tx, ident)
            getData(tx, ident)
        }
    }

    private fun getData(tx: Session, ident: String): UserSettings {
        val sistLagret: LocalDateTime = tx.run(
            queryOf("SELECT tidspunkt FROM $sistOppdatertTable WHERE ident = ?", ident)
                .map { row -> row.localDateTime("tidspunkt") }
                .asSingle
        ) ?: LocalDateTime.now()

        val innstillinger = tx.run(
            queryOf("SELECT navn, verdi FROM $innstillingerTable WHERE ident = ?", ident)
                .map { row -> Pair(row.string("navn"), row.string("verdi")) }
                .asList
        ).toMap()

        return UserSettings(sistLagret, innstillinger)
    }

    private fun deleteData(tx: Session, ident: String) {
        tx.run(queryOf("DELETE FROM $innstillingerTable WHERE ident = ?", ident).asUpdate)
        tx.run(queryOf("DELETE FROM $sistOppdatertTable WHERE ident = ?", ident).asUpdate)
    }
}
