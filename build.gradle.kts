import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val kotlinVersion = "1.3.70"
val ktorVersion = "1.3.1"
val prometheusVersion = "0.4.0"
val logbackVersion = "1.2.3"
val logstashVersion = "5.1"
val amazonS3Version = "1.11.534"
val konfigVersion = "1.6.10.0"

val mainClass = "no.nav.modiapersonoversikt.ApplicationKt"

plugins {
    application
    kotlin("jvm") version "1.3.70"
}

buildscript {
    dependencies {
        classpath("org.junit.platform:junit-platform-gradle-plugin:1.2.0")
    }
}

application {
    mainClassName = mainClass
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation("io.ktor:ktor-server-netty:$ktorVersion")
    implementation("io.ktor:ktor-jackson:$ktorVersion")
    implementation("io.ktor:ktor-metrics:$ktorVersion")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.9.9")
    implementation("io.prometheus:simpleclient_hotspot:$prometheusVersion")
    implementation("io.prometheus:simpleclient_common:$prometheusVersion")
    implementation("io.prometheus:simpleclient_dropwizard:$prometheusVersion")
    implementation("ch.qos.logback:logback-classic:$logbackVersion")
    implementation("net.logstash.logback:logstash-logback-encoder:$logstashVersion")
    implementation("no.nav:vault-jdbc:1.3.1")
    implementation("org.flywaydb:flyway-core:6.3.1")
    implementation("com.github.seratch:kotliquery:1.3.0")
    implementation("com.natpryce:konfig:$konfigVersion")

    testRuntimeOnly("org.jetbrains.kotlin:kotlin-reflect:$kotlinVersion")
    testImplementation("com.h2database:h2:1.4.200")
}

repositories {
    jcenter()
    mavenCentral()
    maven("https://dl.bintray.com/kotlin/ktor")
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

tasks.withType<Wrapper> {
    gradleVersion = "5.3.1"
}

tasks.named<Jar>("jar") {
    baseName = "app"

    manifest {
        attributes["Main-Class"] = mainClass
        attributes["Class-Path"] = configurations["compile"].joinToString(separator = " ") {
            it.name
        }
    }

    doLast {
        configurations["compile"].forEach {
            val file = File("$buildDir/libs/${it.name}")
            if (!file.exists())
                it.copyTo(file)
        }
    }
}

tasks.named<KotlinCompile>("compileKotlin") {
    kotlinOptions.jvmTarget = "11"
}

tasks.named<KotlinCompile>("compileTestKotlin") {
    kotlinOptions.jvmTarget = "11"
}
