package org.example.project.data.models

import kotlinx.serialization.Serializable

// ==================== AUTH MODELS ====================

@Serializable
data class RegisterRequest(
    val email: String,
    val password: String,
    val fullName: String,
    val phoneNumber: String
)

@Serializable
data class LoginRequest(
    val email: String,
    val password: String
)

@Serializable
data class VerifyEmailRequest(
    val email: String,
    val code: String
)

@Serializable
data class ResendVerificationRequest(
    val email: String
)

@Serializable
data class ForgotPasswordRequest(
    val email: String
)

@Serializable
data class VerifyResetCodeRequest(
    val email: String,
    val code: String
)

@Serializable
data class ResetPasswordRequest(
    val email: String,
    val code: String,
    val newPassword: String
)

@Serializable
data class AuthResponse(
    val success: Boolean,
    val message: String,
    val token: String? = null,
    val refreshToken: String? = null,
    val user: UserResponse? = null
)

@Serializable
data class UserResponse(
    val id: String,
    val email: String,
    val fullName: String,
    val phoneNumber: String,
    val profileImageUrl: String? = null,
    val isEmailVerified: Boolean = false
)

// ==================== USER MODELS ====================

@Serializable
data class UpdateProfileRequest(
    val fullName: String? = null,
    val phoneNumber: String? = null,
    val profileImageUrl: String? = null
)

@Serializable
data class UpdateLocationRequest(
    val latitude: Double,
    val longitude: Double,
    val address: String? = null
)

@Serializable
data class ChangePasswordRequest(
    val currentPassword: String,
    val newPassword: String
)

// ==================== EMERGENCY CONTACT MODELS ====================

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
    val userId: String,
    val name: String,
    val phoneNumber: String,
    val email: String? = null,
    val relationship: String? = null,
    val isActive: Boolean = true,
    val notifyBySms: Boolean = true,
    val notifyByEmail: Boolean = true,
    val notifyByCall: Boolean = false,
    val priority: Int = 0
)

// ==================== TRIP MODELS ====================

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
    val status: String = "OK" // OK, ARRIVED, PANIC, EXTENDED
)

@Serializable
data class ExtendTripRequest(
    val additionalMinutes: Int
)

@Serializable
data class TripResponse(
    val id: String,
    val userId: String,
    val startLocation: LocationData,
    val destination: LocationData,
    val startTime: Long,
    val expectedArrivalTime: Long,
    val checkInIntervalMinutes: Int,
    val status: String,
    val lastCheckIn: Long,
    val missedCheckIns: Int = 0
)

@Serializable
data class LocationData(
    val latitude: Double,
    val longitude: Double,
    val address: String? = null
)

// ==================== SOS MODELS ====================

@Serializable
data class TriggerSOSRequest(
    val latitude: Double,
    val longitude: Double,
    val address: String? = null,
    val action: String = "EMAIL_CONTACTS", // CALL_POLICE, SMS_CONTACTS, EMAIL_CONTACTS, ALL
    val message: String? = null
)

@Serializable
data class ResolveSOSRequest(
    val status: String, // RESOLVED, FALSE_ALARM, CANCELLED
    val notes: String? = null
)

@Serializable
data class SOSAlertResponse(
    val id: String,
    val userId: String,
    val location: LocationData,
    val alertType: String,
    val status: String,
    val nearestPoliceStation: PoliceStationResponse? = null,
    val createdAt: Long
)

// ==================== POLICE STATION MODELS ====================

@Serializable
data class PoliceStationResponse(
    val id: String,
    val name: String,
    val phoneNumber: String,
    val alternatePhoneNumber: String? = null,
    val address: String,
    val latitude: Double,
    val longitude: Double,
    val state: String,
    val distance: String? = null
)

// ==================== COMMUNITY MODELS ====================

@Serializable
data class CreatePostRequest(
    val content: String,
    val category: String = "GENERAL", // SAFETY_ALERT, SAFETY_TIP, LOCAL_NEWS, GENERAL
    val latitude: Double? = null,
    val longitude: Double? = null,
    val address: String? = null,
    val imageUrls: List<String> = emptyList()
)

@Serializable
data class CreateCommentRequest(
    val content: String
)

@Serializable
data class PostResponse(
    val id: String,
    val authorId: String,
    val authorName: String,
    val authorInitials: String,
    val content: String,
    val category: String,
    val location: LocationData? = null,
    val imageUrls: List<String> = emptyList(),
    val likesCount: Int = 0,
    val commentsCount: Int = 0,
    val isLikedByCurrentUser: Boolean = false,
    val createdAt: Long
)

@Serializable
data class CommentResponse(
    val id: String,
    val postId: String,
    val authorId: String,
    val authorName: String,
    val content: String,
    val likesCount: Int = 0,
    val createdAt: Long
)

// ==================== GENERIC RESPONSES ====================

@Serializable
data class ApiResponse<T>(
    val success: Boolean,
    val message: String,
    val data: T? = null
)

@Serializable
data class SimpleResponse(
    val success: Boolean,
    val message: String
)

@Serializable
data class PaginatedResponse<T>(
    val success: Boolean,
    val data: List<T>,
    val page: Int,
    val limit: Int,
    val total: Long,
    val totalPages: Int
)
