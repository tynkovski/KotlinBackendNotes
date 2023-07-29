package com.tynkovski.routes

import com.tynkovski.data.datasources.UserDataSource
import com.tynkovski.data.entities.User
import com.tynkovski.data.requests.LoginRequest
import com.tynkovski.data.requests.RegisterRequest
import com.tynkovski.data.responses.ErrorWrapperResponse
import com.tynkovski.data.responses.LoginResponse
import com.tynkovski.data.responses.RegisterResponse
import com.tynkovski.data.responses.UserResponse
import com.tynkovski.security.hashing.HashingService
import com.tynkovski.security.hashing.SaltedHash
import com.tynkovski.security.token.TokenClaim
import com.tynkovski.security.token.TokenConfig
import com.tynkovski.security.token.TokenService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.util.pipeline.*

fun Route.register(
    userDataSource: UserDataSource,
    hashingService: HashingService,
    tokenService: TokenService,
    tokenConfig: TokenConfig
) {
    post("/register") {
        safe {
            val request = call.receive<RegisterRequest>()

            val areFieldsBlank = request.login.isBlank() || request.password.isBlank()
            val isPasswordTooShort = request.password.length < 8

            if (areFieldsBlank) {
                throw IllegalStateException("Fields are empty")
            }

            if (isPasswordTooShort) {
                throw IllegalStateException("Password is too short")
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
        safe {
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
        }
    }
}

fun Route.authenticate() {
    authenticate {
        get("/authenticate") {
            safe {
                call.respond(HttpStatusCode.OK)
            }
        }
    }
}

fun Route.getUser(userDataSource: UserDataSource) {
    authenticate {
        get("/user") {
            safe {
                val principal = call.principal<JWTPrincipal>()

                val userId = principal?.getClaim("userId", String::class)
                    ?: throw IllegalStateException("Getting user error")

                val user = userDataSource.getUserById(userId)
                    ?: throw IllegalStateException("Getting user error. Invalid id $userId")

                call.respond(HttpStatusCode.OK, UserResponse(user.id, user.login))
            }
        }
    }
}