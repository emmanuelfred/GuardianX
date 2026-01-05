package org.example.project.routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.example.project.data.models.*
import org.example.project.data.repository.PoliceStationRepository
import org.example.project.plugins.getUserId
import org.example.project.utils.*

fun Route.policeStationRoutes() {
    val stationRepository = PoliceStationRepository()

    route("/police-stations") {
        // Public routes - no auth required for viewing
        
        // Get all police stations with pagination
        get {
            val page = call.request.queryParameters["page"]?.toIntOrNull() ?: 1
            val limit = call.request.queryParameters["limit"]?.toIntOrNull() ?: 20
            
            val stations = stationRepository.findAll(page, limit)
            val total = stationRepository.countAll()
            
            call.respond(
                HttpStatusCode.OK,
                PaginatedResponse(
                    success = true,
                    data = stations.map { it.toResponse() },
                    page = page,
                    limit = limit,
                    total = total,
                    totalPages = ((total + limit - 1) / limit).toInt()
                )
            )
        }

        // Search police stations
        get("/search") {
            val query = call.request.queryParameters["q"]
                ?: throw BadRequestException("Search query is required")
            val page = call.request.queryParameters["page"]?.toIntOrNull() ?: 1
            val limit = call.request.queryParameters["limit"]?.toIntOrNull() ?: 20
            
            val stations = stationRepository.search(query, page, limit)
            
            call.respond(
                HttpStatusCode.OK,
                ApiResponse(
                    success = true,
                    message = "Search results",
                    data = stations.map { it.toResponse() }
                )
            )
        }

        // Get nearby police stations
        get("/nearby") {
            val latitude = call.request.queryParameters["lat"]?.toDoubleOrNull()
                ?: throw BadRequestException("Latitude is required")
            val longitude = call.request.queryParameters["lng"]?.toDoubleOrNull()
                ?: throw BadRequestException("Longitude is required")
            val radius = call.request.queryParameters["radius"]?.toDoubleOrNull() ?: 50.0
            val limit = call.request.queryParameters["limit"]?.toIntOrNull() ?: 10
            
            val nearbyStations = stationRepository.findNearby(latitude, longitude, radius, limit)
            
            call.respond(
                HttpStatusCode.OK,
                ApiResponse(
                    success = true,
                    message = "Nearby stations",
                    data = nearbyStations.map { (station, distance) ->
                        station.toResponse(distance)
                    }
                )
            )
        }

        // Get nearest police station
        get("/nearest") {
            val latitude = call.request.queryParameters["lat"]?.toDoubleOrNull()
                ?: throw BadRequestException("Latitude is required")
            val longitude = call.request.queryParameters["lng"]?.toDoubleOrNull()
                ?: throw BadRequestException("Longitude is required")
            
            val nearest = stationRepository.findNearest(latitude, longitude)
            
            if (nearest == null) {
                call.respond(
                    HttpStatusCode.OK,
                    ApiResponse(
                        success = true,
                        message = "No police station found nearby",
                        data = null as PoliceStationResponse?
                    )
                )
                return@get
            }
            
            call.respond(
                HttpStatusCode.OK,
                ApiResponse(
                    success = true,
                    message = "Nearest station found",
                    data = nearest.first.toResponse(nearest.second)
                )
            )
        }

        // Get stations by state
        get("/state/{state}") {
            val state = call.parameters["state"]
                ?: throw BadRequestException("State is required")
            val page = call.request.queryParameters["page"]?.toIntOrNull() ?: 1
            val limit = call.request.queryParameters["limit"]?.toIntOrNull() ?: 20
            
            val stations = stationRepository.findByState(state, page, limit)
            
            call.respond(
                HttpStatusCode.OK,
                ApiResponse(
                    success = true,
                    message = "Stations in $state",
                    data = stations.map { it.toResponse() }
                )
            )
        }

        // Get single station by ID
        get("/{id}") {
            val stationId = call.parameters["id"]
                ?: throw BadRequestException("Station ID is required")
            
            val station = stationRepository.findById(stationId)
                ?: throw NotFoundException("Police station not found")
            
            call.respond(
                HttpStatusCode.OK,
                ApiResponse(
                    success = true,
                    message = "Station found",
                    data = station.toResponse()
                )
            )
        }

        // Protected routes - require authentication
        authenticate("auth-jwt") {
            // Add new police station (user contribution)
            post {
                val principal = call.principal<JWTPrincipal>()!!
                val userId = principal.getUserId()
                val request = call.receive<PoliceStationRequest>()
                
                val station = PoliceStation(
                    name = request.name,
                    phoneNumber = request.phoneNumber,
                    alternatePhoneNumber = request.alternatePhoneNumber,
                    email = request.email,
                    address = request.address,
                    location = Location(
                        latitude = request.latitude,
                        longitude = request.longitude
                    ),
                    state = request.state,
                    lga = request.lga,
                    city = request.city,
                    officerInCharge = request.officerInCharge,
                    operatingHours = request.operatingHours,
                    isVerified = false, // Needs admin verification
                    addedByUserId = userId
                )
                
                stationRepository.create(station)
                
                call.respond(
                    HttpStatusCode.Created,
                    ApiResponse(
                        success = true,
                        message = "Police station added. It will be visible after verification.",
                        data = station.toResponse()
                    )
                )
            }

            // Update police station (only stations added by user)
            put("/{id}") {
                val principal = call.principal<JWTPrincipal>()!!
                val userId = principal.getUserId()
                val stationId = call.parameters["id"]
                    ?: throw BadRequestException("Station ID is required")
                val request = call.receive<PoliceStationRequest>()
                
                val existingStation = stationRepository.findById(stationId)
                    ?: throw NotFoundException("Police station not found")
                
                // Only allow update if user added the station
                if (existingStation.addedByUserId != userId) {
                    throw ForbiddenException("You can only edit stations you added")
                }
                
                val updatedStation = existingStation.copy(
                    name = request.name,
                    phoneNumber = request.phoneNumber,
                    alternatePhoneNumber = request.alternatePhoneNumber,
                    email = request.email,
                    address = request.address,
                    location = Location(
                        latitude = request.latitude,
                        longitude = request.longitude
                    ),
                    state = request.state,
                    lga = request.lga,
                    city = request.city,
                    officerInCharge = request.officerInCharge,
                    operatingHours = request.operatingHours,
                    isVerified = false // Reset verification on update
                )
                
                stationRepository.update(updatedStation)
                
                call.respond(
                    HttpStatusCode.OK,
                    ApiResponse(
                        success = true,
                        message = "Police station updated",
                        data = updatedStation.toResponse()
                    )
                )
            }
        }
    }
}
