package org.example.project.data.repository

import org.example.project.data.database.MongoDB
import org.example.project.data.models.*
import org.litote.kmongo.*
import org.litote.kmongo.coroutine.CoroutineCollection

class SOSAlertRepository {
    private val collection: CoroutineCollection<SOSAlert> = MongoDB.database.getCollection()

    suspend fun create(alert: SOSAlert): SOSAlert {
        collection.insertOne(alert)
        return alert
    }

    suspend fun findById(id: String): SOSAlert? {
        return collection.findOne(SOSAlert::id eq id)
    }

    suspend fun findByIdAndUserId(id: String, userId: String): SOSAlert? {
        return collection.findOne(
            and(
                SOSAlert::id eq id,
                SOSAlert::userId eq userId
            )
        )
    }

    suspend fun findActiveByUserId(userId: String): SOSAlert? {
        return collection.findOne(
            and(
                SOSAlert::userId eq userId,
                SOSAlert::status eq AlertStatus.ACTIVE
            )
        )
    }

    suspend fun findByUserId(userId: String, page: Int = 1, limit: Int = 20): List<SOSAlert> {
        return collection
            .find(SOSAlert::userId eq userId)
            .sort(descending(SOSAlert::createdAt))
            .skip((page - 1) * limit)
            .limit(limit)
            .toList()
    }

    suspend fun findByTripId(tripId: String): SOSAlert? {
        return collection.findOne(SOSAlert::tripId eq tripId)
    }

    suspend fun findAllActive(): List<SOSAlert> {
        return collection
            .find(SOSAlert::status eq AlertStatus.ACTIVE)
            .sort(descending(SOSAlert::createdAt))
            .toList()
    }

    suspend fun update(alert: SOSAlert): Boolean {
        val result = collection.updateOne(
            SOSAlert::id eq alert.id,
            alert.copy(updatedAt = System.currentTimeMillis())
        )
        return result.modifiedCount > 0
    }

    suspend fun resolve(
        alertId: String,
        status: AlertStatus,
        resolvedBy: String,
        notes: String? = null
    ): Boolean {
        val result = collection.updateOne(
            SOSAlert::id eq alertId,
            combine(
                setValue(SOSAlert::status, status),
                setValue(SOSAlert::resolvedAt, System.currentTimeMillis()),
                setValue(SOSAlert::resolvedBy, resolvedBy),
                setValue(SOSAlert::resolutionNotes, notes),
                setValue(SOSAlert::updatedAt, System.currentTimeMillis())
            )
        )
        return result.modifiedCount > 0
    }

    suspend fun updateNotifiedContacts(alertId: String, contacts: List<NotifiedContact>): Boolean {
        val result = collection.updateOne(
            SOSAlert::id eq alertId,
            combine(
                setValue(SOSAlert::notifiedContacts, contacts),
                setValue(SOSAlert::updatedAt, System.currentTimeMillis())
            )
        )
        return result.modifiedCount > 0
    }

    suspend fun countByUserId(userId: String): Long {
        return collection.countDocuments(SOSAlert::userId eq userId)
    }

    suspend fun countActiveAlerts(): Long {
        return collection.countDocuments(SOSAlert::status eq AlertStatus.ACTIVE)
    }
}
