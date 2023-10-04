package com.abtech.grpc.server

import arrow.fx.coroutines.Resource
import com.be_hase.grpc.micrometer.MicrometerServerInterceptor
import io.grpc.BindableService
import io.grpc.Server
import io.grpc.ServerBuilder
import io.grpc.protobuf.services.HealthStatusManager
import io.grpc.protobuf.services.ProtoReflectionService
import io.micrometer.core.instrument.MeterRegistry

fun grpcServerResource(
    port: Int,
    meterRegistry: MeterRegistry,
    services: () -> List<BindableService>
): Resource<Server> =
    Resource({
        val server: Server = ServerBuilder
            .forPort(port)
            .addService(ProtoReflectionService.newInstance())
            .addService(HealthStatusManager().healthService)
            .intercept(MicrometerServerInterceptor(meterRegistry))
            .apply {
                services().forEach { this.addService(it) }
            }
            .build()
        server.start()
        println("Grpc Server started, listening on $port")
        Runtime.getRuntime().addShutdownHook(
            Thread {
                println("*** shutting down gRPC server since JVM is shutting down")
                server.shutdown()
                println("*** server shut down")
            }
        )
        server
    }) { server, exit ->
        println("Stopping grpc server: $exit")
        server.shutdown().awaitTermination()
    }
