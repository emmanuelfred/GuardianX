package org.example.project.data.models

import kotlinx.serialization.Serializable

// Registration
@Serializable
data class RegisterRequest(
    val email: String,
    val password: String,
    val fullName: String,
    val phoneNumber: String? = null
)

// Login
@Serializable
data class LoginRequest(
    val email: String,
    val password: String
)

@Serializable
data class AuthResponse(
    val success: Boolean,
    val message: String,
    val token: String? = null,
    val refreshToken: String? = null,
    val user: UserResponse? = null
)

// Email Verification
@Serializable
data class VerifyEmailRequest(
    val email: String,
    val code: String
)

@Serializable
data class ResendVerificationRequest(
    val email: String
)

// Password Reset
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

// Update Profile
@Serializable
data class UpdateProfileRequest(
    val fullName: String? = null,
    val phoneNumber: String? = null,
    val profileImageUrl: String? = null
)

// Update Location
@Serializable
data class UpdateLocationRequest(
    val latitude: Double,
    val longitude: Double,
    val address: String? = null
)

// Change Password
@Serializable
data class ChangePasswordRequest(
    val currentPassword: String,
    val newPassword: String
)

// Generic API Response
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

// Pagination
@Serializable
data class PaginatedResponse<T>(
    val success: Boolean,
    val data: List<T>,
    val page: Int,
    val limit: Int,
    val total: Long,
    val totalPages: Int
)
