package com.tynkovski.di

import com.tynkovski.security.token.JwtTokenService
import com.tynkovski.security.token.TokenConfig
import com.tynkovski.security.token.TokenService
import io.ktor.server.config.*
import org.koin.dsl.module

val tokenModule = module {
    single<TokenService> { JwtTokenService() }
}