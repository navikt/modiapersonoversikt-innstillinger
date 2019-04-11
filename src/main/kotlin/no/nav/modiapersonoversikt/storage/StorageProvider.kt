package no.nav.modiapersonoversikt.storage

import no.nav.modiapersonoversikt.model.UserSettings

interface StorageProvider {
    fun getData(ident: String): UserSettings?

    fun storeData(ident: String, data: UserSettings)

    fun clearData(ident: String)
}