package com.antoinecampbell.kotlin.intro

import com.antoinecampbell.kotlin.intro.plugins.configureRouting
import com.antoinecampbell.kotlin.intro.plugins.configureSerialization
import io.ktor.client.request.accept
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.install
import io.ktor.server.testing.testApplication
import io.ktor.server.websocket.WebSockets
import kotlin.test.Test
import kotlin.test.assertEquals

class ApplicationTest {

    @Test
    fun testRoot() = testApplication {
        application {
            install(WebSockets)
            configureRouting()
            configureSerialization()
        }
        client.get("/").apply {
            assertEquals(HttpStatusCode.OK, status)
            assertEquals(
                """
                {
                  "message" : "Hello World!"
                }
                """.trimIndent(),
                bodyAsText()
            )
        }
    }
}
