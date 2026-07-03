package com.droptechsolution.shared.network

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.ResponseException
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.serialization.SerializationException

/**
 * Centralised Ktor executor. Service classes delegate HTTP work here and receive
 * a [NetworkResult] instead of throwing or returning raw responses.
 */
class NetworkClient(
    val httpClient: HttpClient = HttpClientProvider.client,
) {

    suspend inline fun <reified T> get(
        url: String,
        crossinline block: HttpRequestBuilder.() -> Unit = {},
    ): NetworkResult<T> = execute {
        httpClient.get(url) {
            contentType(ContentType.Application.Json)
            block()
        }
    }

    suspend inline fun <reified T> post(
        url: String,
        crossinline block: HttpRequestBuilder.() -> Unit = {},
    ): NetworkResult<T> = execute {
        httpClient.post(url) {
            contentType(ContentType.Application.Json)
            block()
        }
    }

    suspend inline fun <reified T> put(
        url: String,
        crossinline block: HttpRequestBuilder.() -> Unit = {},
    ): NetworkResult<T> = execute {
        httpClient.put(url) {
            contentType(ContentType.Application.Json)
            block()
        }
    }

    suspend inline fun <reified T> delete(
        url: String,
        crossinline block: HttpRequestBuilder.() -> Unit = {},
    ): NetworkResult<T> = execute {
        httpClient.delete(url) {
            contentType(ContentType.Application.Json)
            block()
        }
    }

    suspend inline fun <reified T> execute(
        crossinline request: suspend () -> HttpResponse,
    ): NetworkResult<T> = try {
        NetworkResult.Success(request().body())
    } catch (e: ResponseException) {
        val responseBody = runCatching { e.response.bodyAsText() }.getOrNull()
        NetworkResult.Error(
            NetworkError.Http(
                statusCode = e.response.status.value,
                message = e.message ?: e.response.status.description,
                body = responseBody,
            )
        )
    } catch (e: SerializationException) {
        NetworkResult.Error(
            NetworkError.Serialization(
                message = e.message ?: "Failed to parse response",
                cause = e,
            )
        )
    } catch (e: Exception) {
        NetworkResult.Error(
            NetworkError.Network(
                message = e.message ?: "Network request failed",
                cause = e,
            )
        )
    }
}
