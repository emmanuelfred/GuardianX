package org.example.project.data.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.bson.types.ObjectId

@Serializable
enum class AlertType {
    SOS_BUTTON,
    TRIP_OVERDUE,
    TRIP_NO_RESPONSE,
    MANUAL
}

@Serializable
enum class AlertStatus {
    ACTIVE,
    RESOLVED,
    FALSE_ALARM,
    CANCELLED
}

@Serializable
enum class AlertAction {
    CALL_POLICE,
    SMS_CONTACTS,
    EMAIL_CONTACTS,
    ALL
}

@Serializable
data class SOSAlert(
    @SerialName("_id")
    val id: String = ObjectId().toString(),
    val userId: String,
    val location: Location,
    val alertType: AlertType,
    val action: AlertAction,
    val status: AlertStatus = AlertStatus.ACTIVE,
    val tripId: String? = null, // If triggered from a trip
    val nearestPoliceStationId: String? = null,
    val notifiedContacts: List<NotifiedContact> = emptyList(),
    val message: String? = null,
    val resolvedAt: Long? = null,
    val resolvedBy: String? = null, // User ID or "SYSTEM"
    val resolutionNotes: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

@Serializable
data class NotifiedContact(
    val contactId: String,
    val contactName: String,
    val contactPhone: String?,
    val contactEmail: String?,
    val notifiedBySms: Boolean = false,
    val notifiedByEmail: Boolean = false,
    val notifiedByCall: Boolean = false,
    val notifiedAt: Long = System.currentTimeMillis()
)

@Serializable
data class TriggerSOSRequest(
    val latitude: Double,
    val longitude: Double,
    val address: String? = null,
    val action: AlertAction,
    val message: String? = null
)

@Serializable
data class ResolveSOSRequest(
    val status: AlertStatus,
    val notes: String? = null
)

@Serializable
data class SOSAlertResponse(
    val id: String,
    val userId: String,
    val location: Location,
    val alertType: AlertType,
    val action: AlertAction,
    val status: AlertStatus,
    val tripId: String?,
    val nearestPoliceStation: PoliceStationResponse?,
    val notifiedContactsCount: Int,
    val message: String?,
    val createdAt: Long
)

fun SOSAlert.toResponse(policeStation: PoliceStation? = null) = SOSAlertResponse(
    id = id,
    userId = userId,
    location = location,
    alertType = alertType,
    action = action,
    status = status,
    tripId = tripId,
    nearestPoliceStation = policeStation?.toResponse(),
    notifiedContactsCount = notifiedContacts.size,
    message = message,
    createdAt = createdAt
)
