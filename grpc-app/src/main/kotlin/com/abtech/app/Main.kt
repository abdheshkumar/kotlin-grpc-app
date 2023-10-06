package com.abtech.app

import arrow.continuations.SuspendApp
import arrow.core.Either
import arrow.core.flatMap
import arrow.core.flatten
import arrow.core.left
import arrow.core.right
import kotlinx.coroutines.awaitCancellation

fun main(): Unit = SuspendApp {
    (System.getenv("ENVIRONMENT")?.right() ?: Unit.left())
        .mapLeft { Exception("No ENVIRONMENT provided") }
        .flatMap { Either.catch { application(parse(it), emptyMap()) }.flatten() }
        .fold({
            println("Error while starting an application: $it")
        }) { it.use { awaitCancellation() } }
}
