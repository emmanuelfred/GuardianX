package org.example.project.data.repository

import io.ktor.client.request.*
import org.example.project.data.api.ApiClient
import org.example.project.data.api.ApiResult
import org.example.project.data.api.safeApiCall
import org.example.project.data.models.*

/**
 * Repository for police station API calls
 */
class PoliceStationRepository {
    
    private val client = ApiClient.httpClient
    
    /**
     * Get all police stations (paginated)
     */
    suspend fun getStations(page: Int = 1, limit: Int = 20): ApiResult<PaginatedResponse<PoliceStationResponse>> = safeApiCall {
        client.get("/police-stations") {
            parameter("page", page)
            parameter("limit", limit)
        }
    }
    
    /**
     * Search police stations
     */
    suspend fun searchStations(query: String, page: Int = 1, limit: Int = 20): ApiResult<ApiResponse<List<PoliceStationResponse>>> = safeApiCall {
        client.get("/police-stations/search") {
            parameter("q", query)
            parameter("page", page)
            parameter("limit", limit)
        }
    }
    
    /**
     * Get nearby police stations
     * 
     * @param latitude Current latitude
     * @param longitude Current longitude
     * @param radius Search radius in km (default 50)
     * @param limit Max results
     */
    suspend fun getNearbyStations(
        latitude: Double,
        longitude: Double,
        radius: Double = 50.0,
        limit: Int = 10
    ): ApiResult<ApiResponse<List<PoliceStationResponse>>> = safeApiCall {
        client.get("/police-stations/nearby") {
            parameter("lat", latitude)
            parameter("lng", longitude)
            parameter("radius", radius)
            parameter("limit", limit)
        }
    }
    
    /**
     * Get nearest police station
     */
    suspend fun getNearestStation(
        latitude: Double,
        longitude: Double
    ): ApiResult<ApiResponse<PoliceStationResponse?>> = safeApiCall {
        client.get("/police-stations/nearest") {
            parameter("lat", latitude)
            parameter("lng", longitude)
        }
    }
    
    /**
     * Get stations by state
     */
    suspend fun getStationsByState(state: String, page: Int = 1, limit: Int = 20): ApiResult<ApiResponse<List<PoliceStationResponse>>> = safeApiCall {
        client.get("/police-stations/state/$state") {
            parameter("page", page)
            parameter("limit", limit)
        }
    }
    
    /**
     * Get single station by ID
     */
    suspend fun getStation(id: String): ApiResult<ApiResponse<PoliceStationResponse>> = safeApiCall {
        client.get("/police-stations/$id")
    }
}
