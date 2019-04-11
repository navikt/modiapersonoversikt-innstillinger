package no.nav.modiapersonoversikt.storage

import no.nav.modiapersonoversikt.model.UserSettings

class LocalStorageProvider : StorageProvider {

    val cache: MutableMap<String, UserSettings> = mutableMapOf()

    override fun getData(ident: String): UserSettings? {
        return cache[ident]
    }

    override fun storeData(ident: String, data: UserSettings) {
        cache[ident] = data
    }

    override fun clearData(ident: String) {
        cache.remove(ident)
    }

}