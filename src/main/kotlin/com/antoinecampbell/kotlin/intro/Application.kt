package com.antoinecampbell.kotlin.intro

import com.antoinecampbell.kotlin.intro.plugins.configureRouting
import com.antoinecampbell.kotlin.intro.plugins.configureSerialization
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.ktor.serialization.jackson.JacksonWebsocketContentConverter
import io.ktor.server.application.install
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.websocket.WebSockets
import java.time.Duration

fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0") {
        install(WebSockets) {
            pingPeriodMillis = Duration.ofSeconds(15).toMillis()
            timeoutMillis = Duration.ofSeconds(15).toMillis()
            maxFrameSize = Long.MAX_VALUE
            masking = false
            contentConverter = JacksonWebsocketContentConverter(
                jacksonObjectMapper().disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            )
        }

        configureRouting()
        configureSerialization()
    }.start(wait = true)
}
