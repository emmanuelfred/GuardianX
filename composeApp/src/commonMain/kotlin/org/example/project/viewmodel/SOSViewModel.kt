package org.example.project.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.example.project.data.api.ApiResult
import org.example.project.data.models.PoliceStationResponse
import org.example.project.data.models.SOSAlertResponse
import org.example.project.data.repository.PoliceStationRepository
import org.example.project.data.repository.SOSRepository

/**
 * ViewModel for SOS/Panic Mode functionality
 */
class SOSViewModel {
    
    private val sosRepository = SOSRepository()
    private val policeRepository = PoliceStationRepository()
    private val scope = CoroutineScope(Dispatchers.Main)
    
    // UI State
    var isLoading by mutableStateOf(false)
        private set
    
    var errorMessage by mutableStateOf<String?>(null)
        private set
    
    var activeAlert by mutableStateOf<SOSAlertResponse?>(null)
        private set
    
    var nearestPoliceStation by mutableStateOf<PoliceStationResponse?>(null)
        private set
    
    var sosTriggered by mutableStateOf(false)
        private set
    
    var notifiedContactsCount by mutableStateOf(0)
        private set
    
    /**
     * Trigger SOS alert
     * 
     * @param latitude Current latitude
     * @param longitude Current longitude
     * @param address Optional address
     * @param action What to do: CALL_POLICE, SMS_CONTACTS, EMAIL_CONTACTS, ALL
     * @param message Optional message to send
     * @param onSuccess Callback when SOS is triggered successfully
     */
    fun triggerSOS(
        latitude: Double,
        longitude: Double,
        address: String? = null,
        action: String = "ALL",
        message: String? = null,
        onSuccess: () -> Unit = {}
    ) {
        scope.launch {
            isLoading = true
            errorMessage = null
            
            when (val result = sosRepository.triggerSOS(latitude, longitude, address, action, message)) {
                is ApiResult.Success -> {
                    if (result.data.success) {
                        sosTriggered = true
                        // Extract data from response
                        val data = result.data.data
                        notifiedContactsCount = (data?.get("notifiedContacts") as? Number)?.toInt() ?: 0
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
     * Get nearest police station
     */
    fun findNearestPoliceStation(latitude: Double, longitude: Double) {
        scope.launch {
            when (val result = policeRepository.getNearestStation(latitude, longitude)) {
                is ApiResult.Success -> {
                    nearestPoliceStation = result.data.data
                }
                is ApiResult.Error -> {
                    // Silently fail, police station is optional
                }
                is ApiResult.Loading -> { }
            }
        }
    }
    
    /**
     * Resolve active SOS alert
     */
    fun resolveAlert(
        alertId: String,
        status: String = "RESOLVED",
        notes: String? = null,
        onSuccess: () -> Unit = {}
    ) {
        scope.launch {
            isLoading = true
            
            when (val result = sosRepository.resolveAlert(alertId, status, notes)) {
                is ApiResult.Success -> {
                    if (result.data.success) {
                        sosTriggered = false
                        activeAlert = null
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
     * Cancel SOS alert (within 30 seconds)
     */
    fun cancelAlert(alertId: String, onSuccess: () -> Unit = {}) {
        scope.launch {
            isLoading = true
            
            when (val result = sosRepository.cancelAlert(alertId)) {
                is ApiResult.Success -> {
                    if (result.data.success) {
                        sosTriggered = false
                        activeAlert = null
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
     * Check for active alert on app start
     */
    fun checkActiveAlert() {
        scope.launch {
            when (val result = sosRepository.getActiveAlert()) {
                is ApiResult.Success -> {
                    activeAlert = result.data.data
                    sosTriggered = activeAlert != null
                }
                is ApiResult.Error -> { }
                is ApiResult.Loading -> { }
            }
        }
    }
    
    /**
     * Reset state
     */
    fun reset() {
        sosTriggered = false
        activeAlert = null
        nearestPoliceStation = null
        notifiedContactsCount = 0
        errorMessage = null
    }
    
    fun clearError() {
        errorMessage = null
    }
}
