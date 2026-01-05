package org.example.project.data.repository

import io.ktor.client.request.*
import io.ktor.client.statement.*
import org.example.project.data.api.ApiClient
import org.example.project.data.api.ApiResult
import org.example.project.data.api.safeApiCall
import org.example.project.data.models.*

/**
 * Repository for authentication related API calls
 */
class AuthRepository {

    private val client = ApiClient.httpClient

    /**
     * Register a new user
     */
    suspend fun register(
        email: String,
        password: String,
        fullName: String,
        phoneNumber: String
    ): ApiResult<AuthResponse> = safeApiCall {
        client.post("/auth/register") {
            setBody(RegisterRequest(email, password, fullName, phoneNumber))
        }
    }

    /**
     * Login user
     */
    suspend fun login(email: String, password: String): ApiResult<AuthResponse> = safeApiCall {
        client.post("/auth/login") {
            setBody(LoginRequest(email, password))
        }
    }

    /**
     * Verify email with 6-digit code
     */
    suspend fun verifyEmail(email: String, code: String): ApiResult<AuthResponse> = safeApiCall {
        client.post("/auth/verify-email") {
            setBody(VerifyEmailRequest(email, code))
        }
    }

    /**
     * Resend verification code
     */
    suspend fun resendVerification(email: String): ApiResult<SimpleResponse> = safeApiCall {
        client.post("/auth/resend-verification") {
            setBody(ResendVerificationRequest(email))
        }
    }

    /**
     * Request password reset
     */
    suspend fun forgotPassword(email: String): ApiResult<SimpleResponse> = safeApiCall {
        client.post("/auth/forgot-password") {
            setBody(ForgotPasswordRequest(email))
        }
    }

    /**
     * Verify reset code
     */
    suspend fun verifyResetCode(email: String, code: String): ApiResult<SimpleResponse> = safeApiCall {
        client.post("/auth/verify-reset-code") {
            setBody(VerifyResetCodeRequest(email, code))
        }
    }

    /**
     * Reset password with code
     */
    suspend fun resetPassword(
        email: String,
        code: String,
        newPassword: String
    ): ApiResult<SimpleResponse> = safeApiCall {
        client.post("/auth/reset-password") {
            setBody(ResetPasswordRequest(email, code, newPassword))
        }
    }

    /**
     * Save auth token after successful login/register
     */
    fun saveAuthToken(token: String) {
        ApiClient.setAuthToken(token)
    }

    /**
     * Clear auth token on logout
     */
    fun logout() {
        ApiClient.clearAuthToken()
    }

    /**
     * Check if user is logged in
     */
    fun isLoggedIn(): Boolean = ApiClient.isLoggedIn()
}
