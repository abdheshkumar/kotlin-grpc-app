pluginManagement {
    val kotlinVersion: String by settings
    val protobufPluginVersion: String by settings
    plugins {
        application
        kotlin("jvm")
        id("com.google.protobuf") version protobufPluginVersion
    }
}
rootProject.name = "kotlin-grpc-app"
include(":metrics")
include(":grpc-app")
include(":http-admin")
include(":grpc-server")
include(":integration")
include("protobuf")
