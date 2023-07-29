package com.tynkovski.plugins

import com.tynkovski.data.datasources.user.UserDataSource
import com.tynkovski.routes.login
import com.tynkovski.routes.register
import com.tynkovski.security.hashing.HashingService
import com.tynkovski.security.token.TokenConfig
import com.tynkovski.security.token.TokenService
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Application.configureRouting() {
    val userDataSource by inject<UserDataSource>()
    val hashingService by inject<HashingService>()
    val tokenService by inject<TokenService>()

    val tokenConfig = TokenConfig(
        issuer = environment.config.property("jwt.issuer").getString(),
        audience = environment.config.property("jwt.audience").getString(),
        expiresIn = 1000L * 60L * 60L * 24L,
        secret = System.getenv("JWT_SECRET")
    )

    routing {
        get("/hello") { call.respondText("Welcome to notes") }

        register(userDataSource, hashingService, tokenService, tokenConfig)

        login(userDataSource, hashingService, tokenService, tokenConfig)
    }
}
