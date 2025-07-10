plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("kotlin-kapt")
    id("dagger.hilt.android.plugin")
    id("com.google.devtools.ksp")
    id("com.google.gms.google-services")

}

android {
    namespace = "com.undefined.hydron"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.undefined.hydron"
        minSdk = 31
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // Configuración para WeatherAPI
        buildConfigField("String", "WEATHER_API_KEY", "\"a0fa49908c354275909164054250707\"")
        buildConfigField("String", "WEATHER_API_BASE_URL", "\"https://api.weatherapi.com/v1/\"")

        // Para diferentes entornos
        manifestPlaceholders["api_base_url"] = "https://api.weatherapi.com/v1/"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            // Puedes sobrescribir estos valores para producción si es necesario
            buildConfigField("String", "WEATHER_API_BASE_URL", "\"https://api.weatherapi.com/v1/\"")
        }

        debug {
            buildConfigField("String", "WEATHER_API_BASE_URL", "\"https://api.weatherapi.com/v1/\"")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    kotlinOptions {
        jvmTarget = "11"
        freeCompilerArgs += listOf(
            "-Xopt-in=androidx.compose.material3.ExperimentalMaterial3Api",
            "-opt-in=kotlinx.coroutines.ExperimentalCoroutinesApi"
        )
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }
}
dependencies {
    // Core
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)

    // Compose
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.material3)

    // Hilt
    implementation(libs.hilt.android)
    kapt(libs.hilt.compiler)

    // Room
    implementation(libs.androidx.room.runtime)
    ksp(libs.androidx.room.compiler)

    implementation (libs.androidx.hilt.navigation.compose.v110)
    implementation (libs.androidx.navigation.compose.v277)

    //DataStore
    implementation (libs.androidx.datastore.preferences.v100)

    // livedata
    implementation (libs.androidx.runtime.livedata)

    //Icons
    implementation (libs.androidx.material.icons.extended.v178)

    //Google fonts
    implementation (libs.androidx.ui.text.google.fonts)

    implementation(libs.androidx.work.runtime.ktx)

    //Firebase
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.auth)
    implementation(libs.firebase.database)

    //Debug
    implementation (libs.androidx.ui)
    debugImplementation (libs.androidx.ui.tooling)

    //Weareable
    implementation (libs.play.services.wearable)

    //Cart Compose
    implementation (libs.compose.charts)

    implementation (libs.retrofit)
    implementation (libs.converter.gson)

    //Wear
    implementation(libs.play.services.wearable.v1810)


    implementation(libs.retrofit.v290)
    implementation(libs.converter.gson.v290)

    implementation("com.squareup.okhttp3:okhttp:4.11.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.11.0")

}

kapt {
    correctErrorTypes = true
}