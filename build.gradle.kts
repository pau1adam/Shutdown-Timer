import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.9.0"
    id("org.jetbrains.compose") version "1.5.0"
}

group = "io.github.pau1adam"
version = "1.0"

repositories {
    google()
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "11"
}

tasks.jar {
    manifest {
        attributes["Main-Class"] = "MainKt"
    }
    configurations["compileClasspath"].forEach { file: File ->
        from(zipTree(file.absoluteFile))
    }
    duplicatesStrategy = DuplicatesStrategy.INCLUDE
}

compose.desktop {
    application {
        mainClass = "MainKt"
        nativeDistributions {
            targetFormats(TargetFormat.Exe, TargetFormat.Msi)
            packageName = "shutdown"
            packageVersion = "1.0.0"
        }
    }
}

dependencies {
    implementation(compose.desktop.currentOs)
    implementation("org.jetbrains.compose.ui:ui-tooling-preview:1.2.0-alpha01-dev602")
}