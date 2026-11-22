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
    mainClass = "com.barclays.martianrobots.AppKt"
}

tasks.test {
    useJUnitPlatform()
}
