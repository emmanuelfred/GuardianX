package org.example.project.routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.example.project.data.models.*
import org.example.project.data.repository.UserRepository
import org.example.project.plugins.JwtConfig
import org.example.project.services.EmailService
import org.example.project.utils.*

fun Route.authRoutes(emailService: EmailService) {
    val userRepository = UserRepository()

    route("/auth") {
        // Register new user
        post("/register") {
            val request = call.receive<RegisterRequest>()
            
            // Validate email format
            if (!request.email.contains("@") || !request.email.contains(".")) {
                throw BadRequestException("Invalid email format")
            }
            
            // Validate password
            if (!PasswordHasher.isValidPassword(request.password)) {
                throw BadRequestException("Password must be at least 8 characters with uppercase, lowercase, and number")
            }
            
            // Check if email exists
            if (userRepository.emailExists(request.email.lowercase())) {
                throw ConflictException("Email already registered")
            }
            
            // Create user
            val verificationCode = emailService.generateVerificationCode()
            val user = User(
                email = request.email.lowercase(),
                passwordHash = PasswordHasher.hash(request.password),
                fullName = request.fullName,
                phoneNumber = request.phoneNumber,
                verificationCode = verificationCode,
                verificationCodeExpiry = System.currentTimeMillis() + (15 * 60 * 1000) // 15 minutes
            )
            
            userRepository.createUser(user)
            
            // Send verification email
            emailService.sendVerificationEmail(user.email, user.fullName, verificationCode)
            
            call.respond(
                HttpStatusCode.Created,
                AuthResponse(
                    success = true,
                    message = "Registration successful. Please check your email for verification code.",
                    user = user.toResponse()
                )
            )
        }

        // Login
        post("/login") {
            val request = call.receive<LoginRequest>()
            
            val user = userRepository.findByEmail(request.email.lowercase())
                ?: throw UnauthorizedException("Invalid email or password")
            
            if (!PasswordHasher.verify(request.password, user.passwordHash)) {
                throw UnauthorizedException("Invalid email or password")
            }
            
            if (!user.isEmailVerified) {
                throw ForbiddenException("Please verify your email before logging in", "EMAIL_NOT_VERIFIED")
            }
            
            val token = JwtConfig.generateToken(user.id, user.email)
            val refreshToken = JwtConfig.generateRefreshToken(user.id)
            
            call.respond(
                HttpStatusCode.OK,
                AuthResponse(
                    success = true,
                    message = "Login successful",
                    token = token,
                    refreshToken = refreshToken,
                    user = user.toResponse()
                )
            )
        }

        // Verify email with code
        post("/verify-email") {
            val request = call.receive<VerifyEmailRequest>()
            
            val user = userRepository.findByEmail(request.email.lowercase())
                ?: throw NotFoundException("User not found")
            
            if (user.isEmailVerified) {
                call.respond(
                    HttpStatusCode.OK,
                    SimpleResponse(success = true, message = "Email already verified")
                )
                return@post
            }
            
            if (user.verificationCode != request.code) {
                throw BadRequestException("Invalid verification code")
            }
            
            if (user.verificationCodeExpiry != null && user.verificationCodeExpiry < System.currentTimeMillis()) {
                throw BadRequestException("Verification code has expired")
            }
            
            userRepository.verifyEmail(user.id)
            
            val token = JwtConfig.generateToken(user.id, user.email)
            val refreshToken = JwtConfig.generateRefreshToken(user.id)
            
            call.respond(
                HttpStatusCode.OK,
                AuthResponse(
                    success = true,
                    message = "Email verified successfully",
                    token = token,
                    refreshToken = refreshToken,
                    user = user.copy(isEmailVerified = true).toResponse()
                )
            )
        }

        // Resend verification code
        post("/resend-verification") {
            val request = call.receive<ResendVerificationRequest>()
            
            val user = userRepository.findByEmail(request.email.lowercase())
                ?: throw NotFoundException("User not found")
            
            if (user.isEmailVerified) {
                call.respond(
                    HttpStatusCode.OK,
                    SimpleResponse(success = true, message = "Email already verified")
                )
                return@post
            }
            
            val newCode = emailService.generateVerificationCode()
            val expiry = System.currentTimeMillis() + (15 * 60 * 1000)
            
            userRepository.updateVerificationCode(user.id, newCode, expiry)
            emailService.sendVerificationEmail(user.email, user.fullName, newCode)
            
            call.respond(
                HttpStatusCode.OK,
                SimpleResponse(success = true, message = "Verification code sent to your email")
            )
        }

        // Forgot password - request reset code
        post("/forgot-password") {
            val request = call.receive<ForgotPasswordRequest>()
            
            val user = userRepository.findByEmail(request.email.lowercase())
            
            // Always respond with success for security (don't reveal if email exists)
            if (user == null) {
                call.respond(
                    HttpStatusCode.OK,
                    SimpleResponse(success = true, message = "If your email is registered, you will receive a reset code")
                )
                return@post
            }
            
            val resetCode = emailService.generateVerificationCode()
            val expiry = System.currentTimeMillis() + (15 * 60 * 1000)
            
            userRepository.updateResetPasswordCode(user.id, resetCode, expiry)
            emailService.sendPasswordResetEmail(user.email, user.fullName, resetCode)
            
            call.respond(
                HttpStatusCode.OK,
                SimpleResponse(success = true, message = "If your email is registered, you will receive a reset code")
            )
        }

        // Verify reset code (optional step to check code before resetting)
        post("/verify-reset-code") {
            val request = call.receive<VerifyResetCodeRequest>()
            
            val user = userRepository.findByEmail(request.email.lowercase())
                ?: throw NotFoundException("User not found")
            
            if (user.resetPasswordCode != request.code) {
                throw BadRequestException("Invalid reset code")
            }
            
            if (user.resetPasswordCodeExpiry != null && user.resetPasswordCodeExpiry < System.currentTimeMillis()) {
                throw BadRequestException("Reset code has expired")
            }
            
            call.respond(
                HttpStatusCode.OK,
                SimpleResponse(success = true, message = "Reset code is valid")
            )
        }

        // Reset password with code
        post("/reset-password") {
            val request = call.receive<ResetPasswordRequest>()
            
            val user = userRepository.findByEmail(request.email.lowercase())
                ?: throw NotFoundException("User not found")
            
            if (user.resetPasswordCode != request.code) {
                throw BadRequestException("Invalid reset code")
            }
            
            if (user.resetPasswordCodeExpiry != null && user.resetPasswordCodeExpiry < System.currentTimeMillis()) {
                throw BadRequestException("Reset code has expired")
            }
            
            if (!PasswordHasher.isValidPassword(request.newPassword)) {
                throw BadRequestException("Password must be at least 8 characters with uppercase, lowercase, and number")
            }
            
            val newPasswordHash = PasswordHasher.hash(request.newPassword)
            userRepository.updatePassword(user.id, newPasswordHash)
            
            call.respond(
                HttpStatusCode.OK,
                SimpleResponse(success = true, message = "Password reset successfully. You can now login with your new password.")
            )
        }
    }
}
