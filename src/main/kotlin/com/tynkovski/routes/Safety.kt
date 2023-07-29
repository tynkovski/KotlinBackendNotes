package com.tynkovski.routes

import com.tynkovski.data.responses.ErrorWrapperResponse
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.util.pipeline.*

suspend inline fun PipelineContext<Unit, ApplicationCall>.safe(
    crossinline operation: suspend () -> Unit
) = try {
    operation()
} catch (error: IllegalStateException) {
    call.respond(HttpStatusCode.Conflict, ErrorWrapperResponse(error.message.toString()))
} catch (error: BadRequestException) {
    call.respond(HttpStatusCode.BadRequest, ErrorWrapperResponse(error.message.toString()))
} catch (error: Exception) {
    call.respond(HttpStatusCode.NotAcceptable, ErrorWrapperResponse(error.message.toString()))
}
