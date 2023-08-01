package com.tynkovski.routes

import com.tynkovski.data.datasources.NoteDataSource
import com.tynkovski.data.entities.Sort
import com.tynkovski.data.mappers.noteMapper
import com.tynkovski.data.requests.CreateNoteRequest
import com.tynkovski.data.requests.UpdateNoteRequest
import com.tynkovski.data.responses.NoteCreated
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

                call.respond(HttpStatusCode.OK, NotesResponse(notes.size, notes))
            }
        }
    }
}

fun Route.saveNote(
    noteDataSource: NoteDataSource
) {
    authenticate {
        post("/createNote") {
            safe {
                val principal = call.principal<JWTPrincipal>()
                val userId = principal?.getClaim("userId", String::class)
                    ?: throw IllegalStateException("Getting user error")

                val request = call.receive<CreateNoteRequest>()
                val note = noteMapper(userId, request)

                val id = noteDataSource.createNote(note)

                if (id != null) {
                    call.respond(HttpStatusCode.OK, NoteCreated(id))
                } else {
                    throw IllegalStateException("Saving note error")
                }
            }
        }
    }
}

fun Route.updateNote(
    noteDataSource: NoteDataSource
) {
    authenticate {
        post("/updateNote") {
            safe {
                val principal = call.principal<JWTPrincipal>()
                val userId = principal?.getClaim("userId", String::class)
                    ?: throw IllegalStateException("Getting user error")

                val request = call.receive<UpdateNoteRequest>()
                val note = noteMapper(userId, request)

                val wasAcknowledged = noteDataSource.updateNote(userId, note)

                if (wasAcknowledged) {
                    call.respond(HttpStatusCode.OK)
                } else {
                    throw IllegalStateException("Saving note error")
                }
            }
        }
    }
}