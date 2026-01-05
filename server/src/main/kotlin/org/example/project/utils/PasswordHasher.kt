package org.example.project.utils

import org.mindrot.jbcrypt.BCrypt

object PasswordHasher {
    private const val SALT_ROUNDS = 12

    fun hash(password: String): String {
        return BCrypt.hashpw(password, BCrypt.gensalt(SALT_ROUNDS))
    }

    fun verify(password: String, hash: String): Boolean {
        return try {
            BCrypt.checkpw(password, hash)
        } catch (e: Exception) {
            false
        }
    }

    fun isValidPassword(password: String): Boolean {
        // Minimum 8 characters, at least one uppercase, one lowercase, one number
        if (password.length < 8) return false
        if (!password.any { it.isUpperCase() }) return false
        if (!password.any { it.isLowerCase() }) return false
        if (!password.any { it.isDigit() }) return false
        return true
    }
}
