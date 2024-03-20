plugins {
    `java-library`
    id("com.github.johnrengelman.shadow")
}

repositories {
    maven(url = "https://oss.sonatype.org/content/repositories/snapshots/") {
        name = "sonatype-oss-snapshots"
    }
    mavenCentral()
}

dependencies {
    api(project(":masuite-api"))

    implementation("mysql:mysql-connector-java:8.0.29")

    compileOnly("org.projectlombok:lombok:1.18.32")
    annotationProcessor("org.projectlombok:lombok:1.18.32")
    implementation("com.j256.ormlite:ormlite-jdbc:6.1")
    implementation("javax.persistence:javax.persistence-api:2.2")

    implementation("org.spongepowered:configurate-yaml:4.1.2")
    compileOnly("com.google.code.gson:gson:2.9.0")

    compileOnly("net.kyori:adventure-api:4.9.3")
    compileOnly("net.kyori:adventure-text-minimessage:4.1.0-SNAPSHOT")
}

java {
    sourceCompatibility = JavaVersion.toVersion(17)
    targetCompatibility = JavaVersion.toVersion(17)
}

tasks.withType(AbstractArchiveTask::class).configureEach {
    isReproducibleFileOrder = true
    isPreserveFileTimestamps = false
}

tasks.withType<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar> {
    val prefix = "dev.masa.masuite.libs."

    relocate("com.j256.ormlite", prefix + "ormlite")
    relocate("javax.persistence", prefix + "persistence")
    relocate("org.spongepowered", prefix + "spongepowered")
    relocate("mysql", prefix + "mysql")
}

tasks.named("assemble").configure {
    dependsOn("shadowJar")
}

