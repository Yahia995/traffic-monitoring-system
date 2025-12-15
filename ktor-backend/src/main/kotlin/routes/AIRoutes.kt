package com.traffic.routes

import com.traffic.client.AIClient
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.aiRoutes(aiClient: AIClient) {

    post("/api/upload-video") {

        val part = call.receiveMultipart()
            .readAllParts()
            .filterIsInstance<PartData.FileItem>()
            .firstOrNull()
            ?: throw IllegalArgumentException("Invalid file in request")

        val bytes = part.streamProvider().readBytes()
        val name = part.originalFileName ?: "video.mp4"
        part.dispose()

        val sizeMb = bytes.size / 1024.0 / 1024.0

        call.application.log.info(
            "Received video '{}' ({} MB)",
            name,
            String.format("%.2f", sizeMb)
        )


        val result = aiClient.analyzeVideo(bytes, name)
        call.respond(HttpStatusCode.OK, result)
    }
}