plugins {
    kotlin("jvm") version "1.7.21"
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

}