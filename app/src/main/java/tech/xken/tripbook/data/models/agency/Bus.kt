package tech.xken.tripbook.data.models.agency

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@kotlinx.serialization.Serializable
data class Bus(
    @SerialName("bus_id") val busId: String,
    @SerialName("name") val name: String,
    @SerialName("capacity") val capacity: Int?,
    @SerialName("model") val model: String?,
    @SerialName("photo_url") val photoUrl: String?,
    @SerialName("bus_count") val busCount: Long?,
    @SerialName("fuel_type") val fuelType: String,
    @SerialName("seat_plan") val seatPlan: List<List<Int>>
)

@Serializable
data class AgencyBus(
    @SerialName("agency_id") val agencyId: String,
    @SerialName("bus_id") val busId: String,
    @SerialName("license_plate") val licensePlate: String,
    @SerialName("model") val model: String,
)


