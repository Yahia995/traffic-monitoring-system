val kotlin_version: String by project
val ktor_version: String by project
val logback_version: String by project


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
    implementation("io.ktor:ktor-server-core-jvm:$ktor_version")
    implementation("io.ktor:ktor-server-netty-jvm:$ktor_version")

    // Serialization
    implementation("io.ktor:ktor-server-content-negotiation-jvm:${ktor_version}")
    implementation("io.ktor:ktor-serialization-kotlinx-json-jvm:$ktor_version")

    // Middlewares
    implementation("io.ktor:ktor-server-call-logging-jvm:$ktor_version")
    implementation("io.ktor:ktor-server-cors-jvm:$ktor_version")
    implementation("io.ktor:ktor-server-status-pages:${ktor_version}")

    // Database - Exposed ORM
    implementation("org.jetbrains.exposed:exposed-core:0.44.1")
    implementation("org.jetbrains.exposed:exposed-dao:0.44.1")
    implementation("org.jetbrains.exposed:exposed-jdbc:0.44.1")
    implementation("org.jetbrains.exposed:exposed-java-time:0.44.1")

    // PostgreSQL Driver
    implementation("org.postgresql:postgresql:42.7.1")

    // Connection Pool
    implementation("com.zaxxer:HikariCP:5.1.0")

    // Authentication (JWT)
    implementation("io.ktor:ktor-server-auth-jvm:$ktor_version")
    implementation("io.ktor:ktor-server-auth-jwt-jvm:$ktor_version")

    // Password Hashing
    implementation("org.mindrot:jbcrypt:0.4")

    // Validation
    implementation("io.konform:konform-jvm:0.4.0")

    // Client
    implementation("io.ktor:ktor-client-core-jvm:${ktor_version}")
    implementation("io.ktor:ktor-client-apache-jvm:${ktor_version}")
    implementation("io.ktor:ktor-client-content-negotiation-jvm:${ktor_version}")

    // Logging
    implementation("ch.qos.logback:logback-classic:$logback_version")

    // Swagger
    implementation("io.ktor:ktor-server-swagger:${ktor_version}")

    // Testing
    testImplementation("io.ktor:ktor-server-test-host-jvm:$ktor_version")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:$kotlin_version")
    testImplementation("com.h2database:h2:2.2.224") // In-memory DB for tests
    testImplementation("io.mockk:mockk:1.13.8")
}