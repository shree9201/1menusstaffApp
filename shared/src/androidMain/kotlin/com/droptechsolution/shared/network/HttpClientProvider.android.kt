package com.droptechsolution.shared.network

import android.util.Log
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.api.createClientPlugin
import okhttp3.RequestBody.Companion.toRequestBody
import okio.Buffer
import java.security.cert.X509Certificate
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

private const val INTERCEPTOR_TAG = "CustomInterceptor"

val CustomInterceptorPlugin = createClientPlugin("CustomInterceptorPlugin") {
    onRequest { request, _ ->
        request.headers.append("Token", "Qk7NwXbP4mLrYgZ8HsDc2VeT9uAjFi3KoEpMn5RxCt")
        request.headers.append("Id", "2")
    }
}

private fun okhttp3.OkHttpClient.Builder.installNetworkLogging() {
    addInterceptor { chain ->
        val originalRequest = chain.request()
        Log.d(INTERCEPTOR_TAG, "Request: ${originalRequest.method} ${originalRequest.url}")

        val requestToSend = originalRequest.body?.let { requestBody ->
            val buffer = Buffer()
            requestBody.writeTo(buffer)
            val bodyText = buffer.readUtf8()
            Log.d(INTERCEPTOR_TAG, "Request body: $bodyText")

            val rebuiltBody = bodyText.toRequestBody(requestBody.contentType())
            originalRequest.newBuilder()
                .method(originalRequest.method, rebuiltBody)
                .build()
        } ?: run {
            Log.d(INTERCEPTOR_TAG, "Request body: (empty)")
            originalRequest
        }

        val response = chain.proceed(requestToSend)
        Log.d(INTERCEPTOR_TAG, "Response status: ${response.code}")

        response.body?.let { responseBody ->
            val bodyText = response.peekBody(Long.MAX_VALUE).string()
            Log.d(INTERCEPTOR_TAG, "Response body: $bodyText")
        }

        response
    }
}

actual object HttpClientProvider {

    actual val client = HttpClient(OkHttp) {
        installJsonContentNegotiation()
        install(CustomInterceptorPlugin)

        engine {
            config {
                installNetworkLogging()

                val trustAllCerts = arrayOf<TrustManager>(
                    object : X509TrustManager {
                        override fun checkClientTrusted(
                            chain: Array<X509Certificate>,
                            authType: String
                        ) = Unit

                        override fun checkServerTrusted(
                            chain: Array<X509Certificate>,
                            authType: String
                        ) = Unit

                        override fun getAcceptedIssuers(): Array<X509Certificate> = arrayOf()
                    }
                )

                val sslContext = SSLContext.getInstance("SSL")
                sslContext.init(null, trustAllCerts, java.security.SecureRandom())

                sslSocketFactory(
                    sslContext.socketFactory,
                    trustAllCerts[0] as X509TrustManager
                )

                hostnameVerifier { _, _ -> true }
            }
        }

    }



}
