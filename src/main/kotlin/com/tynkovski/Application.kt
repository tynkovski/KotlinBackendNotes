package com.tynkovski

import com.tynkovski.plugins.*
import com.tynkovski.security.token.TokenConfig
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import org.koin.core.parameter.parametersOf
import org.koin.ktor.ext.inject

fun main(args: Array<String>) = EngineMain.main(args)

fun Application.module() {
    configureKoin()
    configureMonitoring()
    configureSerialization()
    configureSecurity()
    configureRouting()
}
