plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    id("kotlin-parcelize")
}

android {
    namespace = "luci.sixsixsix.powerampache2.domain"
    compileSdk = 35

    defaultConfig {
        minSdk = 28

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        debug {
            isMinifyEnabled = false
        }
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
    implementation(project(":MrLog"))

    implementation(libs.androidx.core.ktx)
    implementation(libs.gson)
    implementation(libs.media3.common) // TODO: this is is not supposed to be in the domain
    implementation(libs.androidx.runtime.livedata)
    // --- Coil, image-loader --- //
    implementation(libs.coil.compose) // TODO: this is is not supposed to be in the domain

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
}
