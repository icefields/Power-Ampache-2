buildscript {
    dependencies {
        classpath(libs.hilt.android.gradle.plugin)
    }
}
// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.hilt) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.kotlin.compose) apply false
}
allprojects {
    // This moves the "build" directory for all modules to /tmp
    // It prevents "Path too long" errors on Ext4 partitions
    layout.buildDirectory.set(file("/tmp/powerampache-build/${project.name}"))
}
tasks.register("clean", Delete::class){
    delete(rootProject.buildDir)
}
