package org.example.project.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.example.project.data.api.ApiResult
import org.example.project.data.models.EmergencyContactResponse
import org.example.project.data.repository.EmergencyContactRepository

/**
 * ViewModel for emergency contacts management
 */
class ContactsViewModel {
    
    private val repository = EmergencyContactRepository()
    private val scope = CoroutineScope(Dispatchers.Main)
    
    // UI State
    var isLoading by mutableStateOf(false)
        private set
    
    var errorMessage by mutableStateOf<String?>(null)
        private set
    
    var contacts by mutableStateOf<List<EmergencyContactResponse>>(emptyList())
        private set
    
    var selectedContact by mutableStateOf<EmergencyContactResponse?>(null)
        private set
    
    /**
     * Load all emergency contacts
     */
    fun loadContacts() {
        scope.launch {
            isLoading = true
            errorMessage = null
            
            when (val result = repository.getContacts()) {
                is ApiResult.Success -> {
                    if (result.data.success) {
                        contacts = result.data.data ?: emptyList()
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
     * Add new emergency contact
     */
    fun addContact(
        name: String,
        phoneNumber: String,
        email: String? = null,
        relationship: String? = null,
        notifyBySms: Boolean = true,
        notifyByEmail: Boolean = true,
        notifyByCall: Boolean = false,
        priority: Int = 0,
        onSuccess: () -> Unit = {}
    ) {
        scope.launch {
            isLoading = true
            errorMessage = null
            
            when (val result = repository.addContact(
                name, phoneNumber, email, relationship,
                notifyBySms, notifyByEmail, notifyByCall, priority
            )) {
                is ApiResult.Success -> {
                    if (result.data.success) {
                        loadContacts() // Refresh list
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
     * Update emergency contact
     */
    fun updateContact(
        id: String,
        name: String,
        phoneNumber: String,
        email: String? = null,
        relationship: String? = null,
        notifyBySms: Boolean = true,
        notifyByEmail: Boolean = true,
        notifyByCall: Boolean = false,
        priority: Int = 0,
        onSuccess: () -> Unit = {}
    ) {
        scope.launch {
            isLoading = true
            errorMessage = null
            
            when (val result = repository.updateContact(
                id, name, phoneNumber, email, relationship,
                notifyBySms, notifyByEmail, notifyByCall, priority
            )) {
                is ApiResult.Success -> {
                    if (result.data.success) {
                        loadContacts() // Refresh list
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
     * Toggle contact active status
     */
    fun toggleContactActive(id: String) {
        scope.launch {
            when (val result = repository.toggleActive(id)) {
                is ApiResult.Success -> {
                    if (result.data.success) {
                        loadContacts() // Refresh list
                    }
                }
                is ApiResult.Error -> {
                    errorMessage = result.message
                }
                is ApiResult.Loading -> { }
            }
        }
    }
    
    /**
     * Delete emergency contact
     */
    fun deleteContact(id: String, onSuccess: () -> Unit = {}) {
        scope.launch {
            isLoading = true
            
            when (val result = repository.deleteContact(id)) {
                is ApiResult.Success -> {
                    if (result.data.success) {
                        loadContacts() // Refresh list
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
     * Select a contact for editing
     */
    fun selectContact(contact: EmergencyContactResponse?) {
        selectedContact = contact
    }
    
    /**
     * Get active contacts (for SOS/sharing)
     */
    fun getActiveContacts(): List<EmergencyContactResponse> {
        return contacts.filter { it.isActive }
    }
    
    /**
     * Get contact IDs for trip guardians
     */
    fun getActiveContactIds(): List<String> {
        return contacts.filter { it.isActive }.map { it.id }
    }
    
    fun clearError() {
        errorMessage = null
    }
}
