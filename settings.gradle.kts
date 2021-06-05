pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
        maven { url = uri("https://maven.pkg.jetbrains.space/public/p/compose/dev") }
        maven("https://s01.oss.sonatype.org/content/repositories/snapshots")
    }
    
}

plugins {
    // See https://jmfayard.github.io/refreshVersions
    id("de.fayard.refreshVersions") version "0.10.1-SNAPSHOT"
}
rootProject.name = "ViewBinderWizard"

