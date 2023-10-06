import com.google.protobuf.gradle.id
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val descFilePath = "$projectDir/build/descriptors/main.desc"
val kotlinVersion: String by project
val kotlinCoroutinesVersion: String by project
val protobufVersion: String by project
val googleApiVersion: String by project
val grpcVersion: String by project
val grpcKotlinVersion: String by project
val junitVersion: String by project
val krotoPlusVersion: String by project
val protoValidatorVersion: String by project
val kotestVersion: String by project

plugins {
    kotlin("jvm")
    `java-library`
    `maven-publish`
    id("com.google.protobuf")
}
group = "com.example"
version = "0.0.1-SNAPSHOT"
repositories {
    mavenLocal()
    mavenCentral()
}

dependencies {
    implementation("com.google.api.grpc:proto-google-common-protos:$googleApiVersion")
    implementation("io.grpc:grpc-netty-shaded:$grpcVersion")
    implementation("io.grpc:grpc-protobuf:$grpcVersion")
    implementation("io.grpc:grpc-stub:$grpcVersion")
    implementation("io.grpc:grpc-services:$grpcVersion")
    implementation("io.grpc:grpc-kotlin-stub:$grpcKotlinVersion")
    implementation("com.google.protobuf:protobuf-kotlin:$protobufVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$kotlinCoroutinesVersion")
    implementation("io.envoyproxy.protoc-gen-validate:pgv-java-stub:$protoValidatorVersion")
    compileOnly("javax.annotation:javax.annotation-api:1.3.2")
    testImplementation("org.junit.jupiter:junit-jupiter-api:$junitVersion")
    testImplementation("org.junit.jupiter:junit-jupiter-engine:$junitVersion")
    // testing
    testImplementation("io.kotest", "kotest-assertions-core-jvm", kotestVersion)
    testImplementation("io.kotest", "kotest-framework-datatest-jvm", kotestVersion)
    testImplementation("io.kotest", "kotest-property-jvm", kotestVersion)
    testImplementation("io.kotest", "kotest-runner-junit5-jvm", kotestVersion)
    testImplementation("io.kotest", "kotest-property-jvm", kotestVersion)
}
java {
    sourceCompatibility = JavaVersion.VERSION_18
    targetCompatibility = JavaVersion.VERSION_18
}
sourceSets {
    main {
        java {
            srcDir("${project.layout.buildDirectory}/generated/source/proto/main/grpc")
            srcDir("${project.layout.buildDirectory}/generated/source/proto/main/grpckt")
            srcDir("${project.layout.buildDirectory}/generated/source/proto/main/java")
            srcDir("${project.layout.buildDirectory}/generated/source/proto/main/kotlin")
            srcDir("${project.layout.buildDirectory}/generated/source/proto/main/java-pgv")
        }

        proto {
            srcDir("${project.projectDir}/src/protos")
        }
    }
}

protobuf {
    protoc {
        artifact = "com.google.protobuf:protoc:$protobufVersion"
    }
    plugins {
        id("grpc") {
            artifact = "io.grpc:protoc-gen-grpc-java:$grpcVersion"
        }
        id("grpckt") {
            artifact = "io.grpc:protoc-gen-grpc-kotlin:$grpcKotlinVersion:jdk8@jar"
        }
        id("java-pgv") {
            artifact = "io.envoyproxy.protoc-gen-validate:protoc-gen-validate:$protoValidatorVersion"
        }
    }
    generateProtoTasks {
        ofSourceSet("main").configureEach {
            plugins {
                id("grpc")
                id("grpckt")
                id("java-pgv") {
                    option("lang=java")
                }
            }
            builtins {
                id("kotlin")
            }
            generateDescriptorSet = true
            descriptorSetOptions.path = descFilePath
            descriptorSetOptions.includeImports = true
        }
    }
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = "com.example"
            artifactId = "business-proto"
            version = "0.0.1-SNAPSHOT"
            from(components["java"])
        }
    }
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
