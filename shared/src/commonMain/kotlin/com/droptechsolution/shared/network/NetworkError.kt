package com.droptechsolution.shared.network

import com.droptechsolution.shared.ui.common.GENERIC_ERROR_MESSAGE

sealed interface NetworkError {
    val userMessage: String

    fun userFacingMessage(): String = when (this) {
        is Serialization,
        is Http,
        is Network,
        is Unknown,
        -> GENERIC_ERROR_MESSAGE
    }

    data class Http(
        val statusCode: Int,
        val message: String,
        val body: String? = null,
    ) : NetworkError {
        override val userMessage: String =
            body?.takeIf { it.isNotBlank() } ?: message.ifBlank { "Request failed ($statusCode)" }
    }

    data class Serialization(
        val message: String,
        val cause: Throwable? = null,
    ) : NetworkError {
        override val userMessage: String = message.ifBlank { "Unable to read server response" }
    }

    data class Network(
        val message: String,
        val cause: Throwable? = null,
    ) : NetworkError {
        override val userMessage: String = message.ifBlank { "Network request failed" }
    }

    data class Unknown(
        val message: String,
        val cause: Throwable? = null,
    ) : NetworkError {
        override val userMessage: String = message.ifBlank { "Something went wrong" }
    }
}
