buildscript {
    dependencies {
        classpath("com.google.dagger:hilt-android-gradle-plugin:2.51.1")
    }
}
// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.android.application") version "8.7.2" apply false
    id("com.google.dagger.hilt.android") version "2.51.1" apply false
    id("org.jetbrains.kotlin.android") version "2.0.21" apply false
    //id("com.google.devtools.ksp") version "1.9.22-1.0.17" apply false
    id("com.google.devtools.ksp") version "2.0.21-1.0.27" apply false
    id("org.jetbrains.kotlin.plugin.compose") version "2.0.21" apply false // this version matches your Kotlin version
}

tasks.register("clean", Delete::class){
    delete(rootProject.buildDir)
}
