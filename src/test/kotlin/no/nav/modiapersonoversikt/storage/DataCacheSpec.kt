package no.nav.modiapersonoversikt.storage

import no.nav.modiapersonoversikt.model.UserSetting
import org.amshove.kluent.`should be`
import org.amshove.kluent.`should equal`
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on

private const val IDENT1 = "T000001"
private const val IDENT2 = "T000002"

private const val KEY_STR_VAL = "KEYSTR"
private const val STRING_VALUE = "STRING"
private const val ALTERNATE_STRING_VALUE = "ALTERNATE"

private const val KEY_BOOL_VAL = "KEYBOOL"
private const val BOOLEAN_VALUE = true

private val SETTING1 = UserSetting(KEY_STR_VAL, STRING_VALUE)
private val SETTING1_ALT = UserSetting(KEY_STR_VAL, ALTERNATE_STRING_VALUE)
private val SETTING2 = UserSetting(KEY_BOOL_VAL, BOOLEAN_VALUE)

object DataCacheSpec : Spek({
    lateinit var dataCache: DataCache

    describe("data cache cache") {
        on("add single setting to one user") {
            dataCache = DataCache(StorageProviderTestImpl())
            dataCache.addToCache(IDENT1, SETTING1)

            it("should find value for ident1") {
                val theValue = dataCache.getFromCache(IDENT1, KEY_STR_VAL)
                theValue `should equal` STRING_VALUE
            }

            it("should not find value for ident1 and different key") {
                val theValue = dataCache.getFromCache(IDENT1, KEY_BOOL_VAL)
                theValue `should be` null
            }

            it("should not find value for ident2") {
                val theValue = dataCache.getFromCache(IDENT2, KEY_STR_VAL)
                theValue `should be` null
            }

            it("should have map of size 1 for ident1") {
                val theValue = dataCache.getFromCache(IDENT1)
                theValue.size `should equal` 1
            }

            it("should have map of size 0 for ident2") {
                val theValue = dataCache.getFromCache(IDENT2)
                theValue.size `should equal` 0
            }
        }

        on("add several settings to one user") {
            dataCache = DataCache(StorageProviderTestImpl())
            dataCache.addToCache(IDENT1, SETTING1)
            dataCache.addToCache(IDENT1, SETTING2)

            it("should have map of size 2 for ident1") {
                val theValue = dataCache.getFromCache(IDENT1)
                theValue.size `should equal` 2
            }

            it ("should return proper boolean value") {
                val theValue = dataCache.getFromCache(IDENT1, KEY_BOOL_VAL)
                theValue `should be` true
            }
        }

        on("overwriting setting for a user") {
            dataCache = DataCache(StorageProviderTestImpl())
            dataCache.addToCache(IDENT1, SETTING1)
            dataCache.addToCache(IDENT1, SETTING1_ALT)

            it("should still have map of size 1 for ident1") {
                val theValue = dataCache.getFromCache(IDENT1)
                theValue.size `should equal` 1
            }

            it("should have the alternative value in cache") {
                val theValue = dataCache.getFromCache(IDENT1, KEY_STR_VAL)
                theValue `should equal` ALTERNATE_STRING_VALUE
            }
        }
    }
})