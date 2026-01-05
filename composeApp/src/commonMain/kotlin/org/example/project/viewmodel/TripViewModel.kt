package org.example.project.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.example.project.data.api.ApiResult
import org.example.project.data.models.TripResponse
import org.example.project.data.repository.TripRepository

/**
 * ViewModel for trip monitoring functionality
 */
class TripViewModel {
    
    private val repository = TripRepository()
    private val scope = CoroutineScope(Dispatchers.Main)
    
    // UI State
    var isLoading by mutableStateOf(false)
        private set
    
    var errorMessage by mutableStateOf<String?>(null)
        private set
    
    var activeTrip by mutableStateOf<TripResponse?>(null)
        private set
    
    var tripHistory by mutableStateOf<List<TripResponse>>(emptyList())
        private set
    
    var isTripActive by mutableStateOf(false)
        private set
    
    /**
     * Start a new trip
     */
    fun startTrip(
        startLatitude: Double,
        startLongitude: Double,
        startAddress: String?,
        destLatitude: Double,
        destLongitude: Double,
        destAddress: String?,
        expectedDurationMinutes: Int,
        checkInIntervalMinutes: Int = 5,
        guardianIds: List<String> = emptyList(),
        notes: String? = null,
        onSuccess: () -> Unit = {}
    ) {
        scope.launch {
            isLoading = true
            errorMessage = null
            
            when (val result = repository.startTrip(
                startLatitude, startLongitude, startAddress,
                destLatitude, destLongitude, destAddress,
                expectedDurationMinutes, checkInIntervalMinutes,
                guardianIds, notes
            )) {
                is ApiResult.Success -> {
                    if (result.data.success) {
                        activeTrip = result.data.data
                        isTripActive = true
                        onSuccess()
                    } else {
                        errorMessage = result.data.message
                    }
                }
                is ApiResult.Error -> {
                    errorMessage = result.message
                }
                is ApiResult.Loading -> { }
            }
            
            isLoading = false
        }
    }
    
    /**
     * Check in during active trip
     */
    fun checkIn(
        latitude: Double,
        longitude: Double,
        status: String = "OK",
        onSuccess: () -> Unit = {}
    ) {
        val tripId = activeTrip?.id ?: return
        
        scope.launch {
            isLoading = true
            
            when (val result = repository.checkIn(tripId, latitude, longitude, status)) {
                is ApiResult.Success -> {
                    if (result.data.success) {
                        onSuccess()
                    } else {
                        errorMessage = result.data.message
                    }
                }
                is ApiResult.Error -> {
                    errorMessage = result.message
                }
                is ApiResult.Loading -> { }
            }
            
            isLoading = false
        }
    }
    
    /**
     * Extend trip duration
     */
    fun extendTrip(additionalMinutes: Int, onSuccess: () -> Unit = {}) {
        val tripId = activeTrip?.id ?: return
        
        scope.launch {
            isLoading = true
            
            when (val result = repository.extendTrip(tripId, additionalMinutes)) {
                is ApiResult.Success -> {
                    if (result.data.success) {
                        activeTrip = result.data.data
                        onSuccess()
                    } else {
                        errorMessage = result.data.message
                    }
                }
                is ApiResult.Error -> {
                    errorMessage = result.message
                }
                is ApiResult.Loading -> { }
            }
            
            isLoading = false
        }
    }
    
    /**
     * End trip safely
     */
    fun endTrip(onSuccess: () -> Unit = {}) {
        val tripId = activeTrip?.id ?: return
        
        scope.launch {
            isLoading = true
            
            when (val result = repository.endTrip(tripId)) {
                is ApiResult.Success -> {
                    if (result.data.success) {
                        activeTrip = null
                        isTripActive = false
                        onSuccess()
                    } else {
                        errorMessage = result.data.message
                    }
                }
                is ApiResult.Error -> {
                    errorMessage = result.message
                }
                is ApiResult.Loading -> { }
            }
            
            isLoading = false
        }
    }
    
    /**
     * Cancel trip
     */
    fun cancelTrip(onSuccess: () -> Unit = {}) {
        val tripId = activeTrip?.id ?: return
        
        scope.launch {
            isLoading = true
            
            when (val result = repository.cancelTrip(tripId)) {
                is ApiResult.Success -> {
                    if (result.data.success) {
                        activeTrip = null
                        isTripActive = false
                        onSuccess()
                    } else {
                        errorMessage = result.data.message
                    }
                }
                is ApiResult.Error -> {
                    errorMessage = result.message
                }
                is ApiResult.Loading -> { }
            }
            
            isLoading = false
        }
    }
    
    /**
     * Check for active trip on app start
     */
    fun checkActiveTrip() {
        scope.launch {
            when (val result = repository.getActiveTrip()) {
                is ApiResult.Success -> {
                    activeTrip = result.data.data
                    isTripActive = activeTrip != null
                }
                is ApiResult.Error -> { }
                is ApiResult.Loading -> { }
            }
        }
    }
    
    /**
     * Load trip history
     */
    fun loadTripHistory(page: Int = 1) {
        scope.launch {
            isLoading = true
            
            when (val result = repository.getTrips(page)) {
                is ApiResult.Success -> {
                    tripHistory = result.data.data
                }
                is ApiResult.Error -> {
                    errorMessage = result.message
                }
                is ApiResult.Loading -> { }
            }
            
            isLoading = false
        }
    }
    
    fun clearError() {
        errorMessage = null
    }
}
