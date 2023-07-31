package com.tynkovski.data.mappers

import com.tynkovski.data.entities.Note
import com.tynkovski.data.requests.CreateNoteRequest
import com.tynkovski.data.requests.UpdateNoteRequest
import com.tynkovski.data.responses.NoteResponse

fun noteMapper(note: Note): NoteResponse = NoteResponse(
    id = note.id,
    text = note.text,
    title = note.title,
    color = note.color,
    tags = note.tags,
    createdAt = note.createdAt.value,
    updatedAt = note.updatedAt?.value
)

fun noteMapper(userId: String, request: CreateNoteRequest): Note = Note(
    ownerId = userId,
    text = request.text,
    title = request.title,
    color = request.color,
    tags = request.tags,
)

fun noteMapper(userId: String, request: UpdateNoteRequest): Note = Note(
    id = request.id,
    ownerId = userId,
    text = request.text,
    title = request.title,
    color = request.color,
    tags = request.tags,
)