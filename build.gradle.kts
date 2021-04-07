import org.jetbrains.compose.compose
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.4.31"
    id("org.jetbrains.compose") version "0.4.0-build178"
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
    implementation("com.arkivanov.decompose:decompose:0.2.1")
    implementation("com.arkivanov.decompose:extensions-compose-jetbrains:0.2.1")
}

tasks.withType<KotlinCompile>() {
    kotlinOptions.jvmTarget = "11"
}

compose.desktop {
    application {
        mainClass = "MainKt"
        nativeDistributions {
            //targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "ViewBinderWizard"
        }
    }
}