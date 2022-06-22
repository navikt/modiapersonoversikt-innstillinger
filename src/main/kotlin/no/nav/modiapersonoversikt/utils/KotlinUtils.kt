package no.nav.modiapersonoversikt.utils

object KotlinUtils {
    fun getConfig(name: String, defaultValues: Map<String, String?> = emptyMap()): String? {
        return System.getProperty(name) ?: System.getenv(name) ?: defaultValues[name]
    }

    fun getRequiredConfig(name: String, defaultValues: Map<String, String?> = emptyMap()): String =
        requireNotNull(getConfig(name, defaultValues)) {
            "Could not find property/env for '$name'"
        }

    fun allNotNull(first: String?, second: String?): Pair<String, String>? {
        return first?.let { a -> second?.let { b -> a to b } }
    }
}
