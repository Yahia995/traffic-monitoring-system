package com.traffic.plugins

import com.traffic.client.AIClient
import com.traffic.routes.aiRoutes
import com.traffic.routes.healthRoutes
import io.ktor.server.application.Application
import io.ktor.server.routing.routing

fun Application.configureRouting(aiCLient: AIClient) {
    routing {
        healthRoutes()
        aiRoutes(aiCLient)
    }
}