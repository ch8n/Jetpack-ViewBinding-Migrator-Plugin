import org.jetbrains.compose.compose
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    kotlin("jvm")
    id("org.jetbrains.compose")

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
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:_")
    implementation(kotlin("reflect"))
    // Decompose : Decompose
    implementation("com.arkivanov.decompose:decompose:_")
    implementation("com.arkivanov.decompose:extensions-compose-jetbrains:_")

    // Logging
    implementation("com.ToxicBakery.logging:arbor-jvm:_")
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