package com.tynkovski.data.mappers

import com.tynkovski.data.entities.Note
import com.tynkovski.data.responses.NoteResponse

val noteMapper: (Note) -> NoteResponse = { note ->
    NoteResponse(
        text = note.text,
        tags = note.tags,
        color = note.color,
    )
}