import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val grpcKotlinVersion: String by project
val kotlinVersion: String by project
val kotlinCoroutinesVersion: String by project
val grpcVersion: String by project

plugins {
    kotlin("jvm")
    `maven-publish`
}

java.sourceCompatibility = JavaVersion.VERSION_18

repositories {
    mavenLocal()
    mavenCentral()
}

dependencies {
    implementation(platform("io.arrow-kt:arrow-stack:1.2.1"))
    implementation("io.arrow-kt:arrow-core")
    implementation("io.arrow-kt:arrow-fx-coroutines")

    implementation("io.micrometer:micrometer-registry-prometheus:1.11.4")
    implementation("io.micrometer:micrometer-registry-jmx:1.11.4")

    implementation("io.ktor:ktor-server-jvm:2.3.5")
    implementation("io.ktor:ktor-server-netty-jvm:2.3.5")
    implementation("io.ktor:ktor-serialization:2.3.5")
    implementation("io.ktor:ktor-serialization-kotlinx-json-jvm:2.3.5")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.0")
    implementation("io.ktor:ktor-server-metrics-micrometer-jvm:2.3.5")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.0")

    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:$kotlinVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$kotlinCoroutinesVersion")
    api(project(":metrics"))
    // Test
    testImplementation("org.testcontainers:kafka:1.19.1")
    testImplementation("io.kotest:kotest-runner-junit5-jvm:5.7.2")
    testImplementation("io.kotest:kotest-assertions-core-jvm:5.5.5")
    testImplementation("io.kotest:kotest-property-jvm:5.7.2")
    testImplementation("io.kotest:kotest-framework-datatest-jvm:5.5.5")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "18"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
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

tasks {
    jar {
        enabled = true
    }
}
