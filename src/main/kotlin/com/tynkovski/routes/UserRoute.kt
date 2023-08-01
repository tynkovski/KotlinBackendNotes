package com.tynkovski.routes

import com.tynkovski.data.datasources.UserDataSource
import com.tynkovski.data.mappers.userMapper
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.getUser(userDataSource: UserDataSource) {
    authenticate {
        get("/user/get") {
            safe {
                val principal = call.principal<JWTPrincipal>()

                val userId = principal?.getClaim("userId", String::class)
                    ?: throw IllegalStateException("Getting user error")

                val user = userDataSource.getUserById(userId)
                    ?: throw IllegalStateException("Getting user error. Invalid id $userId")

                call.respond(HttpStatusCode.OK, userMapper(user))
            }
        }
    }
}

fun Route.deleteUser(userDataSource: UserDataSource) {
    authenticate {
        get("/user/delete") {
            safe {
                val principal = call.principal<JWTPrincipal>()

                val userId = principal?.getClaim("userId", String::class)
                    ?: throw IllegalStateException("Getting user error")

                val wasAcknowledged = userDataSource.deleteUserById(userId)

                if (wasAcknowledged) {
                    call.respond(HttpStatusCode.OK)
                } else {
                    throw IllegalStateException("Deleting user error. Invalid id $userId")
                }
            }
        }
    }
}