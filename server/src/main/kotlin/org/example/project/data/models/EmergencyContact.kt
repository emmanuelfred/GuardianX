package org.example.project.data.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.bson.types.ObjectId

@Serializable
data class EmergencyContact(
    @SerialName("_id")
    val id: String = ObjectId().toString(),
    val userId: String, // Owner of this contact
    val name: String,
    val phoneNumber: String,
    val email: String? = null,
    val relationship: String? = null, // e.g., "Father", "Mother", "Friend", "Spouse"
    val isActive: Boolean = true,
    val notifyBySms: Boolean = true,
    val notifyByEmail: Boolean = true,
    val notifyByCall: Boolean = false,
    val priority: Int = 0, // 0 = highest priority
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

@Serializable
data class EmergencyContactRequest(
    val name: String,
    val phoneNumber: String,
    val email: String? = null,
    val relationship: String? = null,
    val notifyBySms: Boolean = true,
    val notifyByEmail: Boolean = true,
    val notifyByCall: Boolean = false,
    val priority: Int = 0
)

@Serializable
data class EmergencyContactResponse(
    val id: String,
    val name: String,
    val phoneNumber: String,
    val email: String?,
    val relationship: String?,
    val isActive: Boolean,
    val notifyBySms: Boolean,
    val notifyByEmail: Boolean,
    val notifyByCall: Boolean,
    val priority: Int,
    val createdAt: Long
)

fun EmergencyContact.toResponse() = EmergencyContactResponse(
    id = id,
    name = name,
    phoneNumber = phoneNumber,
    email = email,
    relationship = relationship,
    isActive = isActive,
    notifyBySms = notifyBySms,
    notifyByEmail = notifyByEmail,
    notifyByCall = notifyByCall,
    priority = priority,
    createdAt = createdAt
)
