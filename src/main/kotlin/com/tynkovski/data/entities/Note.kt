package com.tynkovski.data.entities

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import org.bson.BsonTimestamp
import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId

@Serializable
data class Note(
    val text: String,
    val ownerId: String,
    val tags: List<String> = listOf<String>(),
    val color: Long? = null,
    @BsonId val id: String = ObjectId().toString(),
    @Contextual val createdAt: BsonTimestamp = BsonTimestamp(System.currentTimeMillis()),
) {
    companion object {
        const val TABLE_NAME = "notes"
    }
}
