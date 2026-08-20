package com.droptechsolution.shared.network

actual object NetworkLogger {
    actual fun e(tag: String, message: String, throwable: Throwable?) {
        println("E/$tag: $message${throwable?.let { " (${it.message})" }.orEmpty()}")
    }
}
