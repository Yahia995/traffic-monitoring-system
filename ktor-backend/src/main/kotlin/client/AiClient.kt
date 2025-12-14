package com.traffic.client

import com.traffic.models.AiResponse
import io.ktor.client.*
import io.ktor.client.call.body
import io.ktor.client.engine.apache.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.slf4j.LoggerFactory
import java.io.Closeable
import kotlin.system.measureTimeMillis

class AiClient(
    private val endpoint: String,
    private val timeoutMs: Long = 10 * 60 * 1000
) : Closeable {

    private val log = LoggerFactory.getLogger(AiClient::class.java)

    private val json = Json {
        ignoreUnknownKeys = true
    }

    private val client = HttpClient(Apache) {

        install(ContentNegotiation) {
            json(this@AiClient.json)
        }

        install(HttpTimeout) {
            requestTimeoutMillis = timeoutMs
            connectTimeoutMillis = 30_000
            socketTimeoutMillis = timeoutMs
        }

        expectSuccess = false
    }

    fun contentTypeFromExtension(fileName: String): ContentType =
        when (fileName.substringAfterLast('.', "").lowercase()) {
            "mp4" -> ContentType.Video.MP4
            "avi" -> ContentType("video", "x-msvideo")
            "mov" -> ContentType.Video.QuickTime
            "mkv" -> ContentType("video", "x-matroska")
            else -> throw IllegalArgumentException("Invalid extension")
        }


    suspend fun analyzeVideo(
        fileBytes: ByteArray,
        fileName: String = "video.mp4"
    ): AiResponse {

        val sizeMb = fileBytes.size / 1024.0 / 1024.0

        log.info(
            "AI ▶ Sending video '{}' ({} MB) to {}",
            fileName,
            String.format("%.2f", sizeMb),
            endpoint
        )

        val contentType = contentTypeFromExtension(fileName)

        val response = client.post {
            url(endpoint)
            setBody(
                MultiPartFormDataContent(
                    formData {
                        append(
                            "video",
                            fileBytes,
                            Headers.build {
                                append(HttpHeaders.ContentType, contentType.toString())
                                append(
                                    HttpHeaders.ContentDisposition,
                                    "form-data; filename=\"$fileName\""
                                )
                            }
                        )
                    }
                )
            )
        }

        if (!response.status.isSuccess()) {
            log.error(
                "AI ◀ Error {} from AI service for video '{}'",
                response.status,
                fileName
            )
            throw ResponseException(response, "AI service returned ${response.status}")
        }

        log.info(
            "AI ◀ Received response {} for video '{}'",
            response.status,
            fileName
        )

        return response.body()
    }

    override fun close() {
        log.info("Closing AI HTTP client")
        client.close()
    }
}