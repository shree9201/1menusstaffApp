package com.droptechsolution.shared.network

expect object NetworkLogger {
    fun e(tag: String, message: String, throwable: Throwable? = null)
}

const val NETWORK_LOG_TAG = "MenusNetwork"
