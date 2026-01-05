package org.example.project.plugins

import io.ktor.server.application.*
import org.example.project.data.database.MongoDB

fun Application.configureDatabases() {
    val connectionString = environment.config.property("mongodb.connectionString").getString()
    val databaseName = environment.config.property("mongodb.database").getString()
    
    MongoDB.init(connectionString, databaseName)
    
    log.info("MongoDB connected to database: $databaseName")
}
