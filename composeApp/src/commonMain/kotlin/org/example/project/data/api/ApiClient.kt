package org.example.project.data.api

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

object ApiConfig {
    const val BASE_URL = "http://127.0.0.1:8080/api/v1"


    fun getFullUrl() = BASE_URL
}

object ApiClient {
    private var authToken: String? = null

    val httpClient = HttpClient {
        install(ContentNegotiation) {
            json(Json {
                prettyPrint = true
                isLenient = true
                ignoreUnknownKeys = true
                encodeDefaults = true
            })
        }

        install(Logging) {
            logger = object : Logger {
                override fun log(message: String) {
                    println("HTTP Client: $message")
                }
            }
            level = LogLevel.ALL
        }

        install(HttpTimeout) {
            requestTimeoutMillis = 30000
            connectTimeoutMillis = 30000
            socketTimeoutMillis = 30000
        }

        defaultRequest {
            url(ApiConfig.getFullUrl())
            contentType(ContentType.Application.Json)
            authToken?.let {
                header(HttpHeaders.Authorization, "Bearer $it")
            }
        }
    }

    fun setAuthToken(token: String?) {
        authToken = token
        println("🔑 Auth token ${if (token != null) "set" else "cleared"}")
    }

    fun getAuthToken(): String? = authToken

    fun clearAuthToken() {
        authToken = null
        println("🔑 Auth token cleared")
    }

    fun isLoggedIn(): Boolean = authToken != null
}

sealed class ApiResult<out T> {
    data class Success<T>(val data: T) : ApiResult<T>()
    data class Error(val message: String, val code: Int? = null) : ApiResult<Nothing>()
    object Loading : ApiResult<Nothing>()
}

suspend inline fun <reified T> safeApiCall(
    crossinline apiCall: suspend () -> HttpResponse
): ApiResult<T> {
    return try {
        println("📡 Making API call...")
        val response = apiCall()
        println("📡 Response status: ${response.status.value} ${response.status.description}")

        when {
            response.status.isSuccess() -> {
                try {
                    val body = response.bodyAsText()
                    println("📡 Response body: $body")
                    val data = response.body<T>()
                    println("✅ API call successful")
                    ApiResult.Success(data)
                } catch (e: Exception) {
                    println("❌ Failed to parse response: ${e.message}")
                    e.printStackTrace()
                    ApiResult.Error("Failed to parse response: ${e.message}")
                }
            }
            else -> {
                val errorBody = try {
                    response.bodyAsText()
                } catch (e: Exception) {
                    "Could not read error body"
                }
                println("❌ API error (${response.status.value}): $errorBody")
                ApiResult.Error(errorBody, response.status.value)
            }
        }
    } catch (e: ClientRequestException) {
        val errorMsg = try {
            e.response.bodyAsText()
        } catch (ex: Exception) {
            e.message ?: "Client request failed"
        }
        println("❌ Client request error (${e.response.status.value}): $errorMsg")
        ApiResult.Error(errorMsg, e.response.status.value)
    } catch (e: ServerResponseException) {
        val errorMsg = try {
            e.response.bodyAsText()
        } catch (ex: Exception) {
            e.message ?: "Server error"
        }
        println("❌ Server response error (${e.response.status.value}): $errorMsg")
        ApiResult.Error(errorMsg, e.response.status.value)
    } catch (e: HttpRequestTimeoutException) {
        println("❌ Request timeout: ${e.message}")
        ApiResult.Error("Request timeout - server not responding")
    } catch (e: Exception) {
        println("❌ Unknown error: ${e.message}")
        e.printStackTrace()
        ApiResult.Error(e.message ?: "Unknown error occurred")
    }
}