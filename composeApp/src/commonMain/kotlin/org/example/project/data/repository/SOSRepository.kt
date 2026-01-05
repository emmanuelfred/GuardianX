package org.example.project.data.repository

import io.ktor.client.request.*
import org.example.project.data.api.ApiClient
import org.example.project.data.api.ApiResult
import org.example.project.data.api.safeApiCall
import org.example.project.data.models.*

/**
 * Repository for SOS/Panic mode API calls
 */
class SOSRepository {
    
    private val client = ApiClient.httpClient
    
    /**
     * Get SOS alert history
     */
    suspend fun getAlerts(page: Int = 1, limit: Int = 20): ApiResult<PaginatedResponse<SOSAlertResponse>> = safeApiCall {
        client.get("/sos") {
            parameter("page", page)
            parameter("limit", limit)
        }
    }
    
    /**
     * Get active SOS alert
     */
    suspend fun getActiveAlert(): ApiResult<ApiResponse<SOSAlertResponse?>> = safeApiCall {
        client.get("/sos/active")
    }
    
    /**
     * Get SOS alert by ID
     */
    suspend fun getAlert(id: String): ApiResult<ApiResponse<SOSAlertResponse>> = safeApiCall {
        client.get("/sos/$id")
    }
    
    /**
     * Trigger SOS alert (PANIC MODE)
     * 
     * @param latitude Current latitude
     * @param longitude Current longitude
     * @param address Optional address
     * @param action What action to take: CALL_POLICE, SMS_CONTACTS, EMAIL_CONTACTS, ALL
     * @param message Optional message to send to contacts
     */
    suspend fun triggerSOS(
        latitude: Double,
        longitude: Double,
        address: String? = null,
        action: String = "ALL",
        message: String? = null
    ): ApiResult<ApiResponse<Map<String, Any>>> = safeApiCall {
        client.post("/sos/trigger") {
            setBody(TriggerSOSRequest(latitude, longitude, address, action, message))
        }
    }
    
    /**
     * Resolve SOS alert
     * 
     * @param alertId Alert ID
     * @param status Resolution status: RESOLVED, FALSE_ALARM, CANCELLED
     * @param notes Optional notes about resolution
     */
    suspend fun resolveAlert(
        alertId: String,
        status: String,
        notes: String? = null
    ): ApiResult<SimpleResponse> = safeApiCall {
        client.post("/sos/$alertId/resolve") {
            setBody(ResolveSOSRequest(status, notes))
        }
    }
    
    /**
     * Cancel SOS alert (within 30 seconds)
     */
    suspend fun cancelAlert(alertId: String): ApiResult<SimpleResponse> = safeApiCall {
        client.post("/sos/$alertId/cancel")
    }
}
