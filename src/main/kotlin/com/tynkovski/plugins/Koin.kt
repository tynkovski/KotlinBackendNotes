package com.tynkovski.plugins

import com.tynkovski.di.hashingModule
import com.tynkovski.di.dataSourceModule
import com.tynkovski.di.databaseModule
import com.tynkovski.di.tokenModule
import io.ktor.server.application.*
import org.koin.ktor.plugin.Koin

fun Application.configureKoin() {
    install(Koin) {
        modules(
            databaseModule,
            dataSourceModule,
            hashingModule,
            tokenModule
        )
    }
}