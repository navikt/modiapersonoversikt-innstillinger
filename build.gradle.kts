import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val mainClass = "no.nav.modiapersonoversikt.ApplicationKt"
val kotlinVersion = "1.7.0"
val ktorVersion = "2.0.2"
val prometheusVersion = "1.9.0"
val logbackVersion = "1.2.11"
val logstashVersion = "7.2"
val cryptoVersion = "1.2022.06.27-08.45-060993b81532"

plugins {
    kotlin("jvm") version "1.7.0"
    idea
}

repositories {
    mavenCentral()

    maven {
        name = "Confluent maven repo"
        url = uri("https://packages.confluent.io/maven/")
    }

    val githubToken = System.getenv("GITHUB_TOKEN")
    if (githubToken.isNullOrEmpty()) {
        maven {
            name = "internal-mirror-github-navikt"
            url = uri("https://repo.adeo.no/repository/github-package-registry-navikt/")
        }
    } else {
        maven {
            name = "github-package-registry-navikt"
            url = uri("https://maven.pkg.github.com/navikt/maven-release")
            credentials {
                username = "token"
                password = githubToken
            }
        }
    }
}

idea {
    module {
        isDownloadSources = true
        isDownloadJavadoc = true
    }
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation("io.ktor:ktor-server-netty:$ktorVersion")
    implementation("io.ktor:ktor-server-auth:$ktorVersion")
    implementation("io.ktor:ktor-server-auth-jwt:$ktorVersion")
    implementation("io.ktor:ktor-server-status-pages:$ktorVersion")
    implementation("io.ktor:ktor-server-cors:$ktorVersion")
    implementation("io.ktor:ktor-server-call-logging:$ktorVersion")
    implementation("io.ktor:ktor-server-forwarded-header:$ktorVersion")

    implementation("io.ktor:ktor-server-content-negotiation:$ktorVersion")
    implementation("io.ktor:ktor-serialization-jackson:$ktorVersion")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.13.3")

    implementation("io.ktor:ktor-server-metrics-micrometer:$ktorVersion")
    implementation("io.micrometer:micrometer-registry-prometheus:$prometheusVersion")

    implementation("ch.qos.logback:logback-classic:$logbackVersion")
    implementation("net.logstash.logback:logstash-logback-encoder:$logstashVersion")
    implementation("no.nav:vault-jdbc:1.3.9")
    implementation("no.nav.personoversikt:crypto:$cryptoVersion")
    implementation("org.flywaydb:flyway-core:8.5.12")
    implementation("com.github.seratch:kotliquery:1.8.0")

    testRuntimeOnly("org.jetbrains.kotlin:kotlin-reflect:$kotlinVersion")
    testImplementation("com.h2database:h2:2.1.212")
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "11"
    kotlinOptions.freeCompilerArgs = listOf("-Xcontext-receivers")
}

task<Jar>("fatJar") {
    archiveBaseName.set("app")
    duplicatesStrategy = DuplicatesStrategy.INCLUDE
    manifest {
        attributes["Main-Class"] = mainClass
    }
    from(configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) })
    with(tasks.jar.get() as CopySpec)
}

tasks {
    "build" {
        dependsOn("fatJar")
    }
}
