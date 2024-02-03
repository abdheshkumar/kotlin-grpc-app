import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val grpcKotlinVersion: String by project
val kotlinVersion: String by project
val kotlinCoroutinesVersion: String by project
val grpcVersion: String by project

plugins {
    kotlin("jvm")
    `maven-publish`
}

dependencies {
    implementation("io.arrow-kt:arrow-core:1.2.1")
    implementation("io.arrow-kt:arrow-fx-coroutines:1.2.1")

    implementation("io.micrometer:micrometer-registry-prometheus:1.12.2")
    implementation("io.micrometer:micrometer-registry-jmx:1.12.2")

    implementation("io.ktor:ktor-server-jvm:2.3.8")
    implementation("io.ktor:ktor-server-netty-jvm:2.3.7")
    implementation("io.ktor:ktor-serialization:2.3.8")
    implementation("io.ktor:ktor-serialization-kotlinx-json-jvm:2.3.8")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.2")
    implementation("io.ktor:ktor-server-metrics-micrometer-jvm:2.3.8")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.2")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$kotlinCoroutinesVersion")
    api(project(":metrics"))
    // Test
    testImplementation("org.testcontainers:kafka:1.19.4")
    testImplementation("io.kotest:kotest-runner-junit5-jvm:5.8.0")
    testImplementation("io.kotest:kotest-assertions-core-jvm:5.8.0")
    testImplementation("io.kotest:kotest-property-jvm:5.8.0")
    testImplementation("io.kotest:kotest-framework-datatest-jvm:5.8.0")
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = "com.example"
            artifactId = "http-admin"
            version = "0.0.1-SNAPSHOT"
            from(components["java"])
        }
    }
}