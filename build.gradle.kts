import org.jetbrains.compose.compose
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    kotlin("jvm") version "1.4.32"
    id("org.jetbrains.compose") version "0.4.0-build180"
}

group = "ch8n.androidbites.viewbinder"
version = "1.0.1"

repositories {
    jcenter()
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
}

dependencies {
    implementation(compose.desktop.currentOs)
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.4.3")
    implementation(kotlin("reflect"))
    // Decompose : Decompose
    implementation("com.arkivanov.decompose:decompose:0.2.1")
    implementation("com.arkivanov.decompose:extensions-compose-jetbrains:0.2.1")

    // Logging
    implementation("com.ToxicBakery.logging:arbor-jvm:1.35.72")
}

tasks.withType<KotlinCompile>() {
    kotlinOptions.jvmTarget = "11"
}

compose.desktop {
    application {
        mainClass = "MainKt"
        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "ViewBinderWizard"
        }
    }
}