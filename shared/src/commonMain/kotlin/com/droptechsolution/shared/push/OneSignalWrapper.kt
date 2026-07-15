package com.droptechsolution.shared.push

expect object OneSignalWrapper {
    fun initialize()
    fun setExternalId(id: String)
    fun removeExternalId()
    fun sendTag(key: String, value: String)
    fun requestPermission()
}
