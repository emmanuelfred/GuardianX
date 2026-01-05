package org.example.project.data.models

import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId
import java.time.Instant

@Serializable
data class User(
    @BsonId
    @SerialName("_id")
    val id: String = ObjectId().toString(),
    val email: String,
    val passwordHash: String,
    val fullName: String,
    val phoneNumber: String? = null,
    val profileImageUrl: String? = null,
    val isEmailVerified: Boolean = false,
    val verificationCode: String? = null,
    val verificationCodeExpiry: Long? = null,
    val resetPasswordCode: String? = null,
    val resetPasswordCodeExpiry: Long? = null,
    val lastLocation: Location? = null,
    val deviceToken: String? = null, // For push notifications
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

@Serializable
data class Location(
    val latitude: Double,
    val longitude: Double,
    val address: String? = null,
    val timestamp: Long = System.currentTimeMillis()
)

// Response DTOs (Data Transfer Objects)
@Serializable
data class UserResponse(
    val id: String,
    val email: String,
    val fullName: String,
    val phoneNumber: String?,
    val profileImageUrl: String?,
    val isEmailVerified: Boolean,
    val lastLocation: Location?,
    val createdAt: Long
)

fun User.toResponse() = UserResponse(
    id = id,
    email = email,
    fullName = fullName,
    phoneNumber = phoneNumber,
    profileImageUrl = profileImageUrl,
    isEmailVerified = isEmailVerified,
    lastLocation = lastLocation,
    createdAt = createdAt
)
