package org.example.project.data.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.bson.types.ObjectId

@Serializable
data class PoliceStation(
    @SerialName("_id")
    val id: String = ObjectId().toString(),
    val name: String,
    val phoneNumber: String,
    val alternatePhoneNumber: String? = null,
    val email: String? = null,
    val address: String,
    val location: Location,
    val state: String,
    val lga: String, // Local Government Area
    val city: String? = null,
    val officerInCharge: String? = null,
    val isVerified: Boolean = false,
    val addedByUserId: String? = null, // If user contributed this
    val isActive: Boolean = true,
    val operatingHours: String? = null, // e.g., "24/7" or "8AM-6PM"
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

@Serializable
data class PoliceStationRequest(
    val name: String,
    val phoneNumber: String,
    val alternatePhoneNumber: String? = null,
    val email: String? = null,
    val address: String,
    val latitude: Double,
    val longitude: Double,
    val state: String,
    val lga: String,
    val city: String? = null,
    val officerInCharge: String? = null,
    val operatingHours: String? = null
)

@Serializable
data class PoliceStationResponse(
    val id: String,
    val name: String,
    val phoneNumber: String,
    val alternatePhoneNumber: String?,
    val email: String?,
    val address: String,
    val location: Location,
    val state: String,
    val lga: String,
    val city: String?,
    val officerInCharge: String?,
    val isVerified: Boolean,
    val distance: Double? = null, // Distance from user in km
    val operatingHours: String?
)

fun PoliceStation.toResponse(distanceKm: Double? = null) = PoliceStationResponse(
    id = id,
    name = name,
    phoneNumber = phoneNumber,
    alternatePhoneNumber = alternatePhoneNumber,
    email = email,
    address = address,
    location = location,
    state = state,
    lga = lga,
    city = city,
    officerInCharge = officerInCharge,
    isVerified = isVerified,
    distance = distanceKm,
    operatingHours = operatingHours
)

// Helper to calculate distance between two points (Haversine formula)
fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
    val earthRadius = 6371.0 // km
    val dLat = Math.toRadians(lat2 - lat1)
    val dLon = Math.toRadians(lon2 - lon1)
    val a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
            Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
            Math.sin(dLon / 2) * Math.sin(dLon / 2)
    val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))
    return earthRadius * c
}
