package com.droptechsolution.shared.masterdata.models

import com.droptechsolution.shared.network.ApiStatusSerializer
import com.droptechsolution.shared.network.isApiSuccess
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.MapSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.JsonEncoder
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonArray
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.put

@Serializable
object MasterDataRequest

@Serializable
data class MasterDataKeyValue(
    val key: String,
    val value: String,
)

@Serializable
data class MasterDataResponse(
    @Serializable(with = ApiStatusSerializer::class)
    val status: String = "false",
    val value: String = "",
    @Serializable(with = FlexibleKeyValueListSerializer::class)
    val staffTypes: List<MasterDataKeyValue> = emptyList(),
    @Serializable(with = FlexibleKeyValueListSerializer::class)
    val departments: List<MasterDataKeyValue> = emptyList(),
    @Serializable(with = FlexibleStringMapSerializer::class)
    val statusList: Map<String, String> = emptyMap(),
)

data class MasterData(
    val staffTypes: List<MasterDataKeyValue>,
    val departments: List<MasterDataKeyValue>,
    val statusList: Map<String, String>,
)

fun MasterDataResponse.isSuccessful(): Boolean = status.isApiSuccess()

fun MasterDataResponse.toDomain(): MasterData =
    MasterData(
        staffTypes = staffTypes,
        departments = departments,
        statusList = statusList,
    )

object FlexibleKeyValueListSerializer : KSerializer<List<MasterDataKeyValue>> {
    private val listSerializer = ListSerializer(MasterDataKeyValue.serializer())

    override val descriptor: SerialDescriptor = listSerializer.descriptor

    override fun serialize(encoder: Encoder, value: List<MasterDataKeyValue>) {
        val jsonEncoder = encoder as? JsonEncoder
        if (jsonEncoder == null) {
            listSerializer.serialize(encoder, value)
            return
        }
        jsonEncoder.encodeJsonElement(
            buildJsonArray {
                value.forEach { item ->
                    add(
                        buildJsonObject {
                            put("key", item.key)
                            put("value", item.value)
                        },
                    )
                }
            },
        )
    }

    override fun deserialize(decoder: Decoder): List<MasterDataKeyValue> {
        val jsonDecoder = decoder as? JsonDecoder
            ?: return listSerializer.deserialize(decoder)

        return when (val element = jsonDecoder.decodeJsonElement()) {
            is JsonArray -> element.mapNotNull { item ->
                when (item) {
                    is JsonObject -> {
                        val key = item["key"]?.jsonPrimitive?.contentOrNull ?: return@mapNotNull null
                        val value = item["value"]?.jsonPrimitive?.contentOrNull ?: key
                        MasterDataKeyValue(key = key, value = value)
                    }
                    is JsonPrimitive -> {
                        val key = item.contentOrNull ?: return@mapNotNull null
                        MasterDataKeyValue(key = key, value = key)
                    }
                    else -> null
                }
            }
            is JsonObject -> element.map { (key, value) ->
                MasterDataKeyValue(
                    key = key,
                    value = (value as? JsonPrimitive)?.contentOrNull ?: key,
                )
            }
            else -> emptyList()
        }
    }
}

object FlexibleStringMapSerializer : KSerializer<Map<String, String>> {
    private val mapSerializer = MapSerializer(String.serializer(), String.serializer())

    override val descriptor: SerialDescriptor = mapSerializer.descriptor

    override fun serialize(encoder: Encoder, value: Map<String, String>) {
        mapSerializer.serialize(encoder, value)
    }

    override fun deserialize(decoder: Decoder): Map<String, String> {
        val jsonDecoder = decoder as? JsonDecoder
            ?: return mapSerializer.deserialize(decoder)

        return when (val element = jsonDecoder.decodeJsonElement()) {
            is JsonObject -> element.mapValues { (_, value) ->
                (value as? JsonPrimitive)?.contentOrNull.orEmpty()
            }
            is JsonArray -> element.mapNotNull { item ->
                val obj = item as? JsonObject ?: return@mapNotNull null
                val key = obj["key"]?.jsonPrimitive?.contentOrNull ?: return@mapNotNull null
                val value = obj["value"]?.jsonPrimitive?.contentOrNull ?: key
                key to value
            }.toMap()
            else -> emptyMap()
        }
    }
}
