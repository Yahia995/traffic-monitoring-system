package com.traffic.routes

import com.traffic.auth.AuthService
import com.traffic.dto.RegisterRequest
import com.traffic.dto.request.*
import com.traffic.dto.response.AuthResponse
import com.traffic.plugins.JWTConfig
import com.traffic.plugins.userId
import com.traffic.service.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.util.*

fun Route.v2Routes() {
    val enableAuth = System.getenv("ENABLE_AUTHENTICATION")?.toBoolean() ?: false

    route("/api/v2") {

        // AUTH ROUTES (Public)
        post("/auth/register") {
            call.application.log.info("📝 V2: Register request")
            val req = call.receive<RegisterRequest>()
            val user = AuthService.register(req)
            val token = JWTConfig.generateToken(user)
            call.application.log.info("✅ V2: User registered - ${user.email}")
            call.respond(
                HttpStatusCode.Created,
                AuthResponse(
                    token = token,
                    expiresAt = System.currentTimeMillis() + 3600000,
                    user = user
                )
            )
        }

        post("/auth/login") {
            call.application.log.info("🔐 V2: Login request")
            val req = call.receive<LoginRequest>()
            val user = AuthService.login(req)
                ?: run {
                    call.application.log.warn("❌ V2: Login failed - invalid credentials")
                    return@post call.respond(
                        HttpStatusCode.Unauthorized,
                        mapOf("error" to "Invalid credentials")
                    )
                }

            val token = JWTConfig.generateToken(user)
            call.application.log.info("✅ V2: Login successful - ${user.email}")
            call.respond(
                AuthResponse(
                    token = token,
                    expiresAt = System.currentTimeMillis() + 3600000,
                    user = user
                )
            )
        }

        // PROTECTED ROUTES
        if (enableAuth) {
            authenticate("auth-jwt") {
                protectedRoutes()
            }
        } else {
            protectedRoutes()
        }
    }
}

private fun Route.protectedRoutes() {

    // VIDEO ROUTES
    get("/videos") {
        call.application.log.info("📹 V2: List videos request")
        val page = call.request.queryParameters["page"]?.toIntOrNull() ?: 0
        val size = call.request.queryParameters["size"]?.toIntOrNull() ?: 20
        val userId = call.userId

        val videos = VideoService.listVideos(userId, page, size)
        call.application.log.info("✅ V2: Returned ${videos.data.size} videos")
        call.respond(videos)
    }

    get("/videos/{id}") {
        call.application.log.info("📹 V2: Get video request")
        val id = UUID.fromString(call.parameters["id"]!!)
        val video = VideoService.getVideo(id)
            ?: run {
                call.application.log.warn("❌ V2: Video not found - $id")
                return@get call.respond(
                    HttpStatusCode.NotFound,
                    mapOf("error" to "Video not found")
                )
            }
        call.application.log.info("✅ V2: Video found - ${video.filename}")
        call.respond(video)
    }

    delete("/videos/{id}") {
        call.application.log.info("🗑️ V2: Delete video request")
        val id = UUID.fromString(call.parameters["id"]!!)
        val deleted = VideoService.deleteVideo(id)
        if (deleted) {
            call.application.log.info("✅ V2: Video deleted - $id")
            call.respond(HttpStatusCode.NoContent)
        } else {
            call.application.log.warn("❌ V2: Video not found - $id")
            call.respond(
                HttpStatusCode.NotFound,
                mapOf("error" to "Video not found")
            )
        }
    }

    // VIOLATION ROUTES
    post("/violations/filter") {
        call.application.log.info("🚨 V2: Filter violations request")
        val filter = call.receive<ViolationFilterRequest>()
        val violations = ViolationService.listViolations(filter)
        call.application.log.info("✅ V2: Returned ${violations.data.size} violations")
        call.respond(violations)
    }

    get("/violations/{id}") {
        call.application.log.info("🚨 V2: Get violation request")
        val id = UUID.fromString(call.parameters["id"]!!)
        val violation = ViolationService.getViolation(id)
            ?: run {
                call.application.log.warn("❌ V2: Violation not found - $id")
                return@get call.respond(
                    HttpStatusCode.NotFound,
                    mapOf("error" to "Violation not found")
                )
            }
        call.application.log.info("✅ V2: Violation found - ${violation.plateNumber}")
        call.respond(violation)
    }

    // STATS ROUTES
    get("/stats") {
        call.application.log.info("📊 V2: Get stats request")
        val stats = ViolationService.getStats()
        call.application.log.info("✅ V2: Stats calculated")
        call.respond(stats)
    }

    // EXPORT ROUTES
    get("/violations/export/csv") {
        call.application.log.info("📥 V2: Export CSV request")
        val filter = ViolationFilterRequest(
            page = 0,
            size = Int.MAX_VALUE
        )
        val violations = ViolationService.listViolations(filter)

        val csv = buildString {
            appendLine("ID,Video ID,Plate Number,Speed (km/h),Limit,Overspeed,Severity,Confidence,Validated,Detected At")
            violations.data.forEach { v ->
                appendLine("${v.id},${v.videoId},${v.plateNumber},${v.speedKmh},${v.speedLimitKmh},${v.overspeedKmh},${v.severity},${v.plateConfidence},${v.plateValidated},${v.detectedAt}")
            }
        }

        call.application.log.info("✅ V2: CSV exported - ${violations.data.size} violations")

        call.response.header(
            HttpHeaders.ContentDisposition,
            ContentDisposition.Attachment.withParameter(
                ContentDisposition.Parameters.FileName,
                "violations_${System.currentTimeMillis()}.csv"
            ).toString()
        )
        call.respondText(csv, ContentType.Text.CSV)
    }
}