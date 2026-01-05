package org.example.project.utils

import io.ktor.http.*

open class ApiException(
    val statusCode: HttpStatusCode,
    override val message: String,
    val errorCode: String? = null
) : Exception(message)

class BadRequestException(message: String, errorCode: String? = "BAD_REQUEST") :
    ApiException(HttpStatusCode.BadRequest, message, errorCode)

class UnauthorizedException(message: String = "Unauthorized", errorCode: String? = "UNAUTHORIZED") :
    ApiException(HttpStatusCode.Unauthorized, message, errorCode)

class ForbiddenException(message: String = "Forbidden", errorCode: String? = "FORBIDDEN") :
    ApiException(HttpStatusCode.Forbidden, message, errorCode)

class NotFoundException(message: String = "Resource not found", errorCode: String? = "NOT_FOUND") :
    ApiException(HttpStatusCode.NotFound, message, errorCode)

class ConflictException(message: String, errorCode: String? = "CONFLICT") :
    ApiException(HttpStatusCode.Conflict, message, errorCode)

class InternalServerException(message: String = "Internal server error", errorCode: String? = "INTERNAL_ERROR") :
    ApiException(HttpStatusCode.InternalServerError, message, errorCode)
