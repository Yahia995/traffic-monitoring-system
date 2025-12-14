val kotlin_version: String by project
val ktor_version: String by project
val logback_version: String by project


plugins {
    kotlin("jvm") version "1.9.23"
    id("io.ktor.plugin") version "2.3.13"
    kotlin("plugin.serialization") version "1.9.23"
}

application {
    mainClass.set("com.example.ApplicationKt")
}

repositories {
    mavenCentral()
}

dependencies {

    // Server
    implementation("io.ktor:ktor-server-core-jvm:$ktor_version")
    implementation("io.ktor:ktor-server-netty-jvm:$ktor_version")

    // Serialization
    implementation("io.ktor:ktor-server-content-negotiation-jvm:${ktor_version}")
    implementation("io.ktor:ktor-serialization-kotlinx-json-jvm:$ktor_version")

    // Middlewares
    implementation("io.ktor:ktor-server-call-logging-jvm:$ktor_version")
    implementation("io.ktor:ktor-server-cors-jvm:$ktor_version")
    implementation("io.ktor:ktor-server-status-pages:${ktor_version}")

    // Client
    implementation("io.ktor:ktor-client-core-jvm:${ktor_version}")
    implementation("io.ktor:ktor-client-apache-jvm:${ktor_version}")
    implementation("io.ktor:ktor-client-content-negotiation-jvm:${ktor_version}")

    // Logging
    implementation("ch.qos.logback:logback-classic:$logback_version")

    // Swagger
    implementation("io.ktor:ktor-server-swagger:${ktor_version}")
}