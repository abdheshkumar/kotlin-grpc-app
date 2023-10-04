package com.abtech.http.server

import arrow.fx.coroutines.Resource
import arrow.fx.coroutines.continuations.resource
import com.abtech.http.routes.adminModule
import com.abtech.metrics.MetricsProperties
import com.abtech.metrics.meterRegistryResource
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.metrics.micrometer.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.cors.routing.*
import io.micrometer.core.instrument.MeterRegistry
import io.micrometer.prometheus.PrometheusMeterRegistry
import kotlinx.serialization.json.Json
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

fun <TEngine : ApplicationEngine, TConfiguration : ApplicationEngine.Configuration> server(
    metrics: MetricsProperties,
    factory: ApplicationEngineFactory<TEngine, TConfiguration>,
    port: Int = 80,
    host: String = "0.0.0.0",
    configure: TConfiguration.() -> Unit = {},
    grace: Duration = 1.seconds,
    timeout: Duration = 5.seconds
): Resource<Pair<ApplicationEngine, MeterRegistry>> =
    resource {
        val (meterRegistry, prometheusMeterRegistry) = meterRegistryResource(metrics).bind()
        Resource({
            embeddedServer(factory, host = host, port = port, configure = configure) {
            }.apply {
                val applicationEngine = start()
                applicationEngine.application.healthCheckModule(meterRegistry, prometheusMeterRegistry)
                println("Admin Server started, listening on $port")
            } to meterRegistry
        }, { (engine, _), _ ->
            engine.environment.log.info("Shutting down HTTP server...")
            engine.stop(grace.inWholeMilliseconds, timeout.inWholeMilliseconds)
            engine.environment.log.info("HTTP server shutdown!")
        }).bind()
    }

fun Application.healthCheckModule(metricRegistry: MeterRegistry, prometheusMeterRegistry: PrometheusMeterRegistry) {
    install(CORS) {
        anyHost()
    }
    install(MicrometerMetrics) {
        registry = metricRegistry
    }
    install(ContentNegotiation) {
        json(
            Json {
                prettyPrint = true
                isLenient = true
            }
        )
    }
    adminModule(prometheusMeterRegistry)
}
