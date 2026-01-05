package org.example.project.data.repository

import org.example.project.data.database.MongoDB
import org.example.project.data.models.EmergencyContact
import org.litote.kmongo.*
import org.litote.kmongo.coroutine.CoroutineCollection

class EmergencyContactRepository {
    private val collection: CoroutineCollection<EmergencyContact> = MongoDB.database.getCollection()

    suspend fun create(contact: EmergencyContact): EmergencyContact {
        collection.insertOne(contact)
        return contact
    }

    suspend fun findById(id: String): EmergencyContact? {
        return collection.findOne(EmergencyContact::id eq id)
    }

    suspend fun findByIdAndUserId(id: String, userId: String): EmergencyContact? {
        return collection.findOne(
            and(
                EmergencyContact::id eq id,
                EmergencyContact::userId eq userId
            )
        )
    }

    suspend fun findByUserId(userId: String): List<EmergencyContact> {
        return collection
            .find(EmergencyContact::userId eq userId)
            .sort(ascending(EmergencyContact::priority))
            .toList()
    }

    suspend fun findActiveByUserId(userId: String): List<EmergencyContact> {
        return collection
            .find(
                and(
                    EmergencyContact::userId eq userId,
                    EmergencyContact::isActive eq true
                )
            )
            .sort(ascending(EmergencyContact::priority))
            .toList()
    }

    suspend fun findByIds(ids: List<String>): List<EmergencyContact> {
        return collection
            .find(EmergencyContact::id `in` ids)
            .toList()
    }

    suspend fun update(contact: EmergencyContact): Boolean {
        val result = collection.updateOne(
            EmergencyContact::id eq contact.id,
            contact.copy(updatedAt = System.currentTimeMillis())
        )
        return result.modifiedCount > 0
    }

    suspend fun delete(id: String, userId: String): Boolean {
        val result = collection.deleteOne(
            and(
                EmergencyContact::id eq id,
                EmergencyContact::userId eq userId
            )
        )
        return result.deletedCount > 0
    }

    suspend fun deleteAllByUserId(userId: String): Long {
        val result = collection.deleteMany(EmergencyContact::userId eq userId)
        return result.deletedCount
    }

    suspend fun countByUserId(userId: String): Long {
        return collection.countDocuments(EmergencyContact::userId eq userId)
    }
}
