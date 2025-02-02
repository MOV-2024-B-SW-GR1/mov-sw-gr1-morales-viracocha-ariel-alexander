plugins {
    kotlin("jvm") version "2.0.21"
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

// Configuración para alinear Kotlin y Java al target JVM 17
java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17)) // Asegura que Java 17 se use
    }
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions {
        jvmTarget = "17" // Configura Kotlin para usar Java 17
    }
}
