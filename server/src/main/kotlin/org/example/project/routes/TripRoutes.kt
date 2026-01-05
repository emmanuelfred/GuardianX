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

fun Route.tripRoutes(emailService: EmailService) {
    val tripRepository = TripRepository()
    val userRepository = UserRepository()
    val contactRepository = EmergencyContactRepository()
    val sosRepository = SOSAlertRepository()
    val policeRepository = PoliceStationRepository()

    route("/trips") {
        authenticate("auth-jwt") {
            // Get all trips for current user
            get {
                val principal = call.principal<JWTPrincipal>()!!
                val userId = principal.getUserId()
                val page = call.request.queryParameters["page"]?.toIntOrNull() ?: 1
                val limit = call.request.queryParameters["limit"]?.toIntOrNull() ?: 20
                
                val trips = tripRepository.findByUserId(userId, page, limit)
                val total = tripRepository.countByUserId(userId)
                
                call.respond(
                    HttpStatusCode.OK,
                    PaginatedResponse(
                        success = true,
                        data = trips.map { it.toResponse() },
                        page = page,
                        limit = limit,
                        total = total,
                        totalPages = ((total + limit - 1) / limit).toInt()
                    )
                )
            }

            // Get active trip
            get("/active") {
                val principal = call.principal<JWTPrincipal>()!!
                val userId = principal.getUserId()
                
                val activeTrip = tripRepository.findActiveByUserId(userId)
                
                if (activeTrip == null) {
                    call.respond(
                        HttpStatusCode.OK,
                        ApiResponse(
                            success = true,
                            message = "No active trip",
                            data = null as TripResponse?
                        )
                    )
                    return@get
                }
                
                call.respond(
                    HttpStatusCode.OK,
                    ApiResponse(
                        success = true,
                        message = "Active trip found",
                        data = activeTrip.toResponse()
                    )
                )
            }

            // Get trip by ID
            get("/{id}") {
                val principal = call.principal<JWTPrincipal>()!!
                val userId = principal.getUserId()
                val tripId = call.parameters["id"]
                    ?: throw BadRequestException("Trip ID is required")
                
                val trip = tripRepository.findByIdAndUserId(tripId, userId)
                    ?: throw NotFoundException("Trip not found")
                
                call.respond(
                    HttpStatusCode.OK,
                    ApiResponse(
                        success = true,
                        message = "Trip found",
                        data = trip.toResponse()
                    )
                )
            }

            // Start a new trip
            post("/start") {
                val principal = call.principal<JWTPrincipal>()!!
                val userId = principal.getUserId()
                val request = call.receive<StartTripRequest>()
                
                // Check if user already has an active trip
                val existingTrip = tripRepository.findActiveByUserId(userId)
                if (existingTrip != null) {
                    throw ConflictException("You already have an active trip. End it before starting a new one.")
                }
                
                val user = userRepository.findById(userId)
                    ?: throw NotFoundException("User not found")
                
                val startLocation = Location(
                    latitude = request.startLatitude,
                    longitude = request.startLongitude,
                    address = request.startAddress
                )
                
                val destination = Location(
                    latitude = request.destLatitude,
                    longitude = request.destLongitude,
                    address = request.destAddress
                )
                
                val expectedArrival = System.currentTimeMillis() + (request.expectedDurationMinutes * 60 * 1000L)
                
                val trip = Trip(
                    userId = userId,
                    startLocation = startLocation,
                    destination = destination,
                    expectedArrivalTime = expectedArrival,
                    checkInIntervalMinutes = request.checkInIntervalMinutes,
                    guardianIds = request.guardianIds,
                    lastLocation = startLocation,
                    notes = request.notes
                )
                
                tripRepository.create(trip)
                
                // Update user's last location
                userRepository.updateLocation(userId, startLocation)
                
                // Notify guardians via email
                if (request.guardianIds.isNotEmpty()) {
                    val contacts = contactRepository.findByIds(request.guardianIds)
                    contacts.forEach { contact ->
                        if (contact.notifyByEmail && contact.email != null) {
                            emailService.sendTripStartedEmail(
                                to = contact.email,
                                contactName = contact.name,
                                userName = user.fullName,
                                startLocation = startLocation,
                                destination = destination,
                                expectedArrivalTime = expectedArrival
                            )
                        }
                    }
                }
                
                call.respond(
                    HttpStatusCode.Created,
                    ApiResponse(
                        success = true,
                        message = "Trip started. Stay safe!",
                        data = trip.toResponse()
                    )
                )
            }

            // Check in during trip
            post("/{id}/check-in") {
                val principal = call.principal<JWTPrincipal>()!!
                val userId = principal.getUserId()
                val tripId = call.parameters["id"]
                    ?: throw BadRequestException("Trip ID is required")
                val request = call.receive<TripCheckInRequest>()
                
                val trip = tripRepository.findByIdAndUserId(tripId, userId)
                    ?: throw NotFoundException("Trip not found")
                
                if (trip.status != TripStatus.ACTIVE && trip.status != TripStatus.EXTENDED) {
                    throw BadRequestException("Trip is not active")
                }
                
                val location = Location(
                    latitude = request.latitude,
                    longitude = request.longitude
                )
                
                val checkIn = TripCheckIn(
                    location = location,
                    status = request.status,
                    responseTime = System.currentTimeMillis() - trip.lastCheckIn
                )
                
                when (request.status) {
                    CheckInStatus.OK -> {
                        tripRepository.recordCheckIn(tripId, checkIn, location)
                        userRepository.updateLocation(userId, location)
                        
                        call.respond(
                            HttpStatusCode.OK,
                            SimpleResponse(success = true, message = "Check-in recorded. Stay safe!")
                        )
                    }
                    CheckInStatus.ARRIVED -> {
                        tripRepository.recordCheckIn(tripId, checkIn, location)
                        tripRepository.updateStatus(tripId, TripStatus.COMPLETED)
                        userRepository.updateLocation(userId, location)
                        
                        call.respond(
                            HttpStatusCode.OK,
                            SimpleResponse(success = true, message = "Trip completed! Glad you arrived safely.")
                        )
                    }
                    CheckInStatus.PANIC -> {
                        // Trigger SOS
                        tripRepository.recordCheckIn(tripId, checkIn, location)
                        tripRepository.updateStatus(tripId, TripStatus.PANIC_ACTIVATED)
                        
                        // Create SOS alert (handled by SOS routes logic)
                        call.respond(
                            HttpStatusCode.OK,
                            SimpleResponse(success = true, message = "Emergency alert activated!")
                        )
                    }
                    else -> {
                        tripRepository.recordCheckIn(tripId, checkIn, location)
                        call.respond(
                            HttpStatusCode.OK,
                            SimpleResponse(success = true, message = "Status updated")
                        )
                    }
                }
            }

            // Extend trip duration
            post("/{id}/extend") {
                val principal = call.principal<JWTPrincipal>()!!
                val userId = principal.getUserId()
                val tripId = call.parameters["id"]
                    ?: throw BadRequestException("Trip ID is required")
                val request = call.receive<ExtendTripRequest>()
                
                val trip = tripRepository.findByIdAndUserId(tripId, userId)
                    ?: throw NotFoundException("Trip not found")
                
                if (trip.status != TripStatus.ACTIVE && trip.status != TripStatus.EXTENDED) {
                    throw BadRequestException("Trip is not active")
                }
                
                tripRepository.extendTrip(tripId, request.additionalMinutes)
                
                val updatedTrip = tripRepository.findById(tripId)!!
                
                call.respond(
                    HttpStatusCode.OK,
                    ApiResponse(
                        success = true,
                        message = "Trip extended by ${request.additionalMinutes} minutes",
                        data = updatedTrip.toResponse()
                    )
                )
            }

            // End trip safely
            post("/{id}/end") {
                val principal = call.principal<JWTPrincipal>()!!
                val userId = principal.getUserId()
                val tripId = call.parameters["id"]
                    ?: throw BadRequestException("Trip ID is required")
                
                val trip = tripRepository.findByIdAndUserId(tripId, userId)
                    ?: throw NotFoundException("Trip not found")
                
                if (trip.status != TripStatus.ACTIVE && trip.status != TripStatus.EXTENDED) {
                    throw BadRequestException("Trip is not active")
                }
                
                tripRepository.updateStatus(tripId, TripStatus.COMPLETED)
                
                call.respond(
                    HttpStatusCode.OK,
                    SimpleResponse(success = true, message = "Trip ended safely")
                )
            }

            // Cancel trip
            post("/{id}/cancel") {
                val principal = call.principal<JWTPrincipal>()!!
                val userId = principal.getUserId()
                val tripId = call.parameters["id"]
                    ?: throw BadRequestException("Trip ID is required")
                
                val trip = tripRepository.findByIdAndUserId(tripId, userId)
                    ?: throw NotFoundException("Trip not found")
                
                if (trip.status != TripStatus.ACTIVE && trip.status != TripStatus.EXTENDED) {
                    throw BadRequestException("Trip is not active")
                }
                
                tripRepository.updateStatus(tripId, TripStatus.CANCELLED)
                
                call.respond(
                    HttpStatusCode.OK,
                    SimpleResponse(success = true, message = "Trip cancelled")
                )
            }
        }
    }
}
