package org.example.project.data.repository

import org.example.project.data.database.MongoDB
import org.example.project.data.models.Location
import org.example.project.data.models.PoliceStation
import org.example.project.data.models.calculateDistance
import org.litote.kmongo.*
import org.litote.kmongo.coroutine.CoroutineCollection

class PoliceStationRepository {
    private val collection: CoroutineCollection<PoliceStation> = MongoDB.database.getCollection()

    suspend fun create(station: PoliceStation): PoliceStation {
        collection.insertOne(station)
        return station
    }

    suspend fun findById(id: String): PoliceStation? {
        return collection.findOne(PoliceStation::id eq id)
    }

    suspend fun findAll(page: Int = 1, limit: Int = 20): List<PoliceStation> {
        return collection
            .find(PoliceStation::isActive eq true)
            .skip((page - 1) * limit)
            .limit(limit)
            .toList()
    }

    suspend fun findByState(state: String, page: Int = 1, limit: Int = 20): List<PoliceStation> {
        return collection
            .find(
                and(
                    PoliceStation::state regex Regex(state, RegexOption.IGNORE_CASE),
                    PoliceStation::isActive eq true
                )
            )
            .skip((page - 1) * limit)
            .limit(limit)
            .toList()
    }

    suspend fun findByStateAndLga(state: String, lga: String): List<PoliceStation> {
        return collection
            .find(
                and(
                    PoliceStation::state regex Regex(state, RegexOption.IGNORE_CASE),
                    PoliceStation::lga regex Regex(lga, RegexOption.IGNORE_CASE),
                    PoliceStation::isActive eq true
                )
            )
            .toList()
    }

    suspend fun findNearby(
        latitude: Double,
        longitude: Double,
        radiusKm: Double = 50.0,
        limit: Int = 10
    ): List<Pair<PoliceStation, Double>> {
        // Get all active stations and calculate distance
        // Note: For production, consider using MongoDB geospatial queries
        val allStations = collection
            .find(PoliceStation::isActive eq true)
            .toList()

        return allStations
            .map { station ->
                val distance = calculateDistance(
                    latitude, longitude,
                    station.location.latitude, station.location.longitude
                )
                Pair(station, distance)
            }
            .filter { it.second <= radiusKm }
            .sortedBy { it.second }
            .take(limit)
    }

    suspend fun findNearest(latitude: Double, longitude: Double): Pair<PoliceStation, Double>? {
        val nearby = findNearby(latitude, longitude, radiusKm = 100.0, limit = 1)
        return nearby.firstOrNull()
    }

    suspend fun update(station: PoliceStation): Boolean {
        val result = collection.updateOne(
            PoliceStation::id eq station.id,
            station.copy(updatedAt = System.currentTimeMillis())
        )
        return result.modifiedCount > 0
    }

    suspend fun verify(id: String): Boolean {
        val result = collection.updateOne(
            PoliceStation::id eq id,
            combine(
                setValue(PoliceStation::isVerified, true),
                setValue(PoliceStation::updatedAt, System.currentTimeMillis())
            )
        )
        return result.modifiedCount > 0
    }

    suspend fun delete(id: String): Boolean {
        val result = collection.updateOne(
            PoliceStation::id eq id,
            combine(
                setValue(PoliceStation::isActive, false),
                setValue(PoliceStation::updatedAt, System.currentTimeMillis())
            )
        )
        return result.modifiedCount > 0
    }

    suspend fun countAll(): Long {
        return collection.countDocuments(PoliceStation::isActive eq true)
    }

    suspend fun search(query: String, page: Int = 1, limit: Int = 20): List<PoliceStation> {
        val regex = Regex(query, RegexOption.IGNORE_CASE)
        return collection
            .find(
                and(
                    PoliceStation::isActive eq true,
                    or(
                        PoliceStation::name regex regex,
                        PoliceStation::address regex regex,
                        PoliceStation::state regex regex,
                        PoliceStation::lga regex regex
                    )
                )
            )
            .skip((page - 1) * limit)
            .limit(limit)
            .toList()
    }
}
