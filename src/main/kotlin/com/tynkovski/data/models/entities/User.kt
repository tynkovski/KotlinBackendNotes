package com.tynkovski.data.models.entities

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
        fun collectionName() = "users"
    }
}

