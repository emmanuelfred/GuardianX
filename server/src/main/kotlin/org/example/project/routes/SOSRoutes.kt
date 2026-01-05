package org.example.project.routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.example.project.data.models.*
import org.example.project.data.repository.*
import org.example.project.plugins.getUserId
import org.example.project.services.EmailService
import org.example.project.utils.*

fun Route.sosRoutes(emailService: EmailService) {
    val sosRepository = SOSAlertRepository()
    val userRepository = UserRepository()
    val contactRepository = EmergencyContactRepository()
    val policeRepository = PoliceStationRepository()
    val tripRepository = TripRepository()

    route("/sos") {
        authenticate("auth-jwt") {
            // Get SOS history for current user
            get {
                val principal = call.principal<JWTPrincipal>()!!
                val userId = principal.getUserId()
                val page = call.request.queryParameters["page"]?.toIntOrNull() ?: 1
                val limit = call.request.queryParameters["limit"]?.toIntOrNull() ?: 20
                
                val alerts = sosRepository.findByUserId(userId, page, limit)
                val total = sosRepository.countByUserId(userId)
                
                // Get police stations for each alert
                val alertResponses = alerts.map { alert ->
                    val policeStation = alert.nearestPoliceStationId?.let { 
                        policeRepository.findById(it) 
                    }
                    alert.toResponse(policeStation)
                }
                
                call.respond(
                    HttpStatusCode.OK,
                    PaginatedResponse(
                        success = true,
                        data = alertResponses,
                        page = page,
                        limit = limit,
                        total = total,
                        totalPages = ((total + limit - 1) / limit).toInt()
                    )
                )
            }

            // Get active SOS alert
            get("/active") {
                val principal = call.principal<JWTPrincipal>()!!
                val userId = principal.getUserId()
                
                val activeAlert = sosRepository.findActiveByUserId(userId)
                
                if (activeAlert == null) {
                    call.respond(
                        HttpStatusCode.OK,
                        ApiResponse(
                            success = true,
                            message = "No active alert",
                            data = null as SOSAlertResponse?
                        )
                    )
                    return@get
                }
                
                val policeStation = activeAlert.nearestPoliceStationId?.let {
                    policeRepository.findById(it)
                }
                
                call.respond(
                    HttpStatusCode.OK,
                    ApiResponse(
                        success = true,
                        message = "Active alert found",
                        data = activeAlert.toResponse(policeStation)
                    )
                )
            }

            // Get SOS alert by ID
            get("/{id}") {
                val principal = call.principal<JWTPrincipal>()!!
                val userId = principal.getUserId()
                val alertId = call.parameters["id"]
                    ?: throw BadRequestException("Alert ID is required")
                
                val alert = sosRepository.findByIdAndUserId(alertId, userId)
                    ?: throw NotFoundException("Alert not found")
                
                val policeStation = alert.nearestPoliceStationId?.let {
                    policeRepository.findById(it)
                }
                
                call.respond(
                    HttpStatusCode.OK,
                    ApiResponse(
                        success = true,
                        message = "Alert found",
                        data = alert.toResponse(policeStation)
                    )
                )
            }

            // Trigger SOS alert (PANIC MODE)
            post("/trigger") {
                val principal = call.principal<JWTPrincipal>()!!
                val userId = principal.getUserId()
                val request = call.receive<TriggerSOSRequest>()
                
                val user = userRepository.findById(userId)
                    ?: throw NotFoundException("User not found")
                
                // Check for existing active alert
                val existingAlert = sosRepository.findActiveByUserId(userId)
                if (existingAlert != null) {
                    throw ConflictException("You already have an active SOS alert. Resolve it first.")
                }
                
                val location = Location(
                    latitude = request.latitude,
                    longitude = request.longitude,
                    address = request.address
                )
                
                // Update user's location
                userRepository.updateLocation(userId, location)
                
                // Find nearest police station
                val nearestPolice = policeRepository.findNearest(request.latitude, request.longitude)
                
                // Get active trip if any
                val activeTrip = tripRepository.findActiveByUserId(userId)
                if (activeTrip != null) {
                    tripRepository.updateStatus(activeTrip.id, TripStatus.PANIC_ACTIVATED)
                }
                
                // Get emergency contacts
                val contacts = contactRepository.findActiveByUserId(userId)
                
                // Create SOS alert
                val alert = SOSAlert(
                    userId = userId,
                    location = location,
                    alertType = AlertType.SOS_BUTTON,
                    action = request.action,
                    tripId = activeTrip?.id,
                    nearestPoliceStationId = nearestPolice?.first?.id,
                    message = request.message
                )
                
                sosRepository.create(alert)
                
                // Notify based on action type
                val notifiedContacts = mutableListOf<NotifiedContact>()
                
                when (request.action) {
                    AlertAction.EMAIL_CONTACTS, AlertAction.ALL -> {
                        // Send email to all contacts
                        contacts.forEach { contact ->
                            if (contact.notifyByEmail && contact.email != null) {
                                val sent = emailService.sendSOSAlertEmail(
                                    to = contact.email,
                                    contactName = contact.name,
                                    userName = user.fullName,
                                    location = location,
                                    message = request.message
                                )
                                if (sent) {
                                    notifiedContacts.add(
                                        NotifiedContact(
                                            contactId = contact.id,
                                            contactName = contact.name,
                                            contactPhone = contact.phoneNumber,
                                            contactEmail = contact.email,
                                            notifiedByEmail = true
                                        )
                                    )
                                }
                            }
                        }
                    }
                    AlertAction.SMS_CONTACTS -> {
                        // SMS functionality would require integration with SMS gateway
                        // For now, fall back to email
                        contacts.forEach { contact ->
                            if (contact.notifyBySms && contact.email != null) {
                                val sent = emailService.sendSOSAlertEmail(
                                    to = contact.email,
                                    contactName = contact.name,
                                    userName = user.fullName,
                                    location = location,
                                    message = request.message
                                )
                                if (sent) {
                                    notifiedContacts.add(
                                        NotifiedContact(
                                            contactId = contact.id,
                                            contactName = contact.name,
                                            contactPhone = contact.phoneNumber,
                                            contactEmail = contact.email,
                                            notifiedByEmail = true
                                        )
                                    )
                                }
                            }
                        }
                    }
                    AlertAction.CALL_POLICE -> {
                        // Return police station info for the app to initiate call
                        // The actual call is made by the device
                    }
                }
                
                // Update alert with notified contacts
                sosRepository.updateNotifiedContacts(alert.id, notifiedContacts)
                
                // Build response
                val responseData = mapOf(
                    "alert" to alert.toResponse(nearestPolice?.first),
                    "nearestPoliceStation" to nearestPolice?.let { (station, distance) ->
                        mapOf(
                            "id" to station.id,
                            "name" to station.name,
                            "phoneNumber" to station.phoneNumber,
                            "alternatePhone" to station.alternatePhoneNumber,
                            "distance" to "%.2f km".format(distance),
                            "address" to station.address
                        )
                    },
                    "notifiedContacts" to notifiedContacts.size,
                    "action" to request.action.name
                )
                
                call.respond(
                    HttpStatusCode.Created,
                    ApiResponse(
                        success = true,
                        message = "🚨 Emergency alert activated! ${notifiedContacts.size} contacts notified.",
                        data = responseData
                    )
                )
            }

            // Resolve SOS alert (mark as resolved/false alarm)
            post("/{id}/resolve") {
                val principal = call.principal<JWTPrincipal>()!!
                val userId = principal.getUserId()
                val alertId = call.parameters["id"]
                    ?: throw BadRequestException("Alert ID is required")
                val request = call.receive<ResolveSOSRequest>()
                
                val alert = sosRepository.findByIdAndUserId(alertId, userId)
                    ?: throw NotFoundException("Alert not found")
                
                if (alert.status != AlertStatus.ACTIVE) {
                    throw BadRequestException("Alert is already resolved")
                }
                
                sosRepository.resolve(
                    alertId = alertId,
                    status = request.status,
                    resolvedBy = userId,
                    notes = request.notes
                )
                
                call.respond(
                    HttpStatusCode.OK,
                    SimpleResponse(
                        success = true,
                        message = when (request.status) {
                            AlertStatus.RESOLVED -> "Alert resolved. Glad you're safe!"
                            AlertStatus.FALSE_ALARM -> "Alert marked as false alarm"
                            AlertStatus.CANCELLED -> "Alert cancelled"
                            else -> "Alert status updated"
                        }
                    )
                )
            }

            // Quick cancel (for accidental triggers)
            post("/{id}/cancel") {
                val principal = call.principal<JWTPrincipal>()!!
                val userId = principal.getUserId()
                val alertId = call.parameters["id"]
                    ?: throw BadRequestException("Alert ID is required")
                
                val alert = sosRepository.findByIdAndUserId(alertId, userId)
                    ?: throw NotFoundException("Alert not found")
                
                if (alert.status != AlertStatus.ACTIVE) {
                    throw BadRequestException("Alert is already resolved")
                }
                
                // Only allow cancel within 30 seconds
                val timeSinceCreation = System.currentTimeMillis() - alert.createdAt
                if (timeSinceCreation > 30000) {
                    throw BadRequestException("Cannot cancel alert after 30 seconds. Please resolve it instead.")
                }
                
                sosRepository.resolve(
                    alertId = alertId,
                    status = AlertStatus.CANCELLED,
                    resolvedBy = userId,
                    notes = "Cancelled by user within 30 seconds"
                )
                
                call.respond(
                    HttpStatusCode.OK,
                    SimpleResponse(success = true, message = "Alert cancelled")
                )
            }
        }
    }
}
