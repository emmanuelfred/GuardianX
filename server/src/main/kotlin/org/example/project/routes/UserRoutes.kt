package org.example.project.routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.example.project.data.models.*
import org.example.project.data.repository.UserRepository
import org.example.project.plugins.getUserId
import org.example.project.utils.*

fun Route.userRoutes() {
    val userRepository = UserRepository()

    route("/user") {
        authenticate("auth-jwt") {
            // Get current user profile
            get("/profile") {
                val principal = call.principal<JWTPrincipal>()!!
                val userId = principal.getUserId()
                
                val user = userRepository.findById(userId)
                    ?: throw NotFoundException("User not found")
                
                call.respond(
                    HttpStatusCode.OK,
                    ApiResponse(
                        success = true,
                        message = "Profile retrieved",
                        data = user.toResponse()
                    )
                )
            }

            // Update profile
            put("/profile") {
                val principal = call.principal<JWTPrincipal>()!!
                val userId = principal.getUserId()
                val request = call.receive<UpdateProfileRequest>()
                
                val user = userRepository.findById(userId)
                    ?: throw NotFoundException("User not found")
                
                val updatedUser = user.copy(
                    fullName = request.fullName ?: user.fullName,
                    phoneNumber = request.phoneNumber ?: user.phoneNumber,
                    profileImageUrl = request.profileImageUrl ?: user.profileImageUrl
                )
                
                userRepository.updateUser(updatedUser)
                
                call.respond(
                    HttpStatusCode.OK,
                    ApiResponse(
                        success = true,
                        message = "Profile updated successfully",
                        data = updatedUser.toResponse()
                    )
                )
            }

            // Update location
            post("/location") {
                val principal = call.principal<JWTPrincipal>()!!
                val userId = principal.getUserId()
                val request = call.receive<UpdateLocationRequest>()
                
                val location = Location(
                    latitude = request.latitude,
                    longitude = request.longitude,
                    address = request.address
                )
                
                userRepository.updateLocation(userId, location)
                
                call.respond(
                    HttpStatusCode.OK,
                    SimpleResponse(success = true, message = "Location updated")
                )
            }

            // Change password
            post("/change-password") {
                val principal = call.principal<JWTPrincipal>()!!
                val userId = principal.getUserId()
                val request = call.receive<ChangePasswordRequest>()
                
                val user = userRepository.findById(userId)
                    ?: throw NotFoundException("User not found")
                
                if (!PasswordHasher.verify(request.currentPassword, user.passwordHash)) {
                    throw BadRequestException("Current password is incorrect")
                }
                
                if (!PasswordHasher.isValidPassword(request.newPassword)) {
                    throw BadRequestException("Password must be at least 8 characters with uppercase, lowercase, and number")
                }
                
                val newHash = PasswordHasher.hash(request.newPassword)
                userRepository.updatePassword(userId, newHash)
                
                call.respond(
                    HttpStatusCode.OK,
                    SimpleResponse(success = true, message = "Password changed successfully")
                )
            }

            // Update device token (for push notifications)
            post("/device-token") {
                val principal = call.principal<JWTPrincipal>()!!
                val userId = principal.getUserId()
                val request = call.receive<Map<String, String>>()
                
                val token = request["token"]
                    ?: throw BadRequestException("Device token is required")
                
                userRepository.updateDeviceToken(userId, token)
                
                call.respond(
                    HttpStatusCode.OK,
                    SimpleResponse(success = true, message = "Device token updated")
                )
            }

            // Delete account
            delete("/account") {
                val principal = call.principal<JWTPrincipal>()!!
                val userId = principal.getUserId()
                
                // In production, you might want to:
                // 1. Require password confirmation
                // 2. Soft delete instead of hard delete
                // 3. Delete related data (contacts, trips, etc.)
                
                userRepository.deleteUser(userId)
                
                call.respond(
                    HttpStatusCode.OK,
                    SimpleResponse(success = true, message = "Account deleted successfully")
                )
            }
        }
    }
}
