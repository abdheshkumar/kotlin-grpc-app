package com.abtech.app.grpc.api

import com.helloworld.v1.HelloWorldAPIGrpcKt
import com.helloworld.v1.HelloWorldRequest
import com.helloworld.v1.HelloWorldResponse
import com.helloworld.v1.helloWorldResponse

class HelloWorldService : HelloWorldAPIGrpcKt.HelloWorldAPICoroutineImplBase() {
    override suspend fun helloWorld(request: HelloWorldRequest): HelloWorldResponse {
        return helloWorldResponse {
            helloWorldString = "helloWorld response: ${request.id}"
        }
    }
}
