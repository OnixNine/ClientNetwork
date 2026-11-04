plugins {
    java
    id("fabric-loom") version "1.17.21"
}

group = "me.onixdev.ircchat"
version = "1.0-SNAPSHOT"

base {
    archivesName.set("ircchat-test")
}

val protocolJar = rootProject.layout.projectDirectory
    .file("protocol/build/libs/protocol-1.0-SNAPSHOT-all.jar")

repositories {
    mavenCentral()
    maven { url = uri("https://maven.fabricmc.net/") }
    maven { url = uri("https://jitpack.io") }
}

dependencies {
    minecraft("com.mojang:minecraft:1.20.5")
    mappings("net.fabricmc:yarn:1.20.5+build.1:v2")
    modImplementation("net.fabricmc:fabric-loader:0.16.14")
    modImplementation("net.fabricmc.fabric-api:fabric-api:0.97.8+1.20.5")

    // Net / IRC protocol library (netty inside is relocated to dev.test.netty,
    // so it does not clash with the netty bundled by Minecraft).
    implementation(files(protocolJar))
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
    options.release.set(21)
}

// The protocol fat-jar has to exist before we compile the mod.
tasks.named("compileJava") {
    dependsOn(":protocol:shadowJar")
}

// Bundle the protocol (+ relocated netty) classes straight into the mod jar.
tasks.named<Jar>("jar") {
    dependsOn(":protocol:shadowJar")
    from(zipTree(protocolJar)) {
        exclude("META-INF/**")
        exclude("module-info.class")
    }
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}
