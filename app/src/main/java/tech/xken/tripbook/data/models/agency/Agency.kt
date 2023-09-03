package tech.xken.tripbook.data.models.agency

import androidx.room.Entity
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AgencyProfile(
    @SerialName("agency_id") val agencyId: String,
    @SerialName("name") val name: String,
    @SerialName("physical_created_on") val physicalCreatedOn: LocalDate,
    @SerialName("motto") val motto: String?,
    @SerialName("about_us") val aboutUs: String,
    @SerialName("added_on") val added_on: Instant,
)

@Serializable
data class AgencyProfileStats(
    @SerialName("agency_id") val agencyId: String,
    @SerialName("personnel_count") val personnelCount: Int,
    @SerialName("bus_count") val busCount: Int,
    @SerialName("station_count") val stationCount: Int,
    @SerialName("support_email_count") val supportEmailCount: Int,
    @SerialName("support_phone_count") val supportPhoneCount: Int,
)


@Serializable
data class AgencyMetadata(
    @SerialName("agency_id") val agency_id: String,
    @SerialName("is_suspended") val isSuspended: Boolean?,
    @SerialName("reputation") val reputation: Float?,
    @SerialName("accident_count") val accidentCount: Int?,
)


@Serializable
data class AgencySettings(
    @SerialName("agency_id") val agencyId: String?,
    @SerialName("cost_per_km") val costPerKm: Double?,
    @SerialName("vip_cost_per_km") val vipCostPerKm: Double?,
)

@Serializable
data class AgencyEvent(
    @SerialName("id") val id: String,
    @SerialName("added_on") val addedOn: Long?,
    @SerialName("start_time") val startTime: Long?,
    @SerialName("end_time") val endTime: Long?,
    @SerialName("expected_duration") val expectedDuration: Long?,
    @SerialName("purpose") val purpose: String?,
)


@Serializable
data class StationProfileStats(
    @SerialName("station_id") val stationId: String,
    @SerialName("affiliated_town_count") val affiliatedTownCount: Int,
    @SerialName("personnel_count") val personnelCount: Int,
    @SerialName("bus_count") val busCount: Int,
    @SerialName("station_count") val stationCount: Int,
)


/**
 * Profile
 * Vehicle
 * Support Contacts
 * Stations
 * Towns
 * Jobs & Salaries
 * Locations
 * Trips
 * Personnel
 * Other
 * */