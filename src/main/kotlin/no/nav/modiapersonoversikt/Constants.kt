package no.nav.modiapersonoversikt

import no.nav.personoversikt.common.utils.EnvUtils

const val appName = "modiapersonoversikt-innstillinger"
const val appContextpath = "modiapersonoversikt-innstillinger"
const val OpenAM = "openam"
const val AzureAD = "azuread"

val appImage = EnvUtils.getConfig("NAIS_APP_IMAGE") ?: "N/A"
