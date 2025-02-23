buildscript {
    repositories {
        mavenCentral()
        gradlePluginPortal()
        maven {
            url = uri("https://plugins.gradle.org/m2/")

        }
        maven {
            url = uri("https://jitpack.io")
        }
    }
    dependencies {
        classpath("com.android.tools.build:gradle:8.0.0")
        classpath("com.github.CodingGay:BlackObfuscator-ASPlugin:3.9")

    }
}
// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.android.application") version "8.2.2" apply false
    id("com.google.dagger.hilt.android") version "2.50" apply false
    id("org.jetbrains.kotlin.android") version "1.9.0" apply false
    id("com.google.devtools.ksp") version "1.8.10-1.0.9" apply false
    id("com.google.gms.google-services") version "4.4.1" apply false
}