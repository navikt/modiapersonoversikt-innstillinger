package no.nav.modiapersonoversikt.model

data class UserSettings(val innstillinger: List<UserSetting>)

data class UserSetting(val navn: String, val verdi: Any)