plugins {
    kotlin("jvm") version "2.1.0"
    kotlin("plugin.serialization") version "2.1.0"
    application
}

group = "com.sales"
version = "0.1.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation("io.ktor:ktor-server-core:3.5.2")
    implementation("io.ktor:ktor-server-netty:3.5.2")
    implementation("io.ktor:ktor-server-content-negotiation:3.5.2")
    implementation("io.ktor:ktor-serialization-kotlinx-json:3.5.2")
    implementation("io.ktor:ktor-server-status-pages:3.5.2")
    implementation("ch.qos.logback:logback-classic:1.5.16")
    testImplementation("io.ktor:ktor-server-test-host:3.5.2")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
}

application {
    mainClass.set("com.sales.payment.ApplicationKt")
}

tasks.test {
    useJUnitPlatform()
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}
