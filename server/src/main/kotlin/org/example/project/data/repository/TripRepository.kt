package org.example.project.data.repository

import org.example.project.data.database.MongoDB
import org.example.project.data.models.*
import org.litote.kmongo.*
import org.litote.kmongo.coroutine.CoroutineCollection

class TripRepository {
    private val collection: CoroutineCollection<Trip> = MongoDB.database.getCollection()

    suspend fun create(trip: Trip): Trip {
        collection.insertOne(trip)
        return trip
    }

    suspend fun findById(id: String): Trip? {
        return collection.findOne(Trip::id eq id)
    }

    suspend fun findByIdAndUserId(id: String, userId: String): Trip? {
        return collection.findOne(
            and(
                Trip::id eq id,
                Trip::userId eq userId
            )
        )
    }

    suspend fun findActiveByUserId(userId: String): Trip? {
        return collection.findOne(
            and(
                Trip::userId eq userId,
                Trip::status eq TripStatus.ACTIVE
            )
        )
    }

    suspend fun findByUserId(userId: String, page: Int = 1, limit: Int = 20): List<Trip> {
        return collection
            .find(Trip::userId eq userId)
            .sort(descending(Trip::createdAt))
            .skip((page - 1) * limit)
            .limit(limit)
            .toList()
    }

    suspend fun findAllActive(): List<Trip> {
        return collection
            .find(Trip::status eq TripStatus.ACTIVE)
            .toList()
    }

    suspend fun findOverdueTrips(): List<Trip> {
        val now = System.currentTimeMillis()
        return collection
            .find(
                and(
                    Trip::status eq TripStatus.ACTIVE,
                    Trip::expectedArrivalTime lt now
                )
            )
            .toList()
    }

    suspend fun findTripsNeedingCheckIn(checkInThresholdMs: Long): List<Trip> {
        val threshold = System.currentTimeMillis() - checkInThresholdMs
        return collection
            .find(
                and(
                    Trip::status eq TripStatus.ACTIVE,
                    Trip::lastCheckIn lt threshold
                )
            )
            .toList()
    }

    suspend fun update(trip: Trip): Boolean {
        val result = collection.updateOne(
            Trip::id eq trip.id,
            trip.copy(updatedAt = System.currentTimeMillis())
        )
        return result.modifiedCount > 0
    }

    suspend fun updateStatus(tripId: String, status: TripStatus): Boolean {
        val updates = mutableListOf(
            setValue(Trip::status, status),
            setValue(Trip::updatedAt, System.currentTimeMillis())
        )
        
        if (status == TripStatus.COMPLETED || status == TripStatus.CANCELLED) {
            updates.add(setValue(Trip::completedAt, System.currentTimeMillis()))
        }
        
        if (status == TripStatus.PANIC_ACTIVATED) {
            updates.add(setValue(Trip::panicActivatedAt, System.currentTimeMillis()))
        }
        
        val result = collection.updateOne(
            Trip::id eq tripId,
            combine(updates)
        )
        return result.modifiedCount > 0
    }

    suspend fun recordCheckIn(tripId: String, checkIn: TripCheckIn, newLocation: Location?): Boolean {
        val trip = findById(tripId) ?: return false
        val updatedCheckIns = trip.checkInHistory + checkIn
        
        val result = collection.updateOne(
            Trip::id eq tripId,
            combine(
                setValue(Trip::checkInHistory, updatedCheckIns),
                setValue(Trip::lastCheckIn, System.currentTimeMillis()),
                setValue(Trip::lastLocation, newLocation ?: trip.lastLocation),
                setValue(Trip::updatedAt, System.currentTimeMillis())
            )
        )
        return result.modifiedCount > 0
    }

    suspend fun incrementMissedCheckIns(tripId: String): Boolean {
        val trip = findById(tripId) ?: return false
        val result = collection.updateOne(
            Trip::id eq tripId,
            combine(
                setValue(Trip::missedCheckIns, trip.missedCheckIns + 1),
                setValue(Trip::updatedAt, System.currentTimeMillis())
            )
        )
        return result.modifiedCount > 0
    }

    suspend fun extendTrip(tripId: String, additionalMinutes: Int): Boolean {
        val trip = findById(tripId) ?: return false
        val newExpectedArrival = trip.expectedArrivalTime + (additionalMinutes * 60 * 1000L)
        
        val result = collection.updateOne(
            Trip::id eq tripId,
            combine(
                setValue(Trip::expectedArrivalTime, newExpectedArrival),
                setValue(Trip::status, TripStatus.EXTENDED),
                setValue(Trip::updatedAt, System.currentTimeMillis())
            )
        )
        return result.modifiedCount > 0
    }

    suspend fun countByUserId(userId: String): Long {
        return collection.countDocuments(Trip::userId eq userId)
    }
}
