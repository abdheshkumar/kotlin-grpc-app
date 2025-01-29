import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "2.1.10"
}

allprojects {
    group = "com.example"
    version = "1.0-SNAPSHOT"
    repositories {
        mavenLocal()
        mavenCentral()
    }
}

subprojects {
    apply(plugin = "kotlin")
    apply(plugin = "application")
    apply(plugin = "maven-publish")
    java.sourceCompatibility = JavaVersion.VERSION_18
    java.targetCompatibility = JavaVersion.VERSION_18
    tasks.withType<KotlinCompile> {
        kotlinOptions {
            freeCompilerArgs = listOf("-Xjsr305=strict")
            jvmTarget = "18"
        }
    }

    tasks.withType<Test> {
        useJUnitPlatform()
    }

    tasks {
        jar {
            enabled = true
        }
    }
}
