plugins {
    java
    id("org.jetbrains.kotlin.jvm") version "2.2.21"
    id("com.gradleup.shadow") version "9.1.0"
}

group = "me.onixdev.ircchat"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven { url = uri("https://jitpack.io") }
}

dependencies {
    implementation(project(":protocol"))
    implementation("org.json:json:20250517")
    implementation("dev.whyoleg.cryptography:cryptography-core:0.5.0")
    implementation("io.github.oshai:kotlin-logging-jvm:5.1.0")
    implementation("dev.whyoleg.cryptography:cryptography-provider-optimal:0.5.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.9.0")
    implementation("org.slf4j:slf4j-simple:2.0.3")
    implementation("com.github.groundbreakingmc:MyLib:e2865d2")
    implementation("org.xerial:sqlite-jdbc:3.51.2.0")
    implementation("com.github.OnixNine:OnixEvents:c54a0de95a")
    implementation(files("nettyLib.jar"))
}

kotlin {
    jvmToolchain(21)
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"

    if (JavaVersion.current().isJava10Compatible) {
        options.release.set(21)
    }
}

tasks.shadowJar {
    archiveClassifier.set("")
    relocate("io.netty", "dev.test.netty") {
        exclude("META-INF/**")
        exclude("module-info.class")
    }
    manifest {
        attributes(
            "Main-Class" to "me.onixdev.ircchat.Main"
        )
    }
}

tasks.named("build") {
    dependsOn(tasks.shadowJar)
}
