package com.traffic.plugins

import io.ktor.server.application.*
import io.ktor.server.plugins.callloging.*
import io.ktor.server.request.path
import org.slf4j.event.Level

fun Application.configureCallLogging() {

    install(CallLogging) {
        level = Level.INFO

        filter { call ->
            // ignore health checks
            call.request.path() != "/api/health"
        }
    }
}
