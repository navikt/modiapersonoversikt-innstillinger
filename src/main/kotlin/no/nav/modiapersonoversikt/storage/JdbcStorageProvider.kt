package no.nav.modiapersonoversikt.storage

import kotliquery.Session
import kotliquery.queryOf
import no.nav.modiapersonoversikt.model.UserSettingsMap
import no.nav.modiapersonoversikt.model.UserSettings
import java.time.LocalDateTime
import javax.sql.DataSource

private val innstillingerTable = "innstillinger"
private val sistOppdatertTable = "sist_oppdatert"

class JdbcStorageProvider(private val dataSource: DataSource) : StorageProvider {
    override suspend fun getData(ident: String): UserSettings {
        return transactional(dataSource) { tx -> getData(tx, ident) }
    }

    override suspend fun storeData(ident: String, innstillinger: UserSettingsMap): UserSettings {
        return transactional(dataSource) { tx ->
            deleteData(tx, ident)

            innstillinger.forEach { (navn, verdi) ->
                tx.run(
                        queryOf("INSERT INTO $innstillingerTable (ident, navn, verdi) VALUES(?, ?, ?)", ident, navn, verdi).asUpdate
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
        val sistLagret : LocalDateTime = tx.run(
                queryOf("SELECT tidspunkt FROM $sistOppdatertTable WHERE ident = ?", ident)
                        .map { row ->  row.localDateTime("tidspunkt") }
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
