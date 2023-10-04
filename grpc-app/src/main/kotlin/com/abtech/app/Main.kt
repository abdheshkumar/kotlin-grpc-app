package com.abtech.app

import arrow.continuations.SuspendApp
import arrow.core.Either
import arrow.core.flatMap
import arrow.core.flatten
import arrow.fx.coroutines.never
import kotlinx.coroutines.awaitCancellation

fun main(): Unit = SuspendApp {
    Either.fromNullable(System.getenv("ENVIRONMENT"))
        .mapLeft { Exception("No ENVIRONMENT provided") }
        .flatMap { Either.catch { application(parse(it), emptyMap()) }.flatten() }
        .fold({
            println("Error while starting an application: $it")
        }) { it.use { awaitCancellation() } }
}
