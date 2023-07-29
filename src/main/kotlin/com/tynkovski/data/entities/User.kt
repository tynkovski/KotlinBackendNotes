package com.tynkovski.data.entities

import kotlinx.serialization.Serializable
import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId

@Serializable
data class User(
    @BsonId val id: String = ObjectId().toString(),
    val login: String,
    val password: String,
    val salt: String
) {
    companion object {
        const val TABLE_NAME = "users"
    }
}

