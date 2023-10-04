pluginManagement {
    val kotlinVersion: String by settings
    val protobufPluginVersion: String by settings
    plugins {
        kotlin("jvm") version kotlinVersion apply false
        id("com.google.protobuf") version protobufPluginVersion apply false
    }
}
rootProject.name = "kotlin-grpc-app"
include(":metrics")
include(":grpc-app")
include(":http-admin")
include(":grpc-server")
include(":integration")
include("protobuf")
