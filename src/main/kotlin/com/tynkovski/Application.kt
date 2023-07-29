package com.tynkovski

import com.tynkovski.plugins.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*

fun main(args: Array<String>) = EngineMain.main(args)

fun Application.module() {
    configureKoin()
    configureMonitoring()
    configureSerialization()
    configureSecurity()
    configureRouting()
}
