package no.nav.modiapersonoversikt.storage

class TestStorageProvider : StorageProvider {
    override fun loadData(): MutableMap<String, MutableMap<String, Any>> {
        return mutableMapOf()
    }

    override fun storeData(data: MutableMap<String, MutableMap<String, Any>>) {

    }

    override fun clearData() {

    }
}