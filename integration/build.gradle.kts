import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val grpcKotlinVersion: String by project
val grpcVersion: String by project
val kotlinVersion: String by project

plugins {
    kotlin("jvm")
    `maven-publish`
}

dependencies {
    implementation("io.arrow-kt:arrow-core:1.2.1")
    implementation("io.arrow-kt:arrow-fx-coroutines:1.2.1")

    implementation("io.micrometer:micrometer-registry-prometheus:1.11.5")
    implementation("io.micrometer:micrometer-registry-jmx:1.11.5")
    implementation(project(":grpc-app"))
    implementation(project(":protobuf"))
    implementation("io.grpc:grpc-kotlin-stub:$grpcKotlinVersion")
    implementation("io.grpc:grpc-netty-shaded:$grpcVersion")
    implementation("io.grpc:grpc-protobuf:$grpcVersion")
    implementation("io.grpc:grpc-services:$grpcVersion")
    implementation("io.grpc:grpc-stub:$grpcVersion")

    // Test
    testImplementation("org.testcontainers:kafka:1.19.1")
    testImplementation("io.kotest:kotest-runner-junit5-jvm:5.7.2")
    testImplementation("io.kotest:kotest-assertions-core-jvm:5.8.0")
    testImplementation("io.kotest:kotest-property-jvm:5.8.0")
    testImplementation("io.kotest:kotest-framework-datatest-jvm:5.8.0")
    testImplementation("io.kotest.extensions:kotest-assertions-arrow-fx-coroutines-jvm:1.4.0")
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = "com.example"
            artifactId = "integration"
            version = "0.0.1-SNAPSHOT"
            from(components["java"])
        }
    }
}
