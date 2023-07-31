package com.tynkovski.plugins

import com.tynkovski.data.datasources.NoteDataSource
import com.tynkovski.data.datasources.UserDataSource
import com.tynkovski.routes.*
import com.tynkovski.security.hashing.HashingService
import com.tynkovski.security.token.TokenConfig
import com.tynkovski.security.token.TokenService
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.core.parameter.parametersOf
import org.koin.ktor.ext.inject

fun Application.configureRouting() {
    val noteDataSource by inject<NoteDataSource>()
    val userDataSource by inject<UserDataSource>()
    val hashingService by inject<HashingService>()
    val tokenService by inject<TokenService>()
    val config by inject<TokenConfig> { parametersOf(environment.config) }

    routing {
        // region Test
        get("/hello") { call.respondText("Welcome to notes") }
        // endregion

        // region Auth
        register(userDataSource, hashingService, tokenService, config)
        login(userDataSource, hashingService, tokenService, config)
        getUser(userDataSource)
        authenticate()
        // endregion

        // region Note
        updateNote(noteDataSource)
        saveNote(noteDataSource)
        getNotes(noteDataSource)
        // endregion
    }
}
