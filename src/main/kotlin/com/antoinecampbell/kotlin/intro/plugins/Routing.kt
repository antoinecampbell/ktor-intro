package com.antoinecampbell.kotlin.intro.plugins

import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.application.log
import io.ktor.server.response.respond
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import io.ktor.server.websocket.WebSocketServerSession
import io.ktor.server.websocket.sendSerialized
import io.ktor.server.websocket.webSocket
import io.ktor.websocket.CloseReason
import io.ktor.websocket.Frame
import io.ktor.websocket.close
import io.ktor.websocket.readText
import java.util.Collections

fun Application.configureRouting() {
    val logger = log
    routing {
        get("/") {
            call.respond(mapOf("message" to "Hello World!"))
        }
        val sessions = Collections.synchronizedSet<WebSocketServerSession?>(LinkedHashSet())
        webSocket("/websocket") {
            sendSerialized(mapOf("message" to "Connected"))
            sessions += this
            for (frame in incoming) {
                logger.info("Frame name: ${frame.javaClass.simpleName}")
                when (frame) {
                    is Frame.Text -> {
                        val receivedText = frame.readText()
                        logger.info("Received: $receivedText")
                        if (receivedText.equals("bye", ignoreCase = true)) {
                            close(CloseReason(CloseReason.Codes.NORMAL, "Client said BYE"))
                        } else if (receivedText.equals("bye all", ignoreCase = true)) {
                            // Close all connected sessions
                            for (session in sessions) {
                                session.close(CloseReason(CloseReason.Codes.NORMAL, "Client said BYE"))
                            }
                        } else {
                            // Broadcast message to all connected sessions
                            for (session in sessions) {
                                session.sendSerialized(mapOf("message" to receivedText))
                            }
                        }
                    }
                }
            }
        }
    }
}
