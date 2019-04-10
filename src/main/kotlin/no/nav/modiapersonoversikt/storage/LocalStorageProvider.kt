package no.nav.modiapersonoversikt.storage

class LocalStorageProvider : StorageProvider {

    override fun loadData(): MutableMap<String, MutableMap<String, Any>> {
        return mutableMapOf()
    }

    override fun storeData(data: MutableMap<String, MutableMap<String, Any>>) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun clearData() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}