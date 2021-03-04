package no.nav.modiapersonoversikt

import com.fasterxml.jackson.core.util.DefaultIndenter
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper

class ObjectMapperProvider {
    companion object {
        val objectMapper = jacksonObjectMapper()
            .apply {
                registerModule(JavaTimeModule())
                configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
                setDefaultPrettyPrinter(
                    DefaultPrettyPrinter().apply {
                        indentArraysWith(DefaultPrettyPrinter.FixedSpaceIndenter.instance)
                        indentObjectsWith(DefaultIndenter("  ", "\n"))
                    }
                )
                disableDefaultTyping()
                enable(SerializationFeature.INDENT_OUTPUT)
            }
    }
}
