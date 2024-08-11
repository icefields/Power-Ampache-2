import java.util.Properties

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("kotlin-kapt")
    id("dagger.hilt.android.plugin")
    id("kotlin-parcelize")
    id("com.google.devtools.ksp")
}

val composeVersion = "1.6.8" // rootProject.extra.get("compose_version") as String
val lifecycleVersion = "2.8.4"
val retrofit2Version = "2.9.0"
val coroutinesVersion = "1.7.3"
val exoplayerVersion = "2.19.1"
val composeNavVersion = "1.8.42-beta"
val media3Version = "1.4.0"
val hiltVersion = "1.2.0"

val localProperties = Properties()
localProperties.load(project.rootProject.file("local.properties").inputStream())

android {
    namespace = "luci.sixsixsix.powerampache2"
    compileSdk = 34

    val properties = Properties()
    val propertiesFile = project.rootProject.file("secrets.properties")
    if (propertiesFile.exists()) {
        properties.load(propertiesFile.inputStream())
    } else {
        properties.load(project.rootProject.file("secretsnot.properties").inputStream())
    }
    val apikey = properties.getProperty("API_KEY")
    val ampacheUser = properties.getProperty("AMPACHE_USER")
    val ampachePass = properties.getProperty("AMPACHE_PASSWORD")
    val ampacheUrl = properties.getProperty("AMPACHE_URL")
    val ampacheUrlLocal = properties.getProperty("LOCAL_STABLE_URL")
    val dogmazicUrl = properties.getProperty("DOGMAZIC_URL")
    val dogmazicPass = properties.getProperty("DOGMAZIC_PASSWORD")
    val dogmazicToken = properties.getProperty("DOGMAZIC_TOKEN")
    val dogmazicUser = properties.getProperty("DOGMAZIC_USER")
    val dogmazicEmail = properties.getProperty("DOGMAZIC_EMAIL")
    val errorLogUrl = properties.getProperty("URL_ERROR_LOG")
    val localDevUser = properties.getProperty("LOCAL_DEV_USER")
    val localDevPass = properties.getProperty("LOCAL_DEV_PASSWORD")
    val localDevUrl = properties.getProperty("LOCAL_DEVELOPMENT_URL")
    val errorReportEmail = properties.getProperty("ERROR_REPORT_EMAIL")
    val pastebinApiKey = properties.getProperty("PASTEBIN_API_KEY")
    val localNextcloudUser = properties.getProperty("LOCAL_NEXTCLOUD_USER")
    val localNextcloudPass = properties.getProperty("LOCAL_NEXTCLOUD_PASSWORD")
    val localNextcloudUrl = properties.getProperty("LOCAL_NEXTCLOUD_URL")

    defaultConfig {
        applicationId = "luci.sixsixsix.powerampache2"
        minSdk = 28
        targetSdk = 34
        versionCode = 67
        versionName = "1.00-65" + "-AAOS"
        val versionQuote = "This version is powered by a single prime number multiplied by itself five times"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        vectorDrawables {
            useSupportLibrary = true
        }

        ksp {
            arg("room.schemaLocation", "$projectDir/schemas")
        }

        buildConfigField("String", "VERSION_QUOTE", "\"$versionQuote\"")
        buildConfigField("String", "ERROR_REPORT_EMAIL", errorReportEmail)
        buildConfigField("String", "LOCAL_NEXTCLOUD_USER", localNextcloudUser)
        buildConfigField("String", "LOCAL_NEXTCLOUD_PASSWORD", localNextcloudPass)
        buildConfigField("String", "LOCAL_NEXTCLOUD_URL", localNextcloudUrl)
        buildConfigField("String", "DEBUG_LOCAL_STABLE_URL", ampacheUrlLocal)
        buildConfigField("String", "DEBUG_LOCAL_DEVELOPMENT_URL", localDevUrl)
        buildConfigField("String", "DOGMAZIC_URL", dogmazicUrl)
        buildConfigField("String", "DOGMAZIC_TOKEN", dogmazicToken)
        buildConfigField("String", "DEFAULT_SERVER_URL", "\"\"")
        buildConfigField("boolean", "FORCE_LOGIN_DIALOG_ON_ALL_VERSIONS", "true")
        buildConfigField("boolean", "DEMO_VERSION", "false")
        buildConfigField("String", "REMOTE_CONFIG_FILE", "\"config.json\"")
    }

    buildTypes {
        debug {
            applicationIdSuffix = ".debug"
            versionNameSuffix = ".debug"

            // FLAGS
            buildConfigField("boolean", "MRLOG_ON", "true")
            buildConfigField("boolean", "ENABLE_ERROR_LOG", "true")
            buildConfigField("boolean", "ENABLE_TOKEN_LOGIN", "true")
            buildConfigField("boolean", "ENABLE_DOGMAZIC_DEMO_SERVER", "true")
            buildConfigField("boolean", "ENABLE_OFFICIAL_DEMO_SERVER", "false")
            buildConfigField("boolean", "SHOW_EMPTY_PLAYLISTS", "false")
            buildConfigField("boolean", "RESET_QUEUE_ON_NEW_SESSION", "true")
            buildConfigField("boolean", "SHOW_LOADING_ON_NEW_SESSION", "false")

            // CONSTANTS
            buildConfigField("String", "AMPACHE_USER", ampacheUser)
            buildConfigField("String", "AMPACHE_PASSWORD", ampachePass)
            buildConfigField("String", "AMPACHE_URL", ampacheUrl)
            buildConfigField("String", "API_KEY", apikey)
            buildConfigField("String", "DOGMAZIC_PASSWORD", dogmazicPass)
            buildConfigField("String", "DOGMAZIC_USER", dogmazicUser)
            buildConfigField("String", "DOGMAZIC_EMAIL", dogmazicEmail)
            buildConfigField("String", "LOCAL_DEV_USER", localDevUser)
            buildConfigField("String", "LOCAL_DEV_PASSWORD", localDevPass)

            resValue("string", "build_type", "Debug")

            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }

        release {
            // FLAGS
            buildConfigField("boolean", "MRLOG_ON", "false")
            buildConfigField("boolean", "ENABLE_ERROR_LOG", "true")
            buildConfigField("boolean", "ENABLE_TOKEN_LOGIN", "true")
            buildConfigField("boolean", "ENABLE_DOGMAZIC_DEMO_SERVER", "true")
            buildConfigField("boolean", "ENABLE_OFFICIAL_DEMO_SERVER", "false")
            buildConfigField("boolean", "SHOW_EMPTY_PLAYLISTS", "false")
            buildConfigField("boolean", "RESET_QUEUE_ON_NEW_SESSION", "true")
            buildConfigField("boolean", "SHOW_LOADING_ON_NEW_SESSION", "false")

            // CONSTANTS
            buildConfigField("String", "AMPACHE_USER", "\"\"")
            buildConfigField("String", "AMPACHE_PASSWORD", "\"\"")
            buildConfigField("String", "AMPACHE_URL", "\"\"")
            buildConfigField("String", "API_KEY", "\"\"")
            buildConfigField("String", "DOGMAZIC_PASSWORD", dogmazicPass)
            buildConfigField("String", "DOGMAZIC_USER", dogmazicUser)
            buildConfigField("String", "DOGMAZIC_EMAIL", dogmazicEmail)
            buildConfigField("String", "LOCAL_DEV_USER", "\"\"")
            buildConfigField("String", "LOCAL_DEV_PASSWORD", "\"\"")

            resValue("string", "build_type", "Release")

            isMinifyEnabled = false
            vcsInfo.include = false

            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    val flavourGithub = "Github"
    val flavourFDroid = "FDroid"
    val flavourPlayStore = "PlayStore"
    val flavourPlayStoreFree = "PlayStoreFree"

    flavorDimensions += "ampache"
    productFlavors {
        create(flavourGithub) {
            dimension = "ampache"

            buildConfigField("boolean", "SHOW_LOGIN_SERVER_VERSION_WARNING", "true")
            buildConfigField("boolean", "HIDE_DONATION", "false")
            buildConfigField("String", "URL_ERROR_LOG", "\"https://pastebin.com/api/\"")
            buildConfigField("String", "PASTEBIN_API_KEY", pastebinApiKey)
        }
        create(flavourFDroid) {
            dimension = "ampache"
            applicationIdSuffix = ".fdroid"
            versionNameSuffix = "-fdroid"

            buildConfigField("boolean", "SHOW_LOGIN_SERVER_VERSION_WARNING", "true")
            buildConfigField("boolean", "HIDE_DONATION", "false")
            buildConfigField("String", "URL_ERROR_LOG", "\"https://pastebin.com/api/\"")
            buildConfigField("String", "PASTEBIN_API_KEY", pastebinApiKey)
        }
        create(flavourPlayStore) {
            dimension = "ampache"
            applicationIdSuffix = ".play"
            versionNameSuffix = "-play"

            buildConfigField("boolean", "SHOW_LOGIN_SERVER_VERSION_WARNING", "true")
            buildConfigField("boolean", "HIDE_DONATION", "true")
            buildConfigField("String", "URL_ERROR_LOG", "\"https://pastebin.com/api/\"")
            buildConfigField("String", "PASTEBIN_API_KEY", pastebinApiKey)
        }
        create(flavourPlayStoreFree) {
            dimension = "ampache"
            applicationIdSuffix = ".free"
            versionNameSuffix = "-free"

            buildConfigField("boolean", "SHOW_LOGIN_SERVER_VERSION_WARNING", "true")
            buildConfigField("boolean", "HIDE_DONATION", "true")
            buildConfigField("boolean", "DEMO_VERSION", "true")
            buildConfigField("String", "DEFAULT_SERVER_URL", dogmazicUrl)
            buildConfigField("String", "URL_ERROR_LOG", "\"https://pastebin.com/api/\"")
            buildConfigField("String", "PASTEBIN_API_KEY", pastebinApiKey)
            buildConfigField("String", "REMOTE_CONFIG_FILE", "\"config-dogmazic.json\"")
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
    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:$lifecycleVersion")
    implementation("androidx.activity:activity-compose:1.9.1")
    implementation("androidx.media:media:1.7.0")
    implementation("androidx.work:work-runtime-ktx:2.9.0")
    implementation("androidx.compose.runtime:runtime-livedata:$composeVersion")

    // --- Compose --- //
    implementation("androidx.compose.ui:ui:$composeVersion")
    implementation("androidx.compose.material:material-icons-extended:$composeVersion")
    implementation("androidx.compose.ui:ui-tooling-preview:$composeVersion")
    implementation("androidx.compose.material3:material3:1.2.1")
    implementation("androidx.compose.ui:ui-graphics:$composeVersion")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:$lifecycleVersion")
    implementation("com.google.accompanist:accompanist-flowlayout:0.17.0")
    implementation("androidx.paging:paging-compose:3.3.1")
    implementation("com.google.accompanist:accompanist-swiperefresh:0.34.0")
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

    // --- ExoPlayer --- //
    implementation("androidx.media3:media3-exoplayer:$media3Version")
    // HLS playback support with ExoPlayer
    implementation("androidx.media3:media3-exoplayer-hls:$media3Version")
    // DASH playback support with ExoPlayer
    "PlayStoreImplementation"("androidx.media3:media3-exoplayer-dash:$media3Version")
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
    implementation("com.squareup.okhttp3:okhttp:5.0.0-alpha.14")
    implementation("com.squareup.okhttp3:logging-interceptor:4.10.0")
    // JSON serialization
    implementation("com.google.code.gson:gson:2.10.1")

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
    androidTestImplementation(platform("androidx.compose:compose-bom:2024.04.01"))
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:2.0.4")

    debugImplementation("androidx.compose.ui:ui-test-manifest")
    debugImplementation("androidx.compose.ui:ui-tooling:$composeVersion")
}
