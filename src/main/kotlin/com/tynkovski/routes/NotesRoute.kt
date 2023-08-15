package com.tynkovski.routes

import com.tynkovski.data.datasources.NoteDataSource
import com.tynkovski.data.entities.Note
import com.tynkovski.data.mappers.noteMapper
import com.tynkovski.data.requests.NoteRequest
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
        get("/note/paged") {
            safe {
                val page = call.request.queryParameters["page"]?.toInt() ?: 0
                val limit = call.request.queryParameters["limit"]?.toInt() ?: 5
                val sort = Note.Sort.fromString(call.request.queryParameters["sort"])

                val principal = call.principal<JWTPrincipal>()

                val userId = principal?.getClaim("userId", String::class)
                    ?: throw IllegalStateException("Getting user error")

                val notes = noteDataSource
                    .getNotesPaged(userId, sort, page, limit)
                    .map(::noteMapper)

                call.respond(HttpStatusCode.OK, NotesResponse(notes.size, notes))
            }
        }
    }
}

fun Route.getNote(
    noteDataSource: NoteDataSource
) {
    authenticate {
        get("/note/get") {
            safe {
                val principal = call.principal<JWTPrincipal>()

                val userId = principal?.getClaim("userId", String::class)
                    ?: throw IllegalStateException("Getting user error")

                val id = call.request.queryParameters["id"] ?: throw IllegalStateException("ID must be specified")

                val note = noteDataSource.getNote(userId, id)
                    ?: throw IllegalStateException("Getting note error. Invalid id $id")

                call.respond(HttpStatusCode.OK, noteMapper(note))
            }
        }
    }
}

fun Route.saveNote(
    noteDataSource: NoteDataSource
) {
    authenticate {
        post("/note/create") {
            safe {
                val principal = call.principal<JWTPrincipal>()

                val userId = principal?.getClaim("userId", String::class)
                    ?: throw IllegalStateException("Getting user error")

                val request = call.receive<NoteRequest>()

                val note = noteMapper(userId, request)

                val wasAcknowledged = noteDataSource.createNote(note)

                if (wasAcknowledged) {
                    call.respond(HttpStatusCode.OK, noteMapper(note))
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
        put("/note/update") {
            safe {
                val principal = call.principal<JWTPrincipal>()

                val userId = principal?.getClaim("userId", String::class)
                    ?: throw IllegalStateException("Getting user error")

                val id = call.request.queryParameters["id"] ?: throw IllegalStateException("ID must be specified")

                val request = call.receive<NoteRequest>()

                val note = noteMapper(userId, id, request)

                val wasAcknowledged = noteDataSource.updateNote(userId, note)

                if (wasAcknowledged) {
                    call.respond(HttpStatusCode.OK, noteMapper(note))
                } else {
                    throw IllegalStateException("Updating note error")
                }
            }
        }
    }
}

fun Route.deleteNote(
    noteDataSource: NoteDataSource
) {
    authenticate {
        delete("/note/delete") {
            safe {
                val principal = call.principal<JWTPrincipal>()

                val userId = principal?.getClaim("userId", String::class)
                    ?: throw IllegalStateException("Getting user error")

                val id = call.request.queryParameters["id"] ?: throw IllegalStateException("ID must be specified")

                val note = noteDataSource.getNote(userId, id)
                    ?: throw IllegalStateException("Deleting note error. Invalid id $id")

                val wasAcknowledged = noteDataSource.deleteNote(userId, note)

                if (wasAcknowledged) {
                    call.respond(HttpStatusCode.OK, noteMapper(note))
                } else {
                    throw IllegalStateException("Deleting note error")
                }
            }
        }
    }
}