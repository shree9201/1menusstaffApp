package com.droptechsolution.shared.services.models

import kotlinx.serialization.Serializable

@Serializable
data class ServicesRequest(
    val outletId: String,
)
