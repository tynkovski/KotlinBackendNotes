package com.tynkovski.plugins

import com.tynkovski.di.*
import io.ktor.server.application.*
import org.koin.ktor.plugin.Koin

fun Application.configureKoin() {
    install(Koin) {
        modules(
            databaseModule,
            dataSourceModule,
            hashingModule,
            tokenModule,
            serializationModule
        )
    }
}