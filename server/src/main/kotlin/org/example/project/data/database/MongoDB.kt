package org.example.project.data.database

import org.litote.kmongo.coroutine.CoroutineClient
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.reactivestreams.KMongo

object MongoDB {
    private lateinit var client: CoroutineClient
    lateinit var database: CoroutineDatabase
        private set

    fun init(connectionString: String, databaseName: String) {
        client = KMongo.createClient(connectionString).coroutine
        database = client.getDatabase(databaseName)
    }

    fun close() {
        client.close()
    }
}
