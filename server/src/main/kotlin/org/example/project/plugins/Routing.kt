package org.example.project.plugins

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.example.project.routes.*
import org.example.project.services.EmailService

fun Application.configureRouting() {
    // Initialize JWT config
    JwtConfig.init(this)
    
    // Initialize services
    val emailService = EmailService(this)
    
    routing {
        // Health check
        get("/") {
            call.respond(mapOf(
                "status" to "ok",
                "message" to "GuardianX API is running",
                "version" to "1.0.0"
            ))
        }
        
        get("/health") {
            call.respond(mapOf("status" to "healthy"))
        }
        
        // API routes
        route("/api/v1") {
            authRoutes(emailService)
            userRoutes()
            emergencyContactRoutes()
            policeStationRoutes()
            tripRoutes(emailService)
            sosRoutes(emailService)
            communityRoutes()
        }
    }
}
