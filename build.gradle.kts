plugins {
    alias(libs.plugins.kotlin.jvm)
    application
}

group = "com.barclays"
version = "0.1.0"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform(libs.junit.bom))
    testImplementation(libs.junit.jupiter)
    testImplementation(libs.kotest.assertions.core)
    testRuntimeOnly(libs.junit.platform.launcher)
}

kotlin {
    jvmToolchain(25)
}

application {
    mainClass = "com.barclays.martianrobots.cli.MainKt"
}

tasks.test {
    useJUnitPlatform()
    // Gradle only reports failing tests by default. Listing every test makes the
    // behaviour-named tests visible when a reviewer runs the build.
    testLogging {
        events("passed", "skipped", "failed")
    }
}

tasks.named<JavaExec>("run") {
    standardInput = System.`in`
}
