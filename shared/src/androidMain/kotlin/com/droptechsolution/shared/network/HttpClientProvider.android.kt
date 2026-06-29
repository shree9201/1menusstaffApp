package com.droptechsolution.shared.network

import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.api.createClientPlugin
import java.security.cert.X509Certificate
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager



val CustomInterceptorPlugin = createClientPlugin("CustomInterceptorPlugin") {

    // Intercept the request stage
    onRequest { request, _ ->
        // Example: Add a dynamic header globally
        request.headers.append("Token", "Qk7NwXbP4mLrYgZ8HsDc2VeT9uAjFi3KoEpMn5RxCt")
        request.headers.append("Id", "2")
        println("Intercepted Request: ${request.method.value} ${request.url.buildString()}")
    }

    // Intercept the response stage
    onResponse { response ->
        println("Intercepted Response Status: ${response.status}")
    }
}

actual object HttpClientProvider {

    actual val client = HttpClient(OkHttp) {
        installJsonContentNegotiation()
        install(CustomInterceptorPlugin)

        engine {
            config {
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
