package no.nav.modiapersonoversikt

import com.natpryce.konfig.*

private val defaultProperties = ConfigurationMap(
        mapOf(
                "S3_URL" to "",
                "S3_REGION" to "",
                "S3_ACCESS_KEY" to "",
                "S3_SECRET_KEY" to ""
        )
)

data class Configuration(
        val s3Url: String = config()[Key("S3_URL", stringType)],
        val s3Region: String = config()[Key("S3_REGION", stringType)],
        val s3AccessKey: String = config()[Key("S3_ACCESS_KEY", stringType)],
        val s3SecretKey: String = config()[Key("S3_SECRET_KEY", stringType)]
)

private fun config() = ConfigurationProperties.systemProperties() overriding EnvironmentVariables overriding defaultProperties