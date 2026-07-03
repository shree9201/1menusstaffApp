package com.droptechsolution.shared.services.services

class ServicesAPI :IServicesAPI {
    override suspend fun loadOutletServices() {
        TODO("Not yet implemented")
    }

    override suspend fun loadRoomServices() {
        TODO("Not yet implemented")
    }
}

interface IServicesAPI{
    suspend fun loadOutletServices()
    suspend fun loadRoomServices()
}