package com.tynkovski.routes

import com.tynkovski.data.datasources.NoteDataSource
import com.tynkovski.data.entities.Sort
import com.tynkovski.data.mappers.noteMapper
import com.tynkovski.data.requests.SaveNoteRequest
import com.tynkovski.data.responses.NotesResponse
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.getNotes(
    noteDataSource: NoteDataSource
) {
    authenticate {
        get("/notesPaged") {
            safe {
                val offset = call.request.queryParameters["offset"]?.toInt() ?: 0
                val limit = call.request.queryParameters["limit"]?.toInt() ?: 5
                val sort = Sort.fromString(call.request.queryParameters["sort"])

                val principal = call.principal<JWTPrincipal>()
                val userId = principal?.getClaim("userId", String::class)
                    ?: throw IllegalStateException("Getting user error")

                val notes = noteDataSource
                    .getNotesPaged(userId, sort, offset, limit)
                    .map(::noteMapper)

                call.respond(HttpStatusCode.OK, NotesResponse(notes))
            }
        }
    }
}

fun Route.saveNote(
    noteDataSource: NoteDataSource
) {
    authenticate {
        post("/note") {
            safe {
                val principal = call.principal<JWTPrincipal>()
                val userId = principal?.getClaim("userId", String::class)
                    ?: throw IllegalStateException("Getting user error")

                val request = call.receive<SaveNoteRequest>()
                val note = noteMapper(userId, request)

                val wasAcknowledged = noteDataSource.createNote(note)

                if (wasAcknowledged) {
                    call.respond(HttpStatusCode.OK)
                } else {
                    throw IllegalStateException("Saving note error")
                }
            }
        }
    }
}