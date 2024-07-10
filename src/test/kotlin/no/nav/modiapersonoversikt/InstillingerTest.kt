package no.nav.modiapersonoversikt

import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.testing.*
import no.nav.modiapersonoversikt.ObjectMapperProvider.Companion.objectMapper
import no.nav.modiapersonoversikt.model.UserSettings
import kotlin.test.Test
import kotlin.test.assertEquals

internal class InstillingerTest {
    @Test
    fun `should get innstillinger`() =
        testInnstillingerApplication {
            val res = client.get("/api/innstillinger")
            assertEquals(HttpStatusCode.OK, res.status)
        }

    @Test
    fun `should respond correctly to post`() =
        testInnstillingerApplication {
            val updatedInnstillinger = mapOf("test-key" to "value")
            val client =
                createClient {
                    install(ContentNegotiation) { json() }
                }
            val res =
                client.post("/api/innstillinger") {
                    contentType(ContentType.Application.Json)
                    setBody(updatedInnstillinger)
                }

            assertEquals(res.status, HttpStatusCode.OK)
            val settings = objectMapper.readValue(res.bodyAsText(), UserSettings::class.java)
            assertEquals(updatedInnstillinger["test-key"], settings.innstillinger["test-key"])
        }

    private fun testInnstillingerApplication(block: suspend ApplicationTestBuilder.() -> Unit) =
        testApplication {
            application {
                val configuration = Configuration()
                val dbConfig = DataSourceConfiguration(configuration)
                dbConfig.runFlyway()
                innstillingerApp(configuration = configuration, dataSource = dbConfig.userDataSource(), useMock = true)
            }

            block()
        }
}
