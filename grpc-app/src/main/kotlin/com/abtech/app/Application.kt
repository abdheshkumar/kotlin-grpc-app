package com.abtech.app

import arrow.core.Either
import arrow.core.continuations.either
import arrow.fx.coroutines.Resource
import arrow.fx.coroutines.continuations.resource
import com.abtech.grpc.server.grpcServerResource
import com.abtech.http.server.server
import com.abtech.app.grpc.api.HelloWorldService
import io.ktor.server.netty.*
import io.micrometer.core.instrument.MeterRegistry

suspend fun application(environment: Environment, mapSource: Map<String, Any>): Either<Throwable, Resource<Config>> {
    return either {
        loadApplicationConfig(environment.value, mapSource).map { config ->
            resource {
                println("ENVIRONMENT: $environment, config: $config")
                val (_, meterRegistry) = server(
                    config.metrics,
                    Netty,
                    port = config.adminPort,
                ).bind()
                val dependencies = dependencies(config, meterRegistry).bind()

                grpcServerResource(config.grpcPort, dependencies.meterRegistry) {
                    listOf(HelloWorldService())
                }.bind()

                config
            }
        }.bind()
    }
}

fun dependencies(config: Config, meterRegistry: MeterRegistry): Resource<Dependencies> = resource {
    // Start Kafka producer
    // Start CQLSession
    // Any resource
    Dependencies(meterRegistry)
}
