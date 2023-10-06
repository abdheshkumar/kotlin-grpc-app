package e2e.com.abtech

import com.abtech.app.Environment
import com.helloworld.v1.HelloWorldAPIGrpcKt
import com.helloworld.v1.HelloWorldRequest
import io.kotest.assertions.arrow.fx.coroutines.ResourceExtension
import io.kotest.core.extensions.install
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe

class ApplicationTest : FreeSpec() {
    init {
        val app = install(ResourceExtension(testApplication()))
        "It should work" {
            val client = HelloWorldAPIGrpcKt.HelloWorldAPICoroutineStub(app.get().channel)
            val response = client.helloWorld(HelloWorldRequest.newBuilder().setId("test-id").build())
            response.helloWorldString shouldBe "helloWorld response: test-id"
        }
    }
}
