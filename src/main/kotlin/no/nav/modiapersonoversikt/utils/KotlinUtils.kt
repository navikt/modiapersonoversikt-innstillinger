package no.nav.modiapersonoversikt.utils

object KotlinUtils {
    fun getConfig(name: String, defaultValues: Map<String, String?> = emptyMap()): String? {
        return System.getProperty(name) ?: System.getenv(name) ?: defaultValues[name]
    }

    fun getRequiredConfig(name: String, defaultValues: Map<String, String?> = emptyMap()): String =
        requireNotNull(getConfig(name, defaultValues)) {
            "Could not find property/env for '$name'"
        }

    inline fun <T1 : Any, T2 : Any, T3 : Any, R : Any> ifNotNull(p1: T1?, p2: T2?, p3: T3?, block: (T1, T2, T3) -> R?): R? {
        return if (p1 != null && p2 != null && p3 != null) block(p1, p2, p3) else null
    }
}
