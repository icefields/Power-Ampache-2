plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("com.google.devtools.ksp")
    id("kotlin-kapt")
    id("dagger.hilt.android.plugin")
    id("kotlin-parcelize")
}

val retrofit2Version = "2.11.0"
val okhttpVersion = "5.0.0-alpha.14"
val coroutinesVersion = "1.8.1"
val hiltVersion = "1.2.0"
val roomVersion = "2.7.1"

android {
    namespace = "luci.sixsixsix.powerampache2"
    compileSdk = 35

    defaultConfig {
        minSdk = 28

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
}

val media3Version = "1.6.1"

dependencies {
    implementation(project(":domain"))

    implementation("androidx.core:core-ktx:1.16.0")


    // --- Dagger Hilt --- //
    implementation("com.google.dagger:hilt-android:2.51.1")
    kapt("com.google.dagger:hilt-android-compiler:2.51.1")
    kapt("androidx.hilt:hilt-compiler:$hiltVersion")
    implementation("androidx.hilt:hilt-navigation-compose:$hiltVersion")
    implementation("androidx.hilt:hilt-common:$hiltVersion")
    implementation("androidx.hilt:hilt-work:$hiltVersion")

    //implementation("androidx.hilt:hilt-lifecycle-viewmodel:1.0.0-alpha03")
    // --- Retrofit --- //
    implementation("com.squareup.retrofit2:retrofit:$retrofit2Version")
    //implementation("com.squareup.retrofit2:converter-moshi:$retrofit2Version")
    implementation("com.squareup.retrofit2:converter-gson:$retrofit2Version")
    implementation("com.squareup.okhttp3:okhttp:$okhttpVersion")
    implementation("com.squareup.okhttp3:logging-interceptor:$okhttpVersion")
    // JSON serialization
    implementation("com.google.code.gson:gson:2.11.0")

    // --- Room --- //
    implementation("androidx.room:room-runtime:$roomVersion")
    ksp("androidx.room:room-compiler:$roomVersion")
    // Kotlin Extensions and Coroutines support for Room
    implementation("androidx.room:room-ktx:$roomVersion")

    // ERROR REPORT
    implementation("ch.acra:acra-mail:5.11.3")

    implementation("androidx.media3:media3-exoplayer:$media3Version")
    // Common functionality for media database components
    implementation("androidx.media3:media3-database:$media3Version")
    // Common functionality for loading data
    implementation("androidx.media3:media3-datasource:$media3Version")
    // Common functionality used across multiple media libraries
    implementation("androidx.media3:media3-common:$media3Version")
    implementation("androidx.media3:media3-datasource-okhttp:$media3Version")
    implementation("androidx.media3:media3-session:$media3Version")

    implementation("androidx.work:work-runtime-ktx:2.10.1")

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
}