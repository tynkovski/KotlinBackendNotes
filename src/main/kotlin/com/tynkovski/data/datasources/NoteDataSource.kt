package com.tynkovski.data.datasources

import com.mongodb.client.model.Filters
import com.mongodb.client.model.Sorts
import com.mongodb.kotlin.client.coroutine.MongoDatabase
import com.tynkovski.data.entities.Note
import com.tynkovski.data.entities.Sort
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.toList

interface NoteDataSource {

    suspend fun getNotesPaged(ownerId: String, sort: Sort, offset: Int, limit: Int): List<Note>

    suspend fun getNote(ownerId: String, id: String): Note?

    suspend fun createNote(note: Note): Boolean

}

class NoteDataSourceImpl(
    database: MongoDatabase,
) : NoteDataSource {
    private val notes = database.getCollection<Note>(Note.TABLE_NAME)

    override suspend fun getNotesPaged(ownerId: String, sort: Sort, offset: Int, limit: Int): List<Note> {
        val ownerNotes = notes
            .find(Filters.eq(Note::ownerId.name, ownerId))
            .skip(offset)
            .limit(limit)
            .partial(true)

        val sortedNotes = when(sort) {
            is Sort.ByText -> {
                if (sort.isAscending)
                    ownerNotes.sort(Sorts.ascending(Note::text.name))
                else
                    ownerNotes.sort(Sorts.descending(Note::text.name))
            }
            is Sort.ByTitle -> {
                if (sort.isAscending)
                    ownerNotes.sort(Sorts.ascending(Note::title.name))
                else
                    ownerNotes.sort(Sorts.descending(Note::title.name))
            }
            is Sort.ByDate -> {
                if (sort.isAscending)
                    ownerNotes.sort(Sorts.ascending(Note::createdAt.name))
                else
                    ownerNotes.sort(Sorts.descending(Note::createdAt.name))
            }
        }

        return sortedNotes.toList()
    }

    override suspend fun getNote(ownerId: String, id: String): Note? {
        return notes
            .find(Filters.and(Filters.eq(Note::ownerId.name, ownerId), Filters.eq("_id", id)))
            .firstOrNull()
    }

    override suspend fun createNote(note: Note): Boolean {
        return notes.insertOne(note).wasAcknowledged()
    }
}