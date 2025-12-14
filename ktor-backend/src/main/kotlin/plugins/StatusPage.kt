package com.traffic.plugins

import io.ktor.client.plugins.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*
import io.ktor.util.network.UnresolvedAddressException
import kotlinx.serialization.Serializable
import java.net.ConnectException

@Serializable
data class ErrorResponse(
    val code: String,
    val message: String? = null
)

/**
 * Centralized error responder
 */
private suspend fun ApplicationCall.respondError(
    status: HttpStatusCode,
    code: String,
    message: String? = null,
    cause: Throwable? = null
) {
    cause?.let { application.log.error("Error [$code]", it) }

    respond(
        status,
        ErrorResponse(code, message)
    )
}

fun Application.configureStatusPages() {

    install(StatusPages) {

        exception<IllegalArgumentException> { call, cause ->
            call.respondError(
                HttpStatusCode.BadRequest,
                "BAD_REQUEST",
                cause.message
            )
        }

        exception<ConnectException> { call, _ ->
            call.respondError(
                HttpStatusCode.ServiceUnavailable,
                "AI_UNAVAILABLE",
                "FastAPI backend is not running"
            )
        }

        exception<UnresolvedAddressException> { call, _ ->
            call.respondError(
                HttpStatusCode.ServiceUnavailable,
                "AI_UNREACHABLE",
                "Invalid AI endpoint or network issue"
            )
        }

        exception<HttpRequestTimeoutException> { call, _ ->
            call.respondError(
                HttpStatusCode.GatewayTimeout,
                "AI_TIMEOUT",
                "FastAPI did not respond in time"
            )
        }

        exception<ResponseException> { call, cause ->
            call.respondError(
                HttpStatusCode.BadGateway,
                "AI_ERROR",
                cause.message,
                cause
            )
        }

        exception<RuntimeException> { call, cause ->
            call.respondError(
                HttpStatusCode.InternalServerError,
                "INTERNAL_ERROR",
                cause.message,
                cause
            )
        }

        exception<Throwable> { call, cause ->
            call.respondError(
                HttpStatusCode.InternalServerError,
                "UNEXPECTED_ERROR",
                "An unexpected error occurred",
                cause
            )
        }
    }
}