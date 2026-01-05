package org.example.project

import io.ktor.server.application.*
import io.ktor.server.netty.*
import org.example.project.plugins.*

fun main(args: Array<String>): Unit = EngineMain.main(args)

fun Application.module() {
    configureSerialization()
    configureSecurity()
    configureCORS()
    configureStatusPages()
    configureMonitoring()
    configureDatabases()
    configureRouting()
}
