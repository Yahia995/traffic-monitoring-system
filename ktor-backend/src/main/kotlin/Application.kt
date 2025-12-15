package com.traffic

import com.traffic.client.AIClient
import com.traffic.plugins.configureCallLogging
import com.traffic.plugins.configureCors
import com.traffic.plugins.configureRouting
import com.traffic.plugins.configureSerialization
import com.traffic.plugins.configureStatusPages
import com.traffic.plugins.configureSwagger
import io.ktor.server.application.Application
import io.ktor.server.netty.EngineMain

fun main(args: Array<String>) = EngineMain.main(args)

fun Application.module() {

    val aiClient = AIClient(
        environment.config.property("ktor.ai.endpoint").getString()
    )

    configureSerialization()
    configureCors()
    configureStatusPages()
    configureCallLogging()
    configureRouting(aiClient)
    configureSwagger()
}