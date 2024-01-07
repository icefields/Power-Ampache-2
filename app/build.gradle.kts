import java.util.Properties

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("kotlin-kapt")
    id("dagger.hilt.android.plugin")
    id("kotlin-parcelize")
    id("com.google.devtools.ksp")
}

val composeVersion = rootProject.extra.get("compose_version") as String
val retrofit2Version = rootProject.extra.get("retrofit2_version") as String
val coroutinesVersion = rootProject.extra.get("coroutines_version") as String
val exoplayerVersion = rootProject.extra.get("exoplayer_version") as String
val composeNavVersion = rootProject.extra.get("composeNav_version") as String

val media3Version = "1.2.0"

val localProperties = Properties()
localProperties.load(project.rootProject.file("local.properties").inputStream())

android {
    namespace = "luci.sixsixsix.powerampache2"
    compileSdk = 34

    defaultConfig {
        applicationId = "luci.sixsixsix.powerampache2"
        minSdk = 28
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        debug {
            buildConfigField("boolean", "MRLOG_ON", "true")
            //buildConfigField("String", "AMPACHE_USER", AMPACHE_USER)
        }
        release {
            buildConfigField("boolean", "MRLOG_ON", "false")
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        isCoreLibraryDesugaringEnabled = true
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = composeVersion
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.2")
    implementation("androidx.activity:activity-compose:1.8.2")

    implementation("androidx.media:media:1.7.0")

    // Compose dependencies
    implementation("androidx.compose.ui:ui:$composeVersion")
    implementation("androidx.compose.material:material-icons-extended:1.5.4")
    implementation("androidx.compose.ui:ui-tooling-preview:$composeVersion")
    implementation("androidx.compose.material3:material3:1.1.2")
    implementation("androidx.compose.ui:ui-graphics:$composeVersion")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.6.2")
    implementation("com.google.accompanist:accompanist-flowlayout:0.17.0")
    implementation("androidx.paging:paging-compose:3.3.0-alpha02")
    implementation("androidx.activity:activity-compose:1.8.2")
    implementation("com.google.accompanist:accompanist-swiperefresh:0.24.2-alpha")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.6.2")
    // DO NOT INCLUDE implementation("androidx.compose.material:material:$composeVersion")

    // Compose Nav Destinations
    // implementation("io.github.raamcosta.compose-destinations:core:$composeNavVersion")
    // Version with animations
    implementation("io.github.raamcosta.compose-destinations:animations-core:$composeNavVersion")
    ksp("io.github.raamcosta.compose-destinations:ksp:$composeNavVersion")

    // coroutines in Android
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutinesVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:$coroutinesVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:$coroutinesVersion")

    // ExoPlayer
    implementation("androidx.media3:media3-exoplayer:$media3Version")
    // DASH playback support with ExoPlayer
    implementation("androidx.media3:media3-exoplayer-dash:$media3Version")
    // HLS playback support with ExoPlayer
    implementation("androidx.media3:media3-exoplayer-hls:$media3Version")
    // RTSP playback support with ExoPlayer
    implementation("androidx.media3:media3-exoplayer-rtsp:$media3Version")

    // Common functionality for media database components
    implementation("androidx.media3:media3-database:$media3Version")
    // Common functionality for media decoders
    implementation("androidx.media3:media3-decoder:$media3Version")
    // Common functionality for loading data
    implementation("androidx.media3:media3-datasource:$media3Version")
    // Common functionality used across multiple media libraries
    implementation("androidx.media3:media3-common:$media3Version")
    implementation("androidx.media3:media3-session:$media3Version")
    implementation("androidx.media3:media3-ui:$media3Version")

    // Coil
    implementation("io.coil-kt:coil-compose:2.4.0")

    //Dagger - Hilt
    implementation("com.google.dagger:hilt-android:2.48")
    kapt("com.google.dagger:hilt-android-compiler:2.48")
    kapt("androidx.hilt:hilt-compiler:1.1.0")
    implementation("androidx.hilt:hilt-navigation-compose:1.1.0")
    //implementation("androidx.hilt:hilt-lifecycle-viewmodel:1.0.0-alpha03")

    // Retrofit
    implementation("com.squareup.retrofit2:retrofit:$retrofit2Version")
    //implementation("com.squareup.retrofit2:converter-moshi:$retrofit2Version")
    implementation("com.squareup.retrofit2:converter-gson:$retrofit2Version")
    implementation("com.squareup.okhttp3:okhttp:5.0.0-alpha.9")
    implementation("com.squareup.okhttp3:logging-interceptor:4.10.0")
    // JSON serialization
    implementation("com.google.code.gson:gson:2.10")

    // Room
    implementation("androidx.room:room-runtime:2.6.1")
    ksp("androidx.room:room-compiler:2.6.1")
    // Kotlin Extensions and Coroutines support for Room
    implementation("androidx.room:room-ktx:2.6.1")

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.compose.ui:ui-test-junit4:$composeVersion")
    androidTestImplementation(platform("androidx.compose:compose-bom:2023.08.00"))
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:2.0.4")

    debugImplementation("androidx.compose.ui:ui-test-manifest")
    debugImplementation("androidx.compose.ui:ui-tooling:$composeVersion")
}
