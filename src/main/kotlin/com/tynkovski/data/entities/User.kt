package com.tynkovski.data.entities

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import org.bson.BsonTimestamp
import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId

@Serializable
data class User(
    val login: String,
    val name: String,
    val password: String,
    val salt: String,
    @BsonId val id: String = ObjectId().toString(),
    @Contextual val createdAt: BsonTimestamp = BsonTimestamp(System.currentTimeMillis()),
) {
    companion object {
        const val TABLE_NAME = "users"
    }
}