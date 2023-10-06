import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val grpcKotlinVersion: String by project
val grpcVersion: String by project
plugins {
    kotlin("jvm")
    `maven-publish`
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation("io.arrow-kt:arrow-core:1.1.5")
    implementation("io.arrow-kt:arrow-fx-coroutines:1.2.1")
    implementation("io.micrometer:micrometer-core:1.11.4")
    implementation("com.be-hase.grpc-micrometer:grpc-micrometer:0.0.2")
    implementation("io.grpc:grpc-kotlin-stub:$grpcKotlinVersion")
    implementation("io.grpc:grpc-netty-shaded:$grpcVersion")
    implementation("io.grpc:grpc-protobuf:$grpcVersion")
    implementation("io.grpc:grpc-services:$grpcVersion")
    implementation("io.grpc:grpc-stub:$grpcVersion")
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = "com.example"
            artifactId = "grpc-server"
            version = "0.0.1-SNAPSHOT"
            from(components["java"])
        }
    }
}