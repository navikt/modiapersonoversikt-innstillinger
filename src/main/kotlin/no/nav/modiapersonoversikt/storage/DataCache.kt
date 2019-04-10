package no.nav.modiapersonoversikt.storage

import no.nav.modiapersonoversikt.model.UserSetting

class DataCache(private val provider: StorageProvider) {
    private val cache: MutableMap<String, MutableMap<String, Any>> = provider.loadData()

    fun addToCache(ident: String, setting: UserSetting) {
        val settingsMap = cache.getOrPut(ident) { mutableMapOf() }
        settingsMap[setting.key] = setting.value
    }

    fun getFromCache(ident: String): MutableMap<String, Any> = cache.getOrDefault(ident, mutableMapOf())

    fun getFromCache(ident: String, settingsKey: String): Any? = cache[ident]?.get(settingsKey)

    fun removeFromCache(ident: String): Boolean = cache.remove(ident) != null

    fun removeFromCache(ident: String, settingsKey: String): Boolean {
        val map = getFromCache(ident)
        return map.remove(settingsKey) != null
    }

    fun clearWholeCache() {
        cache.clear()
        provider.clearData()
    }

    fun saveCache() {
        provider.storeData(cache)
    }
}