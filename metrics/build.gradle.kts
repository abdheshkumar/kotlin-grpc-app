import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

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

    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

    implementation("com.sksamuel.hoplite:hoplite-core:2.7.5")
    implementation("com.sksamuel.hoplite:hoplite-hocon:2.7.0")
    implementation("com.sksamuel.hoplite:hoplite-yaml:2.7.5")

    testImplementation("io.kotest:kotest-runner-junit5-jvm:5.7.2")
    testImplementation("io.kotest:kotest-assertions-core-jvm:5.5.4")
    testImplementation("io.kotest:kotest-property-jvm:5.7.2")
    testImplementation("io.kotest:kotest-framework-datatest-jvm:5.5.4")
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
            artifactId = "metrics"
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
