package org.example.project.data.repository

import io.ktor.client.request.*
import org.example.project.data.api.ApiClient
import org.example.project.data.api.ApiResult
import org.example.project.data.api.safeApiCall
import org.example.project.data.models.*

/**
 * Repository for user profile related API calls
 */
class UserRepository {
    
    private val client = ApiClient.httpClient
    
    /**
     * Get current user profile
     */
    suspend fun getProfile(): ApiResult<ApiResponse<UserResponse>> = safeApiCall {
        client.get("/user/profile")
    }
    
    /**
     * Update user profile
     */
    suspend fun updateProfile(
        fullName: String? = null,
        phoneNumber: String? = null,
        profileImageUrl: String? = null
    ): ApiResult<ApiResponse<UserResponse>> = safeApiCall {
        client.put("/user/profile") {
            setBody(UpdateProfileRequest(fullName, phoneNumber, profileImageUrl))
        }
    }
    
    /**
     * Update user location
     */
    suspend fun updateLocation(
        latitude: Double,
        longitude: Double,
        address: String? = null
    ): ApiResult<SimpleResponse> = safeApiCall {
        client.post("/user/location") {
            setBody(UpdateLocationRequest(latitude, longitude, address))
        }
    }
    
    /**
     * Change password
     */
    suspend fun changePassword(
        currentPassword: String,
        newPassword: String
    ): ApiResult<SimpleResponse> = safeApiCall {
        client.post("/user/change-password") {
            setBody(ChangePasswordRequest(currentPassword, newPassword))
        }
    }
    
    /**
     * Update device token for push notifications
     */
    suspend fun updateDeviceToken(token: String): ApiResult<SimpleResponse> = safeApiCall {
        client.post("/user/device-token") {
            setBody(mapOf("token" to token))
        }
    }
    
    /**
     * Delete user account
     */
    suspend fun deleteAccount(): ApiResult<SimpleResponse> = safeApiCall {
        client.delete("/user/account")
    }
}
