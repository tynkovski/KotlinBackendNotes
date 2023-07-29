package com.tynkovski.plugins

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.tynkovski.security.token.TokenConfig
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import org.koin.core.parameter.parametersOf
import org.koin.ktor.ext.inject

fun Application.configureSecurity() {
    val config by inject<TokenConfig> { parametersOf(environment.config) }
    val myRealm = environment.config.property("jwt.realm").getString()
    authentication {
        jwt {
            realm = myRealm

            verifier(
                JWT.require(Algorithm.HMAC256(config.secret))
                    .withAudience(config.audience)
                    .withIssuer(config.issuer)
                    .build()
            )

            validate { credential ->
                if (credential.payload.audience.contains(config.audience)) JWTPrincipal(credential.payload) else null
            }
        }
    }
}
