package com.traffic.plugins

import com.traffic.models.ErrorResponse
import io.ktor.client.plugins.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.request.httpMethod
import io.ktor.server.request.uri
import io.ktor.server.response.*
import io.ktor.util.network.UnresolvedAddressException
import java.net.ConnectException

/**
 * Enhanced error handling with detailed logging and user-friendly messages
 * Version 1.5
 */

/**
 * Centralized error responder with correlation ID tracking
 */
private suspend fun ApplicationCall.respondError(
    status: HttpStatusCode,
    code: String,
    message: String,
    details: String? = null,
    cause: Throwable? = null
) {
    // Extract correlation ID if present
    val correlationId = request.headers["X-Correlation-ID"] ?: "unknown"

    // Log error with context
    if (cause != null) {
        application.log.error("[{}] Error [{}]: {}", correlationId, code, message, cause)
    } else {
        application.log.warn("[{}] Error [{}]: {}", correlationId, code, message)
    }

    // Respond with structured error
    respond(
        status,
        ErrorResponse(
            code = code,
            message = message,
            details = details
        )
    )
}

fun Application.configureStatusPages() {

    install(StatusPages) {

        // Client Errors (4xx)
        exception<IllegalArgumentException> { call, cause ->
            call.respondError(
                status = HttpStatusCode.BadRequest,
                code = "BAD_REQUEST",
                message = cause.message ?: "Invalid request",
                details = "Please check your request parameters and try again",
                cause = cause
            )
        }

        exception<IllegalStateException> { call, cause ->
            call.respondError(
                status = HttpStatusCode.BadRequest,
                code = "INVALID_STATE",
                message = cause.message ?: "Invalid state",
                details = "The request cannot be processed in the current state"
            )
        }

        // AI Service Connectivity Errors (503)
        exception<ConnectException> { call, cause ->
            call.respondError(
                status = HttpStatusCode.ServiceUnavailable,
                code = "AI_UNAVAILABLE",
                message = "AI service is not reachable",
                details = "The AI processing service is currently unavailable. " +
                        "Please try again in a few moments or contact support if the issue persists.",
                cause = cause
            )
        }

        exception<UnresolvedAddressException> { call, cause ->
            call.respondError(
                status = HttpStatusCode.ServiceUnavailable,
                code = "AI_UNREACHABLE",
                message = "Cannot reach AI service",
                details = "The AI service endpoint could not be resolved. " +
                        "This might be a configuration or network issue.",
                cause = cause
            )
        }

        // Timeout Errors (504)
        exception<HttpRequestTimeoutException> { call, cause ->
            call.respondError(
                status = HttpStatusCode.GatewayTimeout,
                code = "AI_TIMEOUT",
                message = "AI processing took too long",
                details = "The video analysis exceeded the maximum processing time (10 minutes). " +
                        "Try uploading a shorter video or reducing its resolution.",
                cause = cause
            )
        }

        // AI Service Errors (502)
        exception<ResponseException> { call, cause ->
            val statusCode = when (cause.response.status.value) {
                in 400..499 -> HttpStatusCode.BadRequest
                in 500..599 -> HttpStatusCode.BadGateway
                else -> HttpStatusCode.BadGateway
            }

            val errorDetails = try {
                cause.response.status.description
            } catch (e: Exception) {
                "AI service returned an error"
            }

            call.respondError(
                status = statusCode,
                code = "AI_ERROR",
                message = "AI service encountered an error",
                details = "Status: ${cause.response.status.value} - $errorDetails",
                cause = cause
            )
        }

        // Internal Server Errors (500)
        exception<RuntimeException> { call, cause ->
            call.respondError(
                status = HttpStatusCode.InternalServerError,
                code = "INTERNAL_ERROR",
                message = "An internal error occurred",
                details = cause.message ?: "An unexpected error occurred while processing your request",
                cause = cause
            )
        }

        // Catch-all for Unexpected Errors
        exception<Throwable> { call, cause ->
            call.respondError(
                status = HttpStatusCode.InternalServerError,
                code = "UNEXPECTED_ERROR",
                message = "An unexpected error occurred",
                details = "An unexpected error occurred. Our team has been notified. " +
                        "Please try again or contact support.",
                cause = cause
            )
        }

        // HTTP Status Errors
        status(HttpStatusCode.NotFound) { call, status ->
            call.respond(
                status,
                ErrorResponse(
                    code = "NOT_FOUND",
                    message = "The requested resource was not found",
                    details = "Path: ${call.request.uri}"
                )
            )
        }

        status(HttpStatusCode.MethodNotAllowed) { call, status ->
            call.respond(
                status,
                ErrorResponse(
                    code = "METHOD_NOT_ALLOWED",
                    message = "HTTP method not allowed for this endpoint",
                    details = "Method: ${call.request.httpMethod.value}, Path: ${call.request.uri}"
                )
            )
        }

        status(HttpStatusCode.PayloadTooLarge) { call, status ->
            call.respond(
                status,
                ErrorResponse(
                    code = "FILE_TOO_LARGE",
                    message = "Uploaded file is too large",
                    details = "Maximum file size is 200 MB"
                )
            )
        }
    }
}