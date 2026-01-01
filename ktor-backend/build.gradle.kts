val kotlin_version: String by project
val ktor_version: String by project
val logback_version: String by project
val postgres_version: String by project
val exposed_version: String by project
val hikari_version: String by project
val jwt_version: String by project
val bcrypt_version: String by project


plugins {
    kotlin("jvm") version "1.9.23"
    id("io.ktor.plugin") version "2.3.13"
    kotlin("plugin.serialization") version "1.9.23"
}

application {
    mainClass.set("com.traffic.ApplicationKt")
}

repositories {
    mavenCentral()
}

dependencies {

    // Server
    implementation("io.ktor:ktor-server-core-jvm:${ktor_version}")
    implementation("io.ktor:ktor-server-netty-jvm:${ktor_version}")

    // Serialization
    implementation("io.ktor:ktor-server-content-negotiation-jvm:${ktor_version}")
    implementation("io.ktor:ktor-serialization-kotlinx-json-jvm:${ktor_version}")

    // Middlewares
    implementation("io.ktor:ktor-server-call-logging-jvm:${ktor_version}")
    implementation("io.ktor:ktor-server-cors-jvm:${ktor_version}")
    implementation("io.ktor:ktor-server-status-pages:${ktor_version}")

    // Authentication & Authorization
    implementation("io.ktor:ktor-server-auth-jvm:${ktor_version}")
    implementation("io.ktor:ktor-server-auth-jwt-jvm:${ktor_version}")
    implementation("com.auth0:java-jwt:${jwt_version}")
    implementation("org.mindrot:jbcrypt:${bcrypt_version}")

    // Database
    implementation("org.postgresql:postgresql:${postgres_version}")
    implementation("com.zaxxer:HikariCP:${hikari_version}")

    // Exposed ORM
    implementation("org.jetbrains.exposed:exposed-core:${exposed_version}")
    implementation("org.jetbrains.exposed:exposed-dao:${exposed_version}")
    implementation("org.jetbrains.exposed:exposed-jdbc:${exposed_version}")
    implementation("org.jetbrains.exposed:exposed-java-time:${exposed_version}")

    // Client
    implementation("io.ktor:ktor-client-core-jvm:${ktor_version}")
    implementation("io.ktor:ktor-client-apache-jvm:${ktor_version}")
    implementation("io.ktor:ktor-client-content-negotiation-jvm:${ktor_version}")

    // Logging
    implementation("ch.qos.logback:logback-classic:${logback_version}")

    // Swagger
    implementation("io.ktor:ktor-server-swagger:${ktor_version}")
}