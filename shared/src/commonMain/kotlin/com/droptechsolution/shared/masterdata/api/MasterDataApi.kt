package com.droptechsolution.shared.masterdata.api

import com.droptechsolution.shared.masterdata.models.MasterDataRequest
import com.droptechsolution.shared.masterdata.models.MasterDataResponse
import com.droptechsolution.shared.network.NetworkClient
import com.droptechsolution.shared.network.NetworkResult
import com.droptechsolution.shared.network.URN
import io.ktor.client.request.setBody

interface IMasterDataApi {
    suspend fun getMasterData(): NetworkResult<MasterDataResponse>
}

class MasterDataApi(
    private val networkClient: NetworkClient,
) : IMasterDataApi {

    override suspend fun getMasterData(): NetworkResult<MasterDataResponse> =
        networkClient.post("${URN.SERVER}${URN.MASTER_DATA}") {
            setBody(MasterDataRequest)
        }
}
