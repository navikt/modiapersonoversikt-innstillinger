package no.nav.modiapersonoversikt.routes

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

private const val KEY_BOOL_VAL = "KEYBOOL"
private const val BOOLEAN_VALUE = true

private val SETTING1 = UserSetting(KEY_STR_VAL, STRING_VALUE)
private val SETTING2 = UserSetting(KEY_BOOL_VAL, BOOLEAN_VALUE)

object SettingsRoutesSpec : Spek({
    describe("settings routes cache") {
        on("add single setting to one user") {
            addToCache(IDENT1, SETTING1)

            it("should find value for ident1") {
                val theValue = getFromCache(IDENT1, KEY_STR_VAL)
                theValue `should equal` STRING_VALUE
            }

            it("should not find value for ident1 and different key") {
                val theValue = getFromCache(IDENT1, KEY_BOOL_VAL)
                theValue `should be` null
            }

            it("should not find value for ident2") {
                val theValue = getFromCache(IDENT2, KEY_STR_VAL)
                theValue `should be` null
            }

            it("should have map of size 1 for ident1") {
                val theValue = getFromCache(IDENT1)
                theValue.size `should equal` 1
            }

            it("should have map of size 0 for ident2") {
                val theValue = getFromCache(IDENT2)
                theValue.size `should equal` 0
            }
        }

        on("add several settings to one user") {
            addToCache(IDENT1, SETTING1)
            addToCache(IDENT1, SETTING2)

            it("should have map of size 2 for ident1") {
                val theValue = getFromCache(IDENT1)
                theValue.size `should equal` 2
            }

            it ("should return proper boolean value") {
                val theValue = getFromCache(IDENT1, KEY_BOOL_VAL)
                theValue `should be` true
            }
        }
    }
})