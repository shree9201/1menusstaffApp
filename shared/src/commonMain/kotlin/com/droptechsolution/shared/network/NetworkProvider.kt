package com.droptechsolution.shared.network

import com.droptechsolution.shared.outletinfo.model.api.OutletApi
import com.droptechsolution.shared.outletinfo.model.api.staff.StaffAPI

/**
 * Shared entry point for service APIs. Use this from platform modules (e.g. app)
 * that should not depend on Ktor types directly.
 */
object NetworkProvider {
    private val networkClient: NetworkClient by lazy { NetworkClient() }

    val outletApi: OutletApi by lazy { OutletApi(networkClient) }
    val staffApi: StaffAPI by lazy { StaffAPI(networkClient) }
}
