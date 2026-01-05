package org.example.project.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.example.project.data.api.ApiResult
import org.example.project.data.models.UserResponse
import org.example.project.data.repository.AuthRepository

/**
 * ViewModel for authentication screens
 */
class AuthViewModel {
    
    private val repository = AuthRepository()
    private val scope = CoroutineScope(Dispatchers.Main)
    
    // UI State
    var isLoading by mutableStateOf(false)
        private set
    
    var errorMessage by mutableStateOf<String?>(null)
        private set
    
    var currentUser by mutableStateOf<UserResponse?>(null)
        private set
    
    var isLoggedIn by mutableStateOf(repository.isLoggedIn())
        private set
    
    /**
     * Register a new user
     */
    fun register(
        email: String,
        password: String,
        fullName: String,
        phoneNumber: String,
        onSuccess: () -> Unit
    ) {
        scope.launch {
            isLoading = true
            errorMessage = null
            
            when (val result = repository.register(email, password, fullName, phoneNumber)) {
                is ApiResult.Success -> {
                    if (result.data.success) {
                        currentUser = result.data.user
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
     * Login user
     */
    fun login(
        email: String,
        password: String,
        onSuccess: () -> Unit,
        onEmailNotVerified: () -> Unit = {}
    ) {
        scope.launch {
            isLoading = true
            errorMessage = null
            
            when (val result = repository.login(email, password)) {
                is ApiResult.Success -> {
                    if (result.data.success) {
                        result.data.token?.let { repository.saveAuthToken(it) }
                        currentUser = result.data.user
                        isLoggedIn = true
                        onSuccess()
                    } else {
                        // Check if email not verified
                        if (result.data.message.contains("verify", ignoreCase = true)) {
                            onEmailNotVerified()
                        } else {
                            errorMessage = result.data.message
                        }
                    }
                }
                is ApiResult.Error -> {
                    if (result.message.contains("EMAIL_NOT_VERIFIED")) {
                        onEmailNotVerified()
                    } else {
                        errorMessage = result.message
                    }
                }
                is ApiResult.Loading -> { }
            }
            
            isLoading = false
        }
    }
    
    /**
     * Verify email with code
     */
    fun verifyEmail(
        email: String,
        code: String,
        onSuccess: () -> Unit
    ) {
        scope.launch {
            isLoading = true
            errorMessage = null
            
            when (val result = repository.verifyEmail(email, code)) {
                is ApiResult.Success -> {
                    if (result.data.success) {
                        result.data.token?.let { repository.saveAuthToken(it) }
                        currentUser = result.data.user
                        isLoggedIn = true
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
     * Resend verification code
     */
    fun resendVerificationCode(email: String, onSuccess: () -> Unit = {}) {
        scope.launch {
            isLoading = true
            errorMessage = null
            
            when (val result = repository.resendVerification(email)) {
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
     * Request password reset
     */
    fun forgotPassword(email: String, onSuccess: () -> Unit) {
        scope.launch {
            isLoading = true
            errorMessage = null
            
            when (val result = repository.forgotPassword(email)) {
                is ApiResult.Success -> {
                    onSuccess()
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
     * Verify reset code
     */
    fun verifyResetCode(email: String, code: String, onSuccess: () -> Unit) {
        scope.launch {
            isLoading = true
            errorMessage = null
            
            when (val result = repository.verifyResetCode(email, code)) {
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
     * Reset password
     */
    fun resetPassword(
        email: String,
        code: String,
        newPassword: String,
        onSuccess: () -> Unit
    ) {
        scope.launch {
            isLoading = true
            errorMessage = null
            
            when (val result = repository.resetPassword(email, code, newPassword)) {
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
     * Logout
     */
    fun logout() {
        repository.logout()
        currentUser = null
        isLoggedIn = false
    }
    
    /**
     * Clear error message
     */
    fun clearError() {
        errorMessage = null
    }
}
