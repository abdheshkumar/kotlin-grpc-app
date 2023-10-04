package e2e.com.abtech

import arrow.core.identity
import arrow.fx.coroutines.Resource
import arrow.fx.coroutines.resource
import com.abtech.app.Environment
import com.abtech.app.application
import io.grpc.ManagedChannel
import io.grpc.netty.shaded.io.grpc.netty.NettyChannelBuilder
import org.testcontainers.containers.KafkaContainer
import org.testcontainers.utility.DockerImageName
import java.util.concurrent.TimeUnit

fun testApplication(): Resource<TestApp> = resource {
    val kafkaBootstrapServers = kafkaResource().bind()
    val mapResource = mapOf("kafkaConfig.kafkaBootstrapServers" to kafkaBootstrapServers)
    val config = application(Environment.DEV, mapResource).fold({ throw it }, ::identity).bind()
    val channel = grpcChannel(config.grpcPort).bind()
    TestApp(kafkaBootstrapServers, channel)
}

private fun kafkaResource(): Resource<String> = resource {
    val kafka = resource({
        val container = KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7.2.2"))
        container.start()
        container
    }) { container, exit ->
        println("Closing ${container.javaClass.simpleName} : $exit")
        container.close()
    }
    kafka.bind().bootstrapServers
}

fun grpcChannel(grpcPort: Int): Resource<ManagedChannel> {
    return resource({
        NettyChannelBuilder
            .forAddress("localhost", grpcPort)
            .usePlaintext()
            .build()
    }) { ch, _ ->
        ch.shutdown().awaitTermination(2, TimeUnit.SECONDS)
    }
}

class TestApp(val bootstrapServers: String, val channel: ManagedChannel)
