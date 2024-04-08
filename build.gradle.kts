buildscript {
//    extra.apply{
//        set("compose_version", "1.6.0")
//        set("composeNav_version", "1.8.42-beta")
//        set("retrofit2_version", "2.9.0")
//        set("coroutines_version", "1.7.3")
//        //set("lifecycle_version", "2.6.2")
//        set("exoplayer_version", "2.19.1")
//    }

    dependencies {
        classpath("com.google.dagger:hilt-android-gradle-plugin:2.50")
    }
}
// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.android.application") version "8.3.1" apply false
    id("com.google.dagger.hilt.android") version "2.50" apply false
    id("org.jetbrains.kotlin.android") version "1.9.22" apply false
    id("com.google.devtools.ksp") version "1.9.22-1.0.17" apply false

}

tasks.register("clean", Delete::class){
    delete(rootProject.buildDir)
}
