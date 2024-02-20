plugins {
    java
}

repositories {
    mavenCentral()
    maven(url = "https://oss.sonatype.org/content/repositories/snapshots/") {
        name = "sonatype-oss-snapshots"
    }
}

dependencies {
    compileOnly("net.kyori:adventure-api:4.9.3")
    compileOnly("net.kyori:adventure-text-minimessage:4.16.0")
}