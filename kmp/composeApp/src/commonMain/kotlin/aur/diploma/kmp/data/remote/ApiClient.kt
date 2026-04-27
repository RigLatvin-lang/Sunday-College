package aur.diploma.kmp.data.remote

import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.header
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.takeFrom
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

expect val BASE_URL: String

fun createHttpClient(tokenStorage: TokenStorage): HttpClient {
    return HttpClient {
        install(Logging) {
            logger = object : Logger {
                override fun log(message: String) {
                    println("🌐 KTOR: $message")
                }
            }
            level = LogLevel.ALL
        }

        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                isLenient = true
                prettyPrint = false
            })
        }

        defaultRequest {
            url(BASE_URL)
            contentType(ContentType.Application.Json)
            val token = tokenStorage.getToken()
            if (token != null) {
                header("Authorization", "Bearer $token")
            }

            // Логируем каждый запрос
            println("📤 REQUEST: ${this.headers.toString()} ${this.url}")
            println("📍 BASE_URL: $BASE_URL")
            if (token != null) {
                println("🔑 Token: ${token.take(20)}...")
            }
        }
    }
}
