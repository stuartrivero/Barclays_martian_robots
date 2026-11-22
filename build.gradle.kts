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
    testImplementation(kotlin("test"))
    testImplementation(platform(libs.junit.bom))
    testImplementation(libs.junit.jupiter)
    testRuntimeOnly(libs.junit.platform.launcher)
}

kotlin {
    jvmToolchain(21)
}

application {
    mainClass = "com.barclays.martianrobots.AppKt"
}

tasks.test {
    useJUnitPlatform()
}
