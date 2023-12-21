import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    java
    id("com.github.johnrengelman.shadow")
}

repositories {
    maven { url = uri("https://papermc.io/repo/repository/maven-public/") }
    maven {
        name = "sonatype"
        url = uri("https://oss.sonatype.org/content/groups/public/")
    }
    maven { url = uri("https://hub.spigotmc.org/nexus/content/groups/public/") }
}

dependencies {
    implementation(project(":masuite-common")) {
        exclude("javax.persistence")
        exclude("com.j256.ormlite")
        exclude("mysql")
    }
    compileOnly("io.papermc.paper:paper-api:1.18.1-R0.1-SNAPSHOT")
    implementation("co.aikar:acf-paper:0.5.0-SNAPSHOT")

    implementation("net.kyori:adventure-platform-bukkit:4.3.2")
    implementation("net.kyori:adventure-text-minimessage:4.1.0-SNAPSHOT")
    implementation("org.spongepowered:configurate-yaml:4.1.2")

    compileOnly("org.projectlombok:lombok:1.18.24")
    annotationProcessor("org.projectlombok:lombok:1.18.24")
}

val buildVersion = "RC${(System.getenv("BUILD_NUMBER") ?: "0")}";
tasks.processResources {
    duplicatesStrategy = DuplicatesStrategy.INCLUDE
    expand("version" to "${parent!!.version}-${buildVersion}")
}

tasks.withType<JavaCompile> {
    options.compilerArgs.add("-parameters")
    options.isFork = true
    // options.forkOptions.executable = "javac"
    options.encoding = "UTF-8"
}

tasks.withType<ShadowJar> {
    val prefix = "dev.masa.masuite.libs."
    relocate("co.aikar.commands", prefix + "acf")
    relocate("co.aikar.locales", prefix + "locales")
    relocate("org.spongepowered", prefix + "spongepowered")
    relocate("net.kyori:adventure-text-minimessage", prefix + "kyori:adventure-text-minimessage")
    relocate("net.kyori:adventure-platform-bukkit", prefix + "adventure-platform-bukkit")
    archiveClassifier.set("")
}

tasks.build {
    dependsOn("shadowJar");
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(17))
}

artifacts {
    base.archivesName.set("${project.name}-${parent!!.version}-${buildVersion}")
}
