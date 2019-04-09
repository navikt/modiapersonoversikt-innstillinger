package no.nav.modiapersonoversikt.storage

import no.nav.modiapersonoversikt.Configuration

private val configuration = Configuration()

class S3StorageProvider : StorageProvider {
    override fun loadData(): MutableMap<String, MutableMap<String, Any>> {
        return mutableMapOf()
    }

    override fun storeData(data: MutableMap<String, MutableMap<String, Any>>) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}