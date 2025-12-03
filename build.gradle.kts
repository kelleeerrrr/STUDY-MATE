// Top-level build file where you can add configuration options common to all sub-projects/modules.

buildscript {
    repositories {
        google()      // Required for Android & Firebase plugins
        mavenCentral()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:8.3.1")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.9.10")
        classpath("com.google.gms:google-services:4.4.4") // Google Services plugin
    }
}

// Remove allprojects { repositories { ... } } completely
// Repositories are now managed only in settings.gradle.kts

// Optional: task to clean project
tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}
