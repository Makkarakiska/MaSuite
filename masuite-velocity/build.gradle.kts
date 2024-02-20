import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    `java-library`
    id("com.github.johnrengelman.shadow")
}

repositories {
    maven { url = uri("https://papermc.io/repo/repository/maven-public/") }
}

dependencies {
    implementation(project(":masuite-common"))

    compileOnly("com.velocitypowered:velocity-api:3.1.1")
    annotationProcessor("com.velocitypowered:velocity-api:3.1.1")

    compileOnly("org.projectlombok:lombok:1.18.24")
    annotationProcessor("org.projectlombok:lombok:1.18.24")

    implementation("org.spongepowered:configurate-yaml:4.1.2")
    implementation("net.kyori:adventure-text-minimessage:4.16.0")
}

tasks.withType(AbstractArchiveTask::class).configureEach {
    isReproducibleFileOrder = true
    isPreserveFileTimestamps = false
}

tasks.withType<ShadowJar> {
    val prefix = "dev.masa.masuite.libs."
    relocate("org.spongepowered", prefix + "spongepowered")
    relocate("net.kyori:adventure-text-minimessage", prefix + "kyori:adventure-text-minimessage")
    archiveClassifier.set("")
}



tasks.named("assemble").configure {
    dependsOn("shadowJar")
}

val buildVersion = "RC${(System.getenv("BUILD_NUMBER") ?: "0")}";

tasks.processResources {
    duplicatesStrategy = DuplicatesStrategy.INCLUDE
    expand("version" to "${parent!!.version}-${buildVersion}");
}

artifacts {
    base.archivesName.set("${project.name}-${parent!!.version}-${buildVersion}")
}