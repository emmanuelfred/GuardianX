package org.example.project.data.repository

import io.ktor.client.request.*
import org.example.project.data.api.ApiClient
import org.example.project.data.api.ApiResult
import org.example.project.data.api.safeApiCall
import org.example.project.data.models.*

/**
 * Repository for trip monitoring API calls
 */
class TripRepository {
    
    private val client = ApiClient.httpClient
    
    /**
     * Get trip history
     */
    suspend fun getTrips(page: Int = 1, limit: Int = 20): ApiResult<PaginatedResponse<TripResponse>> = safeApiCall {
        client.get("/trips") {
            parameter("page", page)
            parameter("limit", limit)
        }
    }
    
    /**
     * Get active trip
     */
    suspend fun getActiveTrip(): ApiResult<ApiResponse<TripResponse?>> = safeApiCall {
        client.get("/trips/active")
    }
    
    /**
     * Get trip by ID
     */
    suspend fun getTrip(id: String): ApiResult<ApiResponse<TripResponse>> = safeApiCall {
        client.get("/trips/$id")
    }
    
    /**
     * Start a new trip
     */
    suspend fun startTrip(
        startLatitude: Double,
        startLongitude: Double,
        startAddress: String?,
        destLatitude: Double,
        destLongitude: Double,
        destAddress: String?,
        expectedDurationMinutes: Int,
        checkInIntervalMinutes: Int = 5,
        guardianIds: List<String> = emptyList(),
        notes: String? = null
    ): ApiResult<ApiResponse<TripResponse>> = safeApiCall {
        client.post("/trips/start") {
            setBody(StartTripRequest(
                startLatitude, startLongitude, startAddress,
                destLatitude, destLongitude, destAddress,
                expectedDurationMinutes, checkInIntervalMinutes,
                guardianIds, notes
            ))
        }
    }
    
    /**
     * Check in during trip
     */
    suspend fun checkIn(
        tripId: String,
        latitude: Double,
        longitude: Double,
        status: String = "OK"
    ): ApiResult<SimpleResponse> = safeApiCall {
        client.post("/trips/$tripId/check-in") {
            setBody(TripCheckInRequest(latitude, longitude, status))
        }
    }
    
    /**
     * Extend trip duration
     */
    suspend fun extendTrip(tripId: String, additionalMinutes: Int): ApiResult<ApiResponse<TripResponse>> = safeApiCall {
        client.post("/trips/$tripId/extend") {
            setBody(ExtendTripRequest(additionalMinutes))
        }
    }
    
    /**
     * End trip safely
     */
    suspend fun endTrip(tripId: String): ApiResult<SimpleResponse> = safeApiCall {
        client.post("/trips/$tripId/end")
    }
    
    /**
     * Cancel trip
     */
    suspend fun cancelTrip(tripId: String): ApiResult<SimpleResponse> = safeApiCall {
        client.post("/trips/$tripId/cancel")
    }
}
