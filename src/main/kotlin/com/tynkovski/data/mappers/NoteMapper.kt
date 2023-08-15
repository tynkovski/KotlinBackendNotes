package com.tynkovski.data.mappers

import com.tynkovski.data.entities.Note
import com.tynkovski.data.requests.NoteRequest
import com.tynkovski.data.responses.NoteResponse

fun noteMapper(note: Note): NoteResponse = NoteResponse(
    id = note.id,
    text = note.text,
    color = note.color,
    title = note.title,
    createdAt = note.createdAt.value,
    updatedAt = note.updatedAt.value
)

fun noteMapper(userId: String, request: NoteRequest): Note = Note(
    ownerId = userId,
    text = request.text,
    title = request.title,
    color = request.color,
)

fun noteMapper(userId: String, noteId: String, request: NoteRequest): Note = Note(
    id = noteId,
    ownerId = userId,
    text = request.text,
    title = request.title,
    color = request.color,
)