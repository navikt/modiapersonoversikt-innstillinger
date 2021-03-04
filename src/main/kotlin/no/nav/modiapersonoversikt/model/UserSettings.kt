package no.nav.modiapersonoversikt.model

import java.time.LocalDateTime

data class UserSettings(
    val sistLagret: LocalDateTime,
    val innstillinger: UserSettingsMap
)
typealias UserSettingsMap = Map<String, String>
