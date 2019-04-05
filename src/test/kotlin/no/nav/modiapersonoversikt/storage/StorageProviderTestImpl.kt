package no.nav.modiapersonoversikt.storage

class StorageProviderTestImpl : StorageProvider {
    override fun loadData(): MutableMap<String, MutableMap<String, Any>> {
        return mutableMapOf()
    }

    override fun storeData(data: MutableMap<String, MutableMap<String, Any>>) {

    }

}