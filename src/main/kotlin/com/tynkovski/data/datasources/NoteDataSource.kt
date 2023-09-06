package com.tynkovski.data.datasources

import com.mongodb.client.model.Filters
import com.mongodb.client.model.Sorts
import com.mongodb.client.model.Updates
import com.mongodb.kotlin.client.coroutine.FindFlow
import com.mongodb.kotlin.client.coroutine.MongoDatabase
import com.tynkovski.data.entities.Note
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.toList
import org.bson.BsonTimestamp

interface NoteDataSource {

    suspend fun getNotesPaged(ownerId: String, sort: Note.Sort, page: Int, limit: Int): List<Note>

    suspend fun getNotes(ownerId: String, sort: Note.Sort): List<Note>

    suspend fun getNotes(ownerId: String, ids: List<String>, sort: Note.Sort = Note.Sort.ByDate(false)): List<Note>

    suspend fun getNote(ownerId: String, id: String): Note?

    suspend fun createNote(note: Note): Boolean

    suspend fun updateNote(ownerId: String, note: Note): Boolean

    suspend fun deleteNote(ownerId: String, note: Note): Boolean

    suspend fun deleteNotes(ownerId: String, list: List<Note>): Boolean

}

class NoteDataSourceImpl(
    database: MongoDatabase,
) : NoteDataSource {
    private val notes = database.getCollection<Note>(Note.TABLE_NAME)

    private suspend fun getSorted(flow: FindFlow<Note>, ascending: Boolean, fieldName: String): List<Note> {
        val sort = if (ascending) Sorts.ascending(fieldName) else Sorts.descending(fieldName)
        return flow.sort(sort).toList()
    }

    override suspend fun getNotesPaged(ownerId: String, sort: Note.Sort, page: Int, limit: Int): List<Note> {
        val filters = Filters.eq(Note::ownerId.name, ownerId)

        val ownerNotes = notes
            .find(filters)
            .skip(page * limit)
            .limit(limit)
            .partial(true)

        return getSorted(ownerNotes, sort.isAscending, sort.toString())
    }

    override suspend fun getNotes(ownerId: String, sort: Note.Sort): List<Note> {
        val filters = Filters.eq(Note::ownerId.name, ownerId)

        val ownerNotes = notes.find(filters)

        return getSorted(ownerNotes, sort.isAscending, sort.toString())
    }

    override suspend fun getNotes(ownerId: String, ids: List<String>, sort: Note.Sort): List<Note> {
        val filters = Filters.and(Filters.eq(Note::ownerId.name, ownerId), Filters.`in`("_id", ids))

        val ownerNotes = notes.find(filters)

        return getSorted(ownerNotes, sort.isAscending, sort.toString())
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
            Updates.set(Note::updatedAt.name, BsonTimestamp(System.currentTimeMillis())),
        )

        val filters = Filters.and(Filters.eq(Note::ownerId.name, ownerId), Filters.eq("_id", note.id))

        return notes.updateOne(filters, updates).wasAcknowledged()
    }

    override suspend fun deleteNote(ownerId: String, note: Note): Boolean {
        val filters = Filters.and(Filters.eq(Note::ownerId.name, ownerId), Filters.eq("_id", note.id))

        return notes.deleteOne(filters).wasAcknowledged()
    }

    override suspend fun deleteNotes(ownerId: String, list: List<Note>): Boolean {
        val ids = list.map { it.id }

        val filters = Filters.and(Filters.eq(Note::ownerId.name, ownerId), Filters.`in`("_id", ids))

        return notes.deleteMany(filters).wasAcknowledged()
    }
}