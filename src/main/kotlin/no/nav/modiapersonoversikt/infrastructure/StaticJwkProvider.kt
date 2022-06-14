package no.nav.modiapersonoversikt.infrastructure

import com.auth0.jwk.Jwk
import com.auth0.jwk.JwkProvider
import com.auth0.jwk.SigningKeyNotFoundException
import com.fasterxml.jackson.databind.ObjectMapper

class StaticJwkProvider(jwk: String) : JwkProvider {
    companion object {
        private val reader = ObjectMapper().readerFor(MutableMap::class.java)
        private fun decodeJWK(value: String): Jwk = Jwk.fromValues(
            reader.readValue(value)
        )
    }

    private val jwk: Jwk = decodeJWK(jwk)

    override fun get(keyId: String?): Jwk {
        if (jwk.id !== keyId) {
            throw SigningKeyNotFoundException("Could not find key with id: $keyId", null)
        }
        return jwk
    }
}
