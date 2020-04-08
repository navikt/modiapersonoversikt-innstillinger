package no.nav.modiapersonoversikt.storage

import no.nav.modiapersonoversikt.model.UserSettings
import no.nav.modiapersonoversikt.model.UserSettingsMap

interface StorageProvider {
    suspend fun getData(ident: String): UserSettings

    suspend fun storeData(ident: String, settings: UserSettingsMap): UserSettings

    suspend fun clearData(ident: String): UserSettings
}
