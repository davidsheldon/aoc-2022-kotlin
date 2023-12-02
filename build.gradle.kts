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

dependencies {
    // Other dependencies.
    testImplementation(kotlin("test"))
    testImplementation("org.assertj:assertj-core:3.23.1")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.14.+")


}