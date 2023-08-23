package com.tynkovski.routes

import com.tynkovski.data.datasources.UserDataSource
import com.tynkovski.data.entities.User
import com.tynkovski.data.requests.AuthRequest
import com.tynkovski.data.requests.RegisterRequest
import com.tynkovski.data.responses.AuthResponse
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

fun Route.register(
    userDataSource: UserDataSource,
    hashingService: HashingService,
    tokenService: TokenService,
    tokenConfig: TokenConfig
) {
    post("/auth/register") {
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
                name = request.name,
                password = saltedHash.hash,
                salt = saltedHash.salt,
            )

            val wasAcknowledged = userDataSource.createUser(newUser)

            if (wasAcknowledged) {
                val token = tokenService.generateToken(
                    config = tokenConfig,
                    TokenClaim(
                        name = "userId",
                        value = newUser.id
                    )
                )

                call.respond(HttpStatusCode.OK, AuthResponse(token))
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
    post("/auth/login") {
        safe {
            val request = call.receive<AuthRequest>()

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

            call.respond(HttpStatusCode.OK, AuthResponse(token = token))
        }
    }
}

fun Route.auth(userDataSource: UserDataSource) {
    authenticate {
        get("/auth") {
            safe {
                val principal = call.principal<JWTPrincipal>()

                val userId = principal?.getClaim("userId", String::class)
                    ?: throw IllegalStateException("Getting user error")

                call.respond(HttpStatusCode.OK)
            }
        }
    }
}