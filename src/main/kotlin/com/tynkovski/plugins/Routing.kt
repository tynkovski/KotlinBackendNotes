package com.tynkovski.plugins

import com.tynkovski.data.datasources.UserDataSource
import com.tynkovski.routes.authenticate
import com.tynkovski.routes.getUser
import com.tynkovski.routes.login
import com.tynkovski.routes.register
import com.tynkovski.security.hashing.HashingService
import com.tynkovski.security.token.TokenConfig
import com.tynkovski.security.token.TokenService
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.core.parameter.parametersOf
import org.koin.ktor.ext.inject

fun Application.configureRouting() {
    val userDataSource by inject<UserDataSource>()
    val hashingService by inject<HashingService>()
    val tokenService by inject<TokenService>()
    val config by inject<TokenConfig> { parametersOf(environment.config) }

    routing {
        get("/hello") { call.respondText("Welcome to notes") }

        register(userDataSource, hashingService, tokenService, config)

        login(userDataSource, hashingService, tokenService, config)

        authenticate()

        getUser(userDataSource)
    }
}
