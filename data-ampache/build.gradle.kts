plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
    id("kotlin-kapt")
    id("kotlin-parcelize")
}

android {
    namespace = "luci.sixsixsix.powerampache2.data"
    compileSdk = 35

    defaultConfig {
        minSdk = 28

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")

        ksp {
            arg("room.schemaLocation", "$projectDir/schemas")
        }
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

dependencies {
    implementation(project(":domain"))
    implementation(project(":MrLog"))

    implementation(libs.androidx.core.ktx)

    // --- Dagger Hilt --- //
    implementation(libs.hilt.android)
    kapt(libs.hilt.android.compiler)
    kapt(libs.androidx.hilt.compiler)
    implementation(libs.androidx.hilt.navigation.compose)
    implementation(libs.androidx.hilt.common)
    implementation(libs.androidx.hilt.work)
    //implementation("androidx.hilt:hilt-lifecycle-viewmodel:1.0.0-alpha03")

    // --- Retrofit --- //
    implementation(libs.retrofit)
    implementation(libs.retrofit.converter.gson)
    implementation(libs.okhttp)
    implementation(libs.okhttp.logging.interceptor)
    //implementation("com.squareup.retrofit2:converter-moshi:$retrofit2Version")

    // JSON serialization
    implementation(libs.gson)

//    // --- Coil, image-loader --- //
    implementation(libs.coil.compose)

    // --- Room --- //
    implementation(libs.room.runtime)
    ksp(libs.room.compiler)
    // Kotlin Extensions and Coroutines support for Room
    implementation(libs.room.ktx)

//    // ERROR REPORT
//    implementation(libs.acra.mail)

//    implementation(libs.media3.exoplayer)
    // Common functionality for media database components
//    implementation(libs.media3.database)
    // Common functionality for loading data
//    implementation(libs.media3.datasource)
    // Common functionality used across multiple media libraries
//    implementation(libs.media3.common)
    implementation(libs.media3.datasource.okhttp)
//    implementation(libs.media3.session)

//    implementation(libs.androidx.work.runtime.ktx)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}
