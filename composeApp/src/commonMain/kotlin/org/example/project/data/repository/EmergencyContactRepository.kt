package org.example.project.data.repository

import io.ktor.client.request.*
import org.example.project.data.api.ApiClient
import org.example.project.data.api.ApiResult
import org.example.project.data.api.safeApiCall
import org.example.project.data.models.*

/**
 * Repository for emergency contacts API calls
 */
class EmergencyContactRepository {
    
    private val client = ApiClient.httpClient
    
    /**
     * Get all emergency contacts
     */
    suspend fun getContacts(): ApiResult<ApiResponse<List<EmergencyContactResponse>>> = safeApiCall {
        client.get("/emergency-contacts")
    }
    
    /**
     * Get single contact by ID
     */
    suspend fun getContact(id: String): ApiResult<ApiResponse<EmergencyContactResponse>> = safeApiCall {
        client.get("/emergency-contacts/$id")
    }
    
    /**
     * Add new emergency contact
     */
    suspend fun addContact(
        name: String,
        phoneNumber: String,
        email: String? = null,
        relationship: String? = null,
        notifyBySms: Boolean = true,
        notifyByEmail: Boolean = true,
        notifyByCall: Boolean = false,
        priority: Int = 0
    ): ApiResult<ApiResponse<EmergencyContactResponse>> = safeApiCall {
        client.post("/emergency-contacts") {
            setBody(EmergencyContactRequest(
                name, phoneNumber, email, relationship,
                notifyBySms, notifyByEmail, notifyByCall, priority
            ))
        }
    }
    
    /**
     * Update emergency contact
     */
    suspend fun updateContact(
        id: String,
        name: String,
        phoneNumber: String,
        email: String? = null,
        relationship: String? = null,
        notifyBySms: Boolean = true,
        notifyByEmail: Boolean = true,
        notifyByCall: Boolean = false,
        priority: Int = 0
    ): ApiResult<ApiResponse<EmergencyContactResponse>> = safeApiCall {
        client.put("/emergency-contacts/$id") {
            setBody(EmergencyContactRequest(
                name, phoneNumber, email, relationship,
                notifyBySms, notifyByEmail, notifyByCall, priority
            ))
        }
    }
    
    /**
     * Toggle contact active status
     */
    suspend fun toggleActive(id: String): ApiResult<ApiResponse<EmergencyContactResponse>> = safeApiCall {
        client.patch("/emergency-contacts/$id/toggle-active")
    }
    
    /**
     * Delete emergency contact
     */
    suspend fun deleteContact(id: String): ApiResult<SimpleResponse> = safeApiCall {
        client.delete("/emergency-contacts/$id")
    }
}
