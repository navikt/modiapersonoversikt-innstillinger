package no.nav.modiapersonoversikt.storage

interface StorageProvider {
    fun loadData(): MutableMap<String, MutableMap<String, Any>>

    fun storeData(data: MutableMap<String, MutableMap<String, Any>>)
}