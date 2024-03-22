plugins {
    java
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

group = "dev.masa.masuite"
version = "4.0.0-SNAPSHOT"

java {
    sourceCompatibility = JavaVersion.toVersion(17)
    targetCompatibility = JavaVersion.toVersion(17)
}

tasks.wrapper {
    gradleVersion = "8.7"
}

allprojects {

    group = project.group
    version = project.version

    repositories {
        mavenLocal()
        mavenCentral()
    }
}
