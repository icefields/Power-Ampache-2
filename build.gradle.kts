buildscript {
    extra.apply{
        set("compose_version", "1.5.4")
        set("composeNav_version", "1.8.42-beta")
        set("retrofit2_version", "2.9.0")
        set("coroutines_version", "1.7.3")
        set("lifecycle_version", "2.6.2")
        set("exoplayer_version", "2.19.1")
    }

    dependencies {
        classpath("com.google.dagger:hilt-android-gradle-plugin:2.48")
    }
}
// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.android.application") version "8.2.0" apply false
    id("com.google.dagger.hilt.android") version "2.48" apply false
    id("org.jetbrains.kotlin.android") version "1.9.20" apply false
    id("com.google.devtools.ksp") version "1.9.20-1.0.14" apply false

}

tasks.register("clean", Delete::class){
    delete(rootProject.buildDir)
}
