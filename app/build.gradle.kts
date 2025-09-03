import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
    alias(libs.plugins.kotlin.compose)
    id("kotlin-kapt")
    id("kotlin-parcelize")
    // id("org.jetbrains.kotlin.plugin.compose")
    // id("dagger.hilt.android.plugin")
    // id("com.google.devtools.ksp")
}

val localProperties = Properties()
localProperties.load(project.rootProject.file("local.properties").inputStream())

android {
    namespace = "luci.sixsixsix.powerampache2"
    compileSdk = 35

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
    val ampacheUser2 = properties.getProperty("AMPACHE_USER_2")
    val ampachePass2 = properties.getProperty("AMPACHE_PASSWORD_2")
    val ampacheUrl2 = properties.getProperty("AMPACHE_URL_2")
    val ampacheUser3 = properties.getProperty("AMPACHE_USER_3")
    val ampachePass3 = properties.getProperty("AMPACHE_PASSWORD_3")
    val ampacheUrl3 = properties.getProperty("AMPACHE_URL_3")
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
        targetSdk = 35
        versionCode = 90
        versionName = "1.01-84"
        val versionQuote = "This version is powered by the 23rd prime number summed by other consecutive primes, Bismuth and overall the best year ever"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        vectorDrawables {
            useSupportLibrary = true
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

        buildConfigField("boolean", "PLAYLISTS_USER_FETCH", "false")
        buildConfigField("boolean", "SMARTLISTS_USER_FETCH", "false")
        buildConfigField("boolean", "PLAYLISTS_ADMIN_FETCH", "false")
        buildConfigField("boolean", "SMARTLISTS_ADMIN_FETCH", "false")
        buildConfigField("boolean", "PLAYLISTS_ALL_SERVER_FETCH", "true")
        buildConfigField("boolean", "USE_INCREMENTAL_LIMIT_ALBUMS", "true")

        // set to false for flavours that implement a data layer different that Ampache
        buildConfigField("boolean", "IS_AMPACHE_DATA", "true")

        buildConfigField("String", "REMOTE_CONFIG_FILE", "\"config.json\"")
    }

    buildTypes {
        debug {
            applicationIdSuffix = ".debug"
            versionNameSuffix = ".debug"

            // FLAGS
            //buildConfigField("boolean", "MRLOG_ON", "true")
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

            buildConfigField("String", "AMPACHE_USER_2", ampacheUser2)
            buildConfigField("String", "AMPACHE_PASSWORD_2", ampachePass2)
            buildConfigField("String", "AMPACHE_URL_2", ampacheUrl2)

            buildConfigField("String", "AMPACHE_USER_3", ampacheUser3)
            buildConfigField("String", "AMPACHE_PASSWORD_3", ampachePass3)
            buildConfigField("String", "AMPACHE_URL_3", ampacheUrl3)

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
            //buildConfigField("boolean", "MRLOG_ON", "false")
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

            buildConfigField("String", "AMPACHE_USER_2", "\"\"")
            buildConfigField("String", "AMPACHE_PASSWORD_2", "\"\"")
            buildConfigField("String", "AMPACHE_URL_2", "\"\"")

            buildConfigField("String", "AMPACHE_USER_3", "\"\"")
            buildConfigField("String", "AMPACHE_PASSWORD_3", "\"\"")
            buildConfigField("String", "AMPACHE_URL_3", "\"\"")

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

            buildConfigField("boolean", "PLAYLISTS_ALL_SERVER_FETCH", "false")
            buildConfigField("boolean", "PLAYLISTS_USER_FETCH", "true")
            buildConfigField("boolean", "SMARTLISTS_USER_FETCH", "true")
            buildConfigField("boolean", "PLAYLISTS_ADMIN_FETCH", "true")
            buildConfigField("boolean", "SMARTLISTS_ADMIN_FETCH", "true")
            buildConfigField("boolean", "USE_INCREMENTAL_LIMIT_ALBUMS", "false")
        }
    }

    compileOptions {
        isCoreLibraryDesugaringEnabled = true
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    kotlinOptions {
        jvmTarget = "11"
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.15"
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

// TODO: what is this?
//composeCompiler {
//    reportsDestination = layout.buildDirectory.dir("compose_compiler")
//    //stabilityConfigurationFile = rootProject.layout.projectDirectory.file("stability_config.conf")
//}

dependencies {
    implementation(project(":PowerAmpache2Theme"))
    implementation(project(":domain"))
    implementation(project(":MrLog"))
    implementation(project(":data-ampache"))

    implementation(libs.androidx.core.ktx)

    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.media)
    implementation(libs.androidx.work.runtime.ktx)
    implementation(libs.androidx.runtime.livedata)

    // --- Compose --- //
    implementation(libs.compose.ui)
    implementation(libs.compose.material.icons.extended)
    implementation(libs.compose.ui.tooling.preview)
    implementation(libs.compose.material3)
    implementation(libs.compose.ui.graphics)
    implementation(libs.compose.lifecycle.viewmodel)
    implementation(libs.accompanist.flowlayout)
    implementation(libs.compose.paging)
    implementation(libs.accompanist.swiperefresh)
    implementation(libs.androidx.lifecycle.runtime.compose)
    // DO NOT INCLUDE implementation("androidx.compose.material:material:$composeVersion")

    // --- Compose Nav-Destinations --- //
    // Version with animations
    implementation(libs.navigationcompose.destinations.animations.core)
    // version with no animations
    // implementation("io.github.raamcosta.compose-destinations:core:$composeNavVersion")
    ksp(libs.navigationcompose.destinations.ksp)

    // COROUTINES
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.android)

    // --- ExoPlayer --- //
    implementation(libs.media3.exoplayer)
    // HLS playback support with ExoPlayer
    implementation(libs.media3.exoplayer.hls)
    // DASH playback support with ExoPlayer
    "PlayStoreImplementation"(libs.media3.exoplayer.dash)
    // RTSP playback support with ExoPlayer
    // implementation("androidx.media3:media3-exoplayer-rtsp:$media3Version")
    // Common functionality for media database components
    implementation(libs.media3.database)
    // Common functionality for media decoders
    implementation(libs.media3.decoder)
    // Common functionality for loading data
    implementation(libs.media3.datasource)
    // Common functionality used across multiple media libraries
    implementation(libs.media3.common)
    implementation(libs.media3.session)
    implementation(libs.media3.ui)
// ON DATA LAYER    implementation(libs.media3.datasource.okhttp)

    // --- Coil, image-loader --- //
    implementation(libs.coil.compose)

    // --- Dagger Hilt --- //
    implementation(libs.hilt.android)
    kapt(libs.hilt.android.compiler)
    kapt(libs.androidx.hilt.compiler)
    implementation(libs.androidx.hilt.navigation.compose)
    implementation(libs.androidx.hilt.common)
    implementation(libs.androidx.hilt.work)
    //implementation("androidx.hilt:hilt-lifecycle-viewmodel:1.0.0-alpha03")

    // --- Retrofit --- //
    // TODO: breaking clean, this doesn't belong here. This is only used to track a NetworkException.
    implementation(libs.retrofit)

    // JSON serialization
    implementation(libs.gson)

    // --- Room --- //
//    implementation(libs.room.runtime)
//    ksp(libs.room.compiler)
//    // Kotlin Extensions and Coroutines support for Room
//    implementation(libs.room.ktx)

    // ERROR REPORT
    implementation(libs.acra.mail)

    coreLibraryDesugaring(libs.desugar.jdk.libs)

    // --- TESTING --- //
    testImplementation(libs.junit)
    androidTestImplementation(libs.compose.ui.test.junit4)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    debugImplementation(libs.androidx.ui.test.manifest)
    debugImplementation(libs.compose.ui.tooling)
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}
