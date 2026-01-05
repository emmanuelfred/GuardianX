package org.example.project.data.repository

import org.example.project.data.database.MongoDB
import org.example.project.data.models.User
import org.litote.kmongo.*
import org.litote.kmongo.coroutine.CoroutineCollection

class UserRepository {
    private val collection: CoroutineCollection<User> = MongoDB.database.getCollection()

    suspend fun createUser(user: User): User {
        collection.insertOne(user)
        return user
    }

    suspend fun findById(id: String): User? {
        return collection.findOne(User::id eq id)
    }

    suspend fun findByEmail(email: String): User? {
        return collection.findOne(User::email eq email)
    }

    suspend fun findByEmailIgnoreCase(email: String): User? {
        return collection.findOne(User::email regex Regex(email, RegexOption.IGNORE_CASE))
    }

    suspend fun emailExists(email: String): Boolean {
        return collection.countDocuments(User::email eq email) > 0
    }

    suspend fun updateUser(user: User): Boolean {
        val result = collection.updateOne(
            User::id eq user.id,
            user.copy(updatedAt = System.currentTimeMillis())
        )
        return result.modifiedCount > 0
    }

    suspend fun updateVerificationCode(userId: String, code: String, expiry: Long): Boolean {
        val result = collection.updateOne(
            User::id eq userId,
            combine(
                setValue(User::verificationCode, code),
                setValue(User::verificationCodeExpiry, expiry),
                setValue(User::updatedAt, System.currentTimeMillis())
            )
        )
        return result.modifiedCount > 0
    }

    suspend fun verifyEmail(userId: String): Boolean {
        val result = collection.updateOne(
            User::id eq userId,
            combine(
                setValue(User::isEmailVerified, true),
                setValue(User::verificationCode, null),
                setValue(User::verificationCodeExpiry, null),
                setValue(User::updatedAt, System.currentTimeMillis())
            )
        )
        return result.modifiedCount > 0
    }

    suspend fun updateResetPasswordCode(userId: String, code: String, expiry: Long): Boolean {
        val result = collection.updateOne(
            User::id eq userId,
            combine(
                setValue(User::resetPasswordCode, code),
                setValue(User::resetPasswordCodeExpiry, expiry),
                setValue(User::updatedAt, System.currentTimeMillis())
            )
        )
        return result.modifiedCount > 0
    }

    suspend fun updatePassword(userId: String, passwordHash: String): Boolean {
        val result = collection.updateOne(
            User::id eq userId,
            combine(
                setValue(User::passwordHash, passwordHash),
                setValue(User::resetPasswordCode, null),
                setValue(User::resetPasswordCodeExpiry, null),
                setValue(User::updatedAt, System.currentTimeMillis())
            )
        )
        return result.modifiedCount > 0
    }

    suspend fun updateLocation(userId: String, location: org.example.project.data.models.Location): Boolean {
        val result = collection.updateOne(
            User::id eq userId,
            combine(
                setValue(User::lastLocation, location),
                setValue(User::updatedAt, System.currentTimeMillis())
            )
        )
        return result.modifiedCount > 0
    }

    suspend fun updateDeviceToken(userId: String, token: String): Boolean {
        val result = collection.updateOne(
            User::id eq userId,
            combine(
                setValue(User::deviceToken, token),
                setValue(User::updatedAt, System.currentTimeMillis())
            )
        )
        return result.modifiedCount > 0
    }

    suspend fun deleteUser(userId: String): Boolean {
        val result = collection.deleteOne(User::id eq userId)
        return result.deletedCount > 0
    }
}
