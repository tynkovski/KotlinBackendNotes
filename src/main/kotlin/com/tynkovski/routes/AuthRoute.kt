package com.tynkovski.routes

import com.tynkovski.data.datasources.user.UserDataSource
import com.tynkovski.data.models.entities.User
import com.tynkovski.data.models.requests.LoginRequest
import com.tynkovski.data.models.requests.RegisterRequest
import com.tynkovski.data.models.responses.ErrorWrapperResponse
import com.tynkovski.data.models.responses.LoginResponse
import com.tynkovski.data.models.responses.RegisterResponse
import com.tynkovski.security.hashing.HashingService
import com.tynkovski.security.hashing.SaltedHash
import com.tynkovski.security.token.TokenClaim
import com.tynkovski.security.token.TokenConfig
import com.tynkovski.security.token.TokenService
import io.ktor.http.*
import io.ktor.serialization.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Route.register(
    userDataSource: UserDataSource,
    hashingService: HashingService,
    tokenService: TokenService,
    tokenConfig: TokenConfig
) {
    post("/register") {
        try {
            val request = call.receive<RegisterRequest>()

            val areFieldsBlank = request.login.isBlank() || request.password.isBlank()
            val isPasswordTooShort = request.password.length < 8

            if (areFieldsBlank) {
                call.respond(HttpStatusCode.Conflict, "Fields are empty")
                return@post
            }

            if (isPasswordTooShort) {
                call.respond(HttpStatusCode.Conflict, "Password is too short")
                return@post
            }

            val saltedHash = hashingService.generateSaltedHash(request.password)

            val newUser = User(
                login = request.login,
                password = saltedHash.hash,
                salt = saltedHash.salt
            )

            val wasAcknowledged = userDataSource.createUserAndGetId(newUser)

            if (wasAcknowledged) {
                val token = tokenService.generateToken(
                    config = tokenConfig,
                    TokenClaim(
                        name = "userId",
                        value = newUser.id
                    )
                )

                call.respond(HttpStatusCode.OK, RegisterResponse(token))
            } else {
                throw IllegalStateException("User already exists")
            }
        } catch (error: IllegalStateException) {
            call.respond(HttpStatusCode.Conflict, ErrorWrapperResponse(error.message.toString()))
        } catch (error: Exception) {
            call.respond(HttpStatusCode.Conflict, ErrorWrapperResponse(error.message.toString()))
        }
    }
}

fun Route.login(
    userDataSource: UserDataSource,
    hashingService: HashingService,
    tokenService: TokenService,
    tokenConfig: TokenConfig
) {
    post("/login") {
        try {
            val request = call.receive<LoginRequest>()

            val user = userDataSource.getUserByLogin(request.login)
                ?: throw IllegalStateException("No user with this login")

            val isValidPassword = hashingService.verify(
                value = request.password,
                saltedHash = SaltedHash(user.password, user.salt)
            )

            if (!isValidPassword) {
                throw IllegalStateException("Incorrect password")
            }

            val token = tokenService.generateToken(
                config = tokenConfig,
                TokenClaim(
                    name = "userId",
                    value = user.id
                )
            )

            call.respond(HttpStatusCode.OK, LoginResponse(token = token))

        } catch (error: IllegalStateException) {
            call.respond(HttpStatusCode.Conflict, ErrorWrapperResponse(error.message.toString()))
        } catch (error: Exception) {
            call.respond(HttpStatusCode.Conflict, ErrorWrapperResponse(error.message.toString()))
        }
    }
}