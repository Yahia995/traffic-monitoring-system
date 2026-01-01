package com.traffic.plugins

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.traffic.dto.response.UserResponse
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import java.util.*

object JWTConfig {
    private val secret = System.getenv("JWT_SECRET") ?: "your-secret-key-change-in-production"
    private val issuer = System.getenv("JWT_ISSUER") ?: "traffic-monitoring-system"
    private val audience = System.getenv("JWT_AUDIENCE") ?: "traffic-monitoring-api"
    val realm = System.getenv("JWT_REALM") ?: "Access to Traffic Monitoring API"
    private val validityMs = System.getenv("JWT_VALIDITY_MS")?.toLong() ?: 3600000L // 1 hour

    private val algorithm = Algorithm.HMAC256(secret)

    fun generateToken(user: UserResponse): String {
        return JWT.create()
            .withAudience(audience)
            .withIssuer(issuer)
            .withClaim("userId", user.id)
            .withClaim("email", user.email)
            .withClaim("role", user.role.toString())
            .withExpiresAt(Date(System.currentTimeMillis() + validityMs))
            .sign(algorithm)
    }

    fun getVerifier() = JWT.require(algorithm)
        .withAudience(audience)
        .withIssuer(issuer)
        .build()
}

fun Application.configureJWT() {
    val enableAuth = System.getenv("ENABLE_AUTHENTICATION")?.toBoolean() ?: false
    if (!enableAuth) return

    install(Authentication) {
        jwt("auth-jwt") {
            realm = JWTConfig.realm
            verifier(JWTConfig.getVerifier())
            validate { credential ->
                if (credential.payload.getClaim("userId").asString() != null) {
                    JWTPrincipal(credential.payload)
                } else null
            }
        }
    }
}

val ApplicationCall.userId: UUID?
    get() = principal<JWTPrincipal>()
        ?.payload
        ?.getClaim("userId")
        ?.asString()
        ?.let { UUID.fromString(it) }