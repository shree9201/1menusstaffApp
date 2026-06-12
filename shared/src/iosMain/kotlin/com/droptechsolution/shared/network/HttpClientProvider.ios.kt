package com.droptechsolution.shared.network

import io.ktor.client.HttpClient
import io.ktor.client.engine.darwin.Darwin

actual object HttpClientProvider {
    actual val client = HttpClient(Darwin) {
        installJsonContentNegotiation()
    }
}
