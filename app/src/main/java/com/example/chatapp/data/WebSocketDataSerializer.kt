package com.example.chatapp.data

import com.example.chatapp.data.remote.request.MarkMessageAsReadRequest
import com.example.chatapp.data.remote.request.MessageRequest
import com.example.chatapp.data.remote.response.ActiveUserIdsResponse
import com.example.chatapp.data.remote.response.MessageResponse
import com.example.chatapp.data.remote.ws.WebSocketData
import com.example.chatapp.data.remote.ws.WebSocketDataType
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerializationException
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.serialDescriptor
import kotlinx.serialization.encoding.CompositeDecoder
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.encoding.decodeStructure
import kotlinx.serialization.encoding.encodeStructure
import kotlinx.serialization.serializer

object WebSocketDataSerializer : KSerializer<WebSocketData> {

    @Suppress("UNCHECKED_CAST")
    private val typeSerializer: Map<String, KSerializer<Any>> = mapOf(
        WebSocketDataType.MESSAGE_RESPONSE.value to serializer<MessageResponse>(),
        WebSocketDataType.MESSAGE_REQUEST.value to serializer<MessageRequest>(),
        WebSocketDataType.MARK_MESSAGE_AS_READ_REQUEST.value to serializer<MarkMessageAsReadRequest>(),
        WebSocketDataType.ACTIVE_USER_IDS_RESPONSE.value to serializer<ActiveUserIdsResponse>(),
    ).mapValues { (_, v) -> v as KSerializer<Any> }

    override val descriptor: SerialDescriptor
        get() = buildClassSerialDescriptor(serialName = "WebSocketData") {
            element("type", serialDescriptor<String>())
            element("data", buildClassSerialDescriptor("Any"))
        }

    override fun serialize(encoder: Encoder, value: WebSocketData) {
        encoder.encodeStructure(descriptor) {
            encodeStringElement(descriptor, 0, value.type)
            encodeSerializableElement(descriptor, 1, getDataSerializer(value.type), value.data)
        }
    }

    @OptIn(ExperimentalSerializationApi::class)
    override fun deserialize(decoder: Decoder): WebSocketData {
        return decoder.decodeStructure(descriptor) {
            if (decodeSequentially()) {
                val type = decodeStringElement(descriptor, 0)
                val data = decodeSerializableElement(descriptor, 1, getDataSerializer(type))
                WebSocketData(type, data)
            } else {
                require(decodeElementIndex(descriptor) == 0) { "Type field should be precede data field" }
                val type = decodeStringElement(descriptor, 0)
                val data = when (val index = decodeElementIndex(descriptor)) {
                    1 -> decodeSerializableElement(descriptor, 1, getDataSerializer(type))
                    CompositeDecoder.DECODE_DONE -> throw SerializationException("Data is missing")
                    else -> error("Unexpected index: $index")
                }
                WebSocketData(type, data)
            }
        }
    }

    private fun getDataSerializer(type: String): KSerializer<Any> {
        return typeSerializer[type] ?: throw SerializationException("Unknown type $type")
    }

}