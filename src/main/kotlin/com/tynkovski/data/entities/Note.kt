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
    val title: String? = null,
    val color: Long? = null,
    val tags: List<String> = listOf<String>(),
    @BsonId val id: String = ObjectId().toString(),
    @Contextual val createdAt: BsonTimestamp = BsonTimestamp(System.currentTimeMillis()),
    @Contextual val updatedAt: BsonTimestamp? = null,
) {
    companion object {
        const val TABLE_NAME = "notes"
    }
}

sealed class Sort(val isAscending: Boolean) {
    companion object {
        fun fromString(string: String?): Sort {
            return when (string) {
                "TITLE_ASC" -> ByTitle(true)
                "TEXT_ASC" -> ByText(true)
                "DATE_ASC" -> ByDate(true)
                "TITLE_DESC" -> ByTitle(false)
                "TEXT_DESC" -> ByText(false)
                "DATE_DESC" -> ByDate(false)
                else -> ByDate(false)
            }
        }
    }

    class ByDate(isAscending: Boolean) : Sort(isAscending)
    class ByTitle(isAscending: Boolean) : Sort(isAscending)
    class ByText(isAscending: Boolean) : Sort(isAscending)
}
