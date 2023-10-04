package com.abtech.http.routes

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.micrometer.prometheus.PrometheusMeterRegistry

fun Application.adminModule(registry: PrometheusMeterRegistry): Routing {
    return routing {
        route("management/") {
            head("alive.txt") {
                call.respond("OK")
            }

            get("alive.txt") {
                call.respond("OK")
            }

            get("prometheus") {
                call.respond(registry.scrape())
            }
        }
    }
}
