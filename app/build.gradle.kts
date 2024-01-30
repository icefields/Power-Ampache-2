import java.util.Properties

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("kotlin-kapt")
    id("dagger.hilt.android.plugin")
    id("kotlin-parcelize")
    id("com.google.devtools.ksp")
}

val composeVersion = "1.5.4" // rootProject.extra.get("compose_version") as String
val lifecycleVersion = "2.7.0"
val retrofit2Version = "2.9.0"
val coroutinesVersion = "1.7.3"
val exoplayerVersion = "2.19.1"
val composeNavVersion = "1.8.42-beta"
val media3Version = "1.2.1"
val hiltVersion = "1.1.0"

val localProperties = Properties()
localProperties.load(project.rootProject.file("local.properties").inputStream())

android {
    namespace = "luci.sixsixsix.powerampache2"
    compileSdk = 34

    val properties = Properties()
    properties.load(project.rootProject.file("secrets.properties").inputStream())
    val apikey = properties.getProperty("API_KEY")
    val ampacheUser = properties.getProperty("AMPACHE_USER")
    val ampachePass = properties.getProperty("AMPACHE_PASSWORD")
    val ampacheUrl = properties.getProperty("AMPACHE_URL")
    val dogmazicPass = properties.getProperty("DOGMAZIC_PASSWORD")
    val dogmazicUser = properties.getProperty("DOGMAZIC_USER")

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
            buildConfigField("String", "AMPACHE_USER", ampacheUser)
            buildConfigField("String", "AMPACHE_PASSWORD", ampachePass)
            buildConfigField("String", "AMPACHE_URL", ampacheUrl)
            buildConfigField("String", "API_KEY", apikey)
            buildConfigField("String", "DOGMAZIC_PASSWORD", dogmazicPass)
            buildConfigField("String", "DOGMAZIC_USER", dogmazicUser)
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
        kotlinCompilerExtensionVersion = "1.5.8"
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:$lifecycleVersion")
    implementation("androidx.activity:activity-compose:1.8.2")
    implementation("androidx.media:media:1.7.0")
    implementation("androidx.work:work-runtime-ktx:2.9.0")
    implementation("androidx.compose.runtime:runtime-livedata:$composeVersion")

    // --- Compose --- //
    implementation("androidx.compose.ui:ui:$composeVersion")
    implementation("androidx.compose.material:material-icons-extended:$composeVersion")
    implementation("androidx.compose.ui:ui-tooling-preview:$composeVersion")
    implementation("androidx.compose.material3:material3:1.1.2")
    implementation("androidx.compose.ui:ui-graphics:$composeVersion")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:$lifecycleVersion")
    implementation("com.google.accompanist:accompanist-flowlayout:0.17.0")
    implementation("androidx.paging:paging-compose:3.3.0-alpha02")
    implementation("androidx.activity:activity-compose:1.8.2")
    implementation("com.google.accompanist:accompanist-swiperefresh:0.24.2-alpha")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:$lifecycleVersion")
    // DO NOT INCLUDE implementation("androidx.compose.material:material:$composeVersion")

    // --- Compose Nav-Destinations --- //
    // Version with animations
    implementation("io.github.raamcosta.compose-destinations:animations-core:$composeNavVersion")
    // version with no animations
    // implementation("io.github.raamcosta.compose-destinations:core:$composeNavVersion")
    ksp("io.github.raamcosta.compose-destinations:ksp:$composeNavVersion")

    // COROUTINES
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutinesVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:$coroutinesVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:$coroutinesVersion")

    // --- ExoPlayer --- //
    implementation("androidx.media3:media3-exoplayer:$media3Version")
    // HLS playback support with ExoPlayer
    implementation("androidx.media3:media3-exoplayer-hls:$media3Version")
    // DASH playback support with ExoPlayer
    // implementation("androidx.media3:media3-exoplayer-dash:$media3Version")
    // RTSP playback support with ExoPlayer
    // implementation("androidx.media3:media3-exoplayer-rtsp:$media3Version")
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

    // --- Coil --- //
    implementation("io.coil-kt:coil-compose:2.4.0")

    // --- Dagger Hilt --- //
    implementation("com.google.dagger:hilt-android:2.50")
    kapt("com.google.dagger:hilt-android-compiler:2.50")
    kapt("androidx.hilt:hilt-compiler:$hiltVersion")
    implementation("androidx.hilt:hilt-navigation-compose:$hiltVersion")
    implementation("androidx.hilt:hilt-common:$hiltVersion")
    implementation("androidx.hilt:hilt-work:$hiltVersion")

    //implementation("androidx.hilt:hilt-lifecycle-viewmodel:1.0.0-alpha03")

    // --- Retrofit --- //
    implementation("com.squareup.retrofit2:retrofit:$retrofit2Version")
    //implementation("com.squareup.retrofit2:converter-moshi:$retrofit2Version")
    implementation("com.squareup.retrofit2:converter-gson:$retrofit2Version")
    implementation("com.squareup.okhttp3:okhttp:5.0.0-alpha.9")
    implementation("com.squareup.okhttp3:logging-interceptor:4.10.0")
    // JSON serialization
    implementation("com.google.code.gson:gson:2.10")

    // --- Room --- //
    implementation("androidx.room:room-runtime:2.6.1")
    ksp("androidx.room:room-compiler:2.6.1")
    // Kotlin Extensions and Coroutines support for Room
    implementation("androidx.room:room-ktx:2.6.1")

    // ERROR REPORT
    implementation("ch.acra:acra-mail:5.11.3")

    // --- TESTING --- //
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.compose.ui:ui-test-junit4:$composeVersion")
    androidTestImplementation(platform("androidx.compose:compose-bom:2023.08.00"))
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:2.0.4")

    debugImplementation("androidx.compose.ui:ui-test-manifest")
    debugImplementation("androidx.compose.ui:ui-tooling:$composeVersion")
}
