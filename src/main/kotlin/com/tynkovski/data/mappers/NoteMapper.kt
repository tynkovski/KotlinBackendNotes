package com.tynkovski.data.mappers

import com.tynkovski.data.entities.Note
import com.tynkovski.data.requests.SaveNoteRequest
import com.tynkovski.data.responses.NoteResponse

fun noteMapper(note: Note): NoteResponse = NoteResponse(
    text = note.text,
    tags = note.tags,
    color = note.color,
    title = note.title,
    createdAt = note.createdAt.value
)

fun noteMapper(userId: String, request: SaveNoteRequest): Note = Note(
    ownerId = userId,
    text = request.text,
    title = request.title,
    tags = request.tags,
    color = request.color,
)