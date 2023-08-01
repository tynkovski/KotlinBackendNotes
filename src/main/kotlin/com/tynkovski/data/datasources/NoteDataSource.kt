package com.tynkovski.data.datasources

import com.mongodb.client.model.Filters
import com.mongodb.client.model.Sorts
import com.mongodb.client.model.Updates
import com.mongodb.kotlin.client.coroutine.MongoDatabase
import com.tynkovski.data.entities.Note
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.toList
import org.bson.BsonTimestamp

interface NoteDataSource {

    suspend fun getNotesPaged(ownerId: String, sort: Note.Sort, offset: Int, limit: Int): List<Note>

    suspend fun getNote(ownerId: String, id: String): Note?

    suspend fun createNote(note: Note): Boolean

    suspend fun updateNote(ownerId: String, note: Note): Boolean

    suspend fun deleteNote(ownerId: String, note: Note): Boolean

}

class NoteDataSourceImpl(
    database: MongoDatabase,
) : NoteDataSource {
    private val notes = database.getCollection<Note>(Note.TABLE_NAME)

    override suspend fun getNotesPaged(ownerId: String, sort: Note.Sort, offset: Int, limit: Int): List<Note> {
        val filters = Filters.eq(Note::ownerId.name, ownerId)

        val ownerNotes = notes
            .find(filters)
            .skip(offset)
            .limit(limit)
            .partial(true)

        val sortedNotes = when (sort) {
            is Note.Sort.ByText -> {
                if (sort.isAscending)
                    ownerNotes.sort(Sorts.ascending(Note::text.name))
                else
                    ownerNotes.sort(Sorts.descending(Note::text.name))
            }

            is Note.Sort.ByTitle -> {
                if (sort.isAscending)
                    ownerNotes.sort(Sorts.ascending(Note::title.name))
                else
                    ownerNotes.sort(Sorts.descending(Note::title.name))
            }

            is Note.Sort.ByDate -> {
                if (sort.isAscending)
                    ownerNotes.sort(Sorts.ascending(Note::createdAt.name))
                else
                    ownerNotes.sort(Sorts.descending(Note::createdAt.name))
            }
        }

        return sortedNotes.toList()
    }

    override suspend fun getNote(ownerId: String, id: String): Note? {
        val filters = Filters.and(Filters.eq(Note::ownerId.name, ownerId), Filters.eq("_id", id))

        return notes
            .find(filters)
            .firstOrNull()
    }

    override suspend fun createNote(note: Note): Boolean {
        return notes.insertOne(note).wasAcknowledged()
    }

    override suspend fun updateNote(
        ownerId: String,
        note: Note
    ): Boolean {
        val updates = listOf(
            Updates.set(Note::text.name, note.text),
            Updates.set(Note::title.name, note.title),
            Updates.set(Note::color.name, note.color),
            Updates.set(Note::tags.name, note.tags),
            Updates.set(Note::updatedAt.name, BsonTimestamp(System.currentTimeMillis())),
        )

        val filters = Filters.and(Filters.eq(Note::ownerId.name, ownerId), Filters.eq("_id", note.id))

        return notes.updateOne(filters, updates).wasAcknowledged()
    }

    override suspend fun deleteNote(ownerId: String, note: Note): Boolean {
        val filters = Filters.and(Filters.eq(Note::ownerId.name, ownerId), Filters.eq("_id", note.id))

        return notes.deleteOne(filters).wasAcknowledged()
    }
}