plugins {
    java
    id("com.gradleup.shadow") version "9.1.0"
}

group = "me.onixdev.ircchat"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(files("nettyLib.jar"))
}

tasks.shadowJar {
    relocate("io.netty", "dev.test.netty") {
        exclude("META-INF/**")
        exclude("module-info.class")
    }
}

tasks.named("build") {
    dependsOn(tasks.shadowJar)
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
    if (JavaVersion.current().isJava10Compatible) {
        options.release.set(21)
    }
}
