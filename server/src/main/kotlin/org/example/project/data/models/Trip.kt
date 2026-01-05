package org.example.project.data.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.bson.types.ObjectId

@Serializable
enum class TripStatus {
    ACTIVE,
    COMPLETED,
    CANCELLED,
    PANIC_ACTIVATED,
    EXTENDED
}

@Serializable
data class Trip(
    @SerialName("_id")
    val id: String = ObjectId().toString(),
    val userId: String,
    val startLocation: Location,
    val destination: Location,
    val startTime: Long = System.currentTimeMillis(),
    val expectedArrivalTime: Long, // User's estimated arrival time
    val checkInIntervalMinutes: Int, // How often to check on user
    val lastCheckIn: Long = System.currentTimeMillis(),
    val lastLocation: Location? = null,
    val status: TripStatus = TripStatus.ACTIVE,
    val guardianIds: List<String> = emptyList(), // Emergency contacts to notify
    val checkInHistory: List<TripCheckIn> = emptyList(),
    val missedCheckIns: Int = 0,
    val panicActivatedAt: Long? = null,
    val completedAt: Long? = null,
    val notes: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

@Serializable
data class TripCheckIn(
    val timestamp: Long = System.currentTimeMillis(),
    val location: Location?,
    val status: CheckInStatus,
    val responseTime: Long? = null // How long user took to respond
)

@Serializable
enum class CheckInStatus {
    OK,
    NO_RESPONSE,
    PANIC,
    EXTENDED,
    ARRIVED
}

@Serializable
data class StartTripRequest(
    val startLatitude: Double,
    val startLongitude: Double,
    val startAddress: String? = null,
    val destLatitude: Double,
    val destLongitude: Double,
    val destAddress: String? = null,
    val expectedDurationMinutes: Int,
    val checkInIntervalMinutes: Int = 5,
    val guardianIds: List<String> = emptyList(),
    val notes: String? = null
)

@Serializable
data class TripCheckInRequest(
    val latitude: Double,
    val longitude: Double,
    val status: CheckInStatus
)

@Serializable
data class ExtendTripRequest(
    val additionalMinutes: Int
)

@Serializable
data class TripResponse(
    val id: String,
    val userId: String,
    val startLocation: Location,
    val destination: Location,
    val startTime: Long,
    val expectedArrivalTime: Long,
    val checkInIntervalMinutes: Int,
    val lastCheckIn: Long,
    val lastLocation: Location?,
    val status: TripStatus,
    val guardianIds: List<String>,
    val missedCheckIns: Int,
    val timeRemainingMinutes: Long,
    val isOverdue: Boolean
)

fun Trip.toResponse(): TripResponse {
    val now = System.currentTimeMillis()
    val timeRemaining = (expectedArrivalTime - now) / (1000 * 60)
    return TripResponse(
        id = id,
        userId = userId,
        startLocation = startLocation,
        destination = destination,
        startTime = startTime,
        expectedArrivalTime = expectedArrivalTime,
        checkInIntervalMinutes = checkInIntervalMinutes,
        lastCheckIn = lastCheckIn,
        lastLocation = lastLocation,
        status = status,
        guardianIds = guardianIds,
        missedCheckIns = missedCheckIns,
        timeRemainingMinutes = if (timeRemaining > 0) timeRemaining else 0,
        isOverdue = now > expectedArrivalTime && status == TripStatus.ACTIVE
    )
}
