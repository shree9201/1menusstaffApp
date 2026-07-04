package com.droptechsolution.shared.network

import android.util.Log
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.api.createClientPlugin
import io.ktor.client.statement.request
import java.security.cert.X509Certificate
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

private const val INTERCEPTOR_TAG = "CustomInterceptor"

val CustomInterceptorPlugin = createClientPlugin("CustomInterceptorPlugin") {

    onRequest { request, _ ->
        request.headers.append("Token", "Qk7NwXbP4mLrYgZ8HsDc2VeT9uAjFi3KoEpMn5RxCt")
        request.headers.append("Id", "2")
        Log.d(
            INTERCEPTOR_TAG,
            "Request: ${request.method.value} ${request.url.buildString()}",
        )
    }

    // Log status only — do not read the body here or ContentNegotiation cannot deserialize.
    onResponse { response ->
        Log.d(INTERCEPTOR_TAG, "Response received for: ${response.request.url}")
        Log.d(INTERCEPTOR_TAG, "Response status: ${response.status}")
    }
}

private fun okhttp3.OkHttpClient.Builder.installResponseBodyLogging() {
    addInterceptor { chain ->
        val response = chain.proceed(chain.request())
        val responseBody = response.body
        if (responseBody != null) {
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
                installResponseBodyLogging()

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
