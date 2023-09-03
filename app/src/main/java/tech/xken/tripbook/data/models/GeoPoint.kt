package tech.xken.tripbook.data.models

import kotlinx.serialization.*
import kotlinx.serialization.descriptors.*
import kotlinx.serialization.encoding.*


@Serializable(with = GeoPointAsStringSerializer::class)
data class GeoPoint(val x: Double, val y: Double)

object GeoPointAsStringSerializer : KSerializer<GeoPoint> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("PointAsString", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: GeoPoint) {
        val string = "(${value.x}, ${value.y})"
        encoder.encodeString(string)
    }

    override fun deserialize(decoder: Decoder): GeoPoint {
        val string = decoder.decodeString()
        val match = Regex("""\((-?\d+(\.\d+)?),\s*(-?\d+(\.\d+)?)\)""").matchEntire(string)
        if (match != null) {
            val (x, _, y) = match.destructured
            return GeoPoint(x.toDouble(), y.toDouble())
        } else {
            throw SerializationException("Invalid point format: $string")
        }
    }
}
