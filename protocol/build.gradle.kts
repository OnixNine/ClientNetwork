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

    testImplementation("org.junit.jupiter:junit-jupiter:5.10.2")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.shadowJar {
    relocate("io.netty", "dev.test.netty") {
        exclude("META-INF/**")
        exclude("module-info.class")
    }
    // nettyLib.jar bundles its own SLF4J (api + simple provider). Whoever uses
    // this jar already has SLF4J on the classpath (the server and, for the
    // Fabric test mod, Minecraft itself), and a second provider makes SLF4J
    // recurse and crash. Drop it from the fat jar.
    exclude("org/slf4j/**")
    exclude("META-INF/services/org.slf4j.spi.SLF4JServiceProvider")
}

tasks.test {
    useJUnitPlatform()
}

tasks.named("build") {
    dependsOn(tasks.shadowJar)
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
    if (JavaVersion.current().isJava10Compatible) {
        options.release.set(17)
    }
}
