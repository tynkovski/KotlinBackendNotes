package com.tynkovski.di

import com.tynkovski.security.token.JwtTokenService
import com.tynkovski.security.token.TokenConfig
import com.tynkovski.security.token.TokenService
import io.ktor.server.config.*
import org.koin.dsl.module

val tokenModule = module {
    single<TokenService> { JwtTokenService() }

    single<TokenConfig> { (config: ApplicationConfig) ->
        TokenConfig(
            issuer = config.property("jwt.issuer").getString(),
            audience = config.property("jwt.audience").getString(),
            expiresIn = 1000L * 60L * 60L * 24L * 365L,
            secret = System.getenv("JWT_SECRET")
        )
    }
}