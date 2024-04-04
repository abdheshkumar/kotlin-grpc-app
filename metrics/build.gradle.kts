import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
val kotlinVersion: String by project

plugins {
    kotlin("jvm")
    `maven-publish`
}


dependencies {
    implementation("io.arrow-kt:arrow-core:1.2.4")
    implementation("io.arrow-kt:arrow-fx-coroutines:1.2.4")

    implementation("io.micrometer:micrometer-registry-prometheus:1.12.4")
    implementation("io.micrometer:micrometer-registry-jmx:1.12.4")
    implementation("com.sksamuel.hoplite:hoplite-core:2.7.5")
    implementation("com.sksamuel.hoplite:hoplite-hocon:2.7.5")
    implementation("com.sksamuel.hoplite:hoplite-yaml:2.7.5")

    testImplementation("io.kotest:kotest-runner-junit5-jvm:5.8.1")
    testImplementation("io.kotest:kotest-assertions-core-jvm:5.8.1")
    testImplementation("io.kotest:kotest-property-jvm:5.8.1")
    testImplementation("io.kotest:kotest-framework-datatest-jvm:5.8.1")
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = "com.example"
            artifactId = "metrics"
            version = "0.0.1-SNAPSHOT"
            from(components["java"])
        }
    }
}
