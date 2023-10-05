import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val grpcKotlinVersion: String by project
val kotlinVersion: String by project
val kotlinCoroutinesVersion: String by project
val grpcVersion: String by project

plugins {
    application
    kotlin("jvm")
    id("com.google.cloud.tools.jib") version "3.4.0"
}

java.sourceCompatibility = JavaVersion.VERSION_18

repositories {
    mavenLocal()
    mavenCentral()
}

dependencies {
    implementation(platform("io.arrow-kt:arrow-stack:1.1.5"))
    implementation("io.arrow-kt:arrow-core")
    implementation("io.arrow-kt:arrow-fx-coroutines")
    implementation("io.arrow-kt:arrow-fx-stm")
    implementation("io.arrow-kt:suspendapp:0.4.0")

    implementation("io.micrometer:micrometer-registry-prometheus:1.11.4")
    implementation("io.micrometer:micrometer-registry-jmx:1.11.4")
    implementation("com.be-hase.grpc-micrometer:grpc-micrometer:0.0.2")

    implementation("io.ktor:ktor-server-jvm:2.3.5")
    implementation("io.ktor:ktor-server-netty-jvm:2.3.4")
    implementation("io.ktor:ktor-serialization:2.3.5")
    implementation("io.ktor:ktor-serialization-kotlinx-json-jvm:2.2.4")
    implementation("io.ktor:ktor-server-metrics-micrometer-jvm:2.3.5")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.0")

    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:$kotlinVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$kotlinCoroutinesVersion")

    implementation("io.grpc:grpc-kotlin-stub:$grpcKotlinVersion")
    implementation("io.grpc:grpc-netty-shaded:$grpcVersion")
    implementation("io.grpc:grpc-protobuf:$grpcVersion")
    implementation("io.grpc:grpc-services:$grpcVersion")
    implementation("io.grpc:grpc-stub:$grpcVersion")

    implementation("com.sksamuel.hoplite:hoplite-core:2.7.5")
    implementation("com.sksamuel.hoplite:hoplite-hocon:2.7.2")
    implementation("com.sksamuel.hoplite:hoplite-yaml:2.7.2")
    implementation("org.apache.logging.log4j:log4j-core:2.20.0")
    implementation("org.apache.logging.log4j:log4j-slf4j-impl:2.20.0")
    implementation("org.apache.kafka:kafka-clients:3.6.0")
    implementation(project(":protobuf"))
    implementation(project(":grpc-server"))
    implementation(project(":http-admin"))
    implementation(project(":metrics"))
    // Test
    testImplementation("org.testcontainers:kafka:1.19.1")
    testImplementation("io.kotest:kotest-runner-junit5-jvm:5.5.5")
    testImplementation("io.kotest:kotest-assertions-core-jvm:5.5.5")
    testImplementation("io.kotest:kotest-property-jvm:5.5.5")
    testImplementation("io.kotest:kotest-framework-datatest-jvm:5.5.5")
    testImplementation("io.kotest.extensions:kotest-assertions-arrow-fx-coroutines-jvm:1.3.0")

}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "18"
    }
}

application {
    mainClass.set("com.abtech.app.MainKt")
    applicationDefaultJvmArgs = listOf()
}

tasks.withType<Test> {
    useJUnitPlatform()
}

jib {

    from {
        image = "kumorelease-docker-virtual.artylab.abtech.biz/library/eg-java/jre:19.0.22"
    }
    to {
        val dockerRegistry = properties["DOCKER_REGISTRY"]?.let { "$it/" } ?: ""
        image = "${dockerRegistry}eg-incentives/kotlin-grpc-app:$version"
    }
    container {
        jvmFlags =
            listOf(
                "-javaagent:/datadog/dd-java-agent.jar",
                "-XX:InitialRAMPercentage=75",
                "-XX:MaxRAMPercentage=75",
            )
        mainClass = "com.abtechgroup.app.MainKt"
        args = listOf()
        ports = listOf("8080/tcp")
        environment = mapOf()
    }
}
