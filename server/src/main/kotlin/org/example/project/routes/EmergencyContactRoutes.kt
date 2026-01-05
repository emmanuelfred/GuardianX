package org.example.project.routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.example.project.data.models.*
import org.example.project.data.repository.EmergencyContactRepository
import org.example.project.plugins.getUserId
import org.example.project.utils.*

fun Route.emergencyContactRoutes() {
    val contactRepository = EmergencyContactRepository()

    route("/emergency-contacts") {
        authenticate("auth-jwt") {
            // Get all emergency contacts for current user
            get {
                val principal = call.principal<JWTPrincipal>()!!
                val userId = principal.getUserId()
                
                val contacts = contactRepository.findByUserId(userId)
                
                call.respond(
                    HttpStatusCode.OK,
                    ApiResponse(
                        success = true,
                        message = "Contacts retrieved",
                        data = contacts.map { it.toResponse() }
                    )
                )
            }

            // Get single contact by ID
            get("/{id}") {
                val principal = call.principal<JWTPrincipal>()!!
                val userId = principal.getUserId()
                val contactId = call.parameters["id"]
                    ?: throw BadRequestException("Contact ID is required")
                
                val contact = contactRepository.findByIdAndUserId(contactId, userId)
                    ?: throw NotFoundException("Contact not found")
                
                call.respond(
                    HttpStatusCode.OK,
                    ApiResponse(
                        success = true,
                        message = "Contact retrieved",
                        data = contact.toResponse()
                    )
                )
            }

            // Create new emergency contact
            post {
                val principal = call.principal<JWTPrincipal>()!!
                val userId = principal.getUserId()
                val request = call.receive<EmergencyContactRequest>()
                
                // Limit number of contacts (optional)
                val existingCount = contactRepository.countByUserId(userId)
                if (existingCount >= 10) {
                    throw BadRequestException("Maximum of 10 emergency contacts allowed")
                }
                
                val contact = EmergencyContact(
                    userId = userId,
                    name = request.name,
                    phoneNumber = request.phoneNumber,
                    email = request.email,
                    relationship = request.relationship,
                    notifyBySms = request.notifyBySms,
                    notifyByEmail = request.notifyByEmail,
                    notifyByCall = request.notifyByCall,
                    priority = request.priority
                )
                
                contactRepository.create(contact)
                
                call.respond(
                    HttpStatusCode.Created,
                    ApiResponse(
                        success = true,
                        message = "Emergency contact added",
                        data = contact.toResponse()
                    )
                )
            }

            // Update emergency contact
            put("/{id}") {
                val principal = call.principal<JWTPrincipal>()!!
                val userId = principal.getUserId()
                val contactId = call.parameters["id"]
                    ?: throw BadRequestException("Contact ID is required")
                val request = call.receive<EmergencyContactRequest>()
                
                val existingContact = contactRepository.findByIdAndUserId(contactId, userId)
                    ?: throw NotFoundException("Contact not found")
                
                val updatedContact = existingContact.copy(
                    name = request.name,
                    phoneNumber = request.phoneNumber,
                    email = request.email,
                    relationship = request.relationship,
                    notifyBySms = request.notifyBySms,
                    notifyByEmail = request.notifyByEmail,
                    notifyByCall = request.notifyByCall,
                    priority = request.priority
                )
                
                contactRepository.update(updatedContact)
                
                call.respond(
                    HttpStatusCode.OK,
                    ApiResponse(
                        success = true,
                        message = "Emergency contact updated",
                        data = updatedContact.toResponse()
                    )
                )
            }

            // Toggle contact active status
            patch("/{id}/toggle-active") {
                val principal = call.principal<JWTPrincipal>()!!
                val userId = principal.getUserId()
                val contactId = call.parameters["id"]
                    ?: throw BadRequestException("Contact ID is required")
                
                val contact = contactRepository.findByIdAndUserId(contactId, userId)
                    ?: throw NotFoundException("Contact not found")
                
                val updatedContact = contact.copy(isActive = !contact.isActive)
                contactRepository.update(updatedContact)
                
                call.respond(
                    HttpStatusCode.OK,
                    ApiResponse(
                        success = true,
                        message = "Contact ${if (updatedContact.isActive) "activated" else "deactivated"}",
                        data = updatedContact.toResponse()
                    )
                )
            }

            // Delete emergency contact
            delete("/{id}") {
                val principal = call.principal<JWTPrincipal>()!!
                val userId = principal.getUserId()
                val contactId = call.parameters["id"]
                    ?: throw BadRequestException("Contact ID is required")
                
                val deleted = contactRepository.delete(contactId, userId)
                
                if (!deleted) {
                    throw NotFoundException("Contact not found")
                }
                
                call.respond(
                    HttpStatusCode.OK,
                    SimpleResponse(success = true, message = "Contact deleted")
                )
            }
        }
    }
}
