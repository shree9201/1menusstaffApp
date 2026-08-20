package com.droptechsolution.shared.network

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.JsonEncoder
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.booleanOrNull
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.intOrNull

object ApiStatusSerializer : KSerializer<String> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("ApiStatus", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: String) {
        (encoder as JsonEncoder).encodeJsonElement(JsonPrimitive(value))
    }

    override fun deserialize(decoder: Decoder): String {
        val jsonDecoder = decoder as? JsonDecoder
            ?: return decoder.decodeString()

        return when (val element = jsonDecoder.decodeJsonElement()) {
            is JsonPrimitive -> when {
                element.isString -> element.content
                element.booleanOrNull != null -> element.booleanOrNull.toString()
                element.intOrNull != null -> element.intOrNull.toString()
                else -> element.contentOrNull.orEmpty()
            }
            else -> "false"
        }
    }
}

fun String.isApiSuccess(): Boolean =
    equals("true", ignoreCase = true) || this == "1"
