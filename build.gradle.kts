plugins {
    kotlin("jvm") version "1.9.20"
}

repositories {
    mavenCentral()
}


tasks {
    test {
        useJUnitPlatform()
    }
    wrapper {
        gradleVersion = "7.5.1"
    }
}
tasks
    .withType<org.jetbrains.kotlin.gradle.tasks.KotlinJvmCompile>()
    .configureEach {
        compilerOptions
            .languageVersion
            .set(
                org.jetbrains.kotlin.gradle.dsl.KotlinVersion.KOTLIN_2_0
            )
    }

dependencies {
    // Other dependencies.
    testImplementation(kotlin("test"))
    testImplementation("org.assertj:assertj-core:3.23.1")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.14.+")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.0-RC")
    implementation("org.jetbrains.bio:viktor:1.1.0")
}