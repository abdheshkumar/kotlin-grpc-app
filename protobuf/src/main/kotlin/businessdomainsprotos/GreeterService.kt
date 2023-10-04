package businessdomainsprotos

import io.grpc.examples.helloworld.GreeterGrpcKt
import io.grpc.examples.helloworld.HelloReply
import io.grpc.examples.helloworld.HelloRequest
import kotlinx.coroutines.flow.Flow

class GreeterService : GreeterGrpcKt.GreeterCoroutineImplBase() {
    override suspend fun sayHello(request: HelloRequest): HelloReply {
        return super.sayHello(request)
    }

    override fun sayHelloStreaming(requests: Flow<HelloRequest>): Flow<HelloReply> {
        return super.sayHelloStreaming(requests)
    }

    override suspend fun sayHelloClientStreaming(requests: Flow<HelloRequest>): HelloReply {
        return super.sayHelloClientStreaming(requests)
    }

    override fun sayHelloServerStreaming(request: HelloRequest): Flow<HelloReply> {
        return super.sayHelloServerStreaming(request)
    }
}
