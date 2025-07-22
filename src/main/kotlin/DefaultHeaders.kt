package com.example

import io.ktor.http.HttpHeaders
import io.ktor.server.application.*
import io.ktor.server.plugins.defaultheaders.DefaultHeaders
import java.time.Duration

fun Application.configureDefaultHeader() {
    install(DefaultHeaders) {
        val oneYearInSeconds = Duration.ofDays(365).seconds
        header(
            name = HttpHeaders.CacheControl,
            value = "public, max-age=$oneYearInSeconds, immutable"
        )
    }
}
