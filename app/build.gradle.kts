plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("kotlin-kapt")
    id("dagger.hilt.android.plugin")
    id("com.google.devtools.ksp")
    //id("com.android.application")
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
        freeCompilerArgs += listOf(
            "-Xopt-in=androidx.compose.material3.ExperimentalMaterial3Api"
        )
    }

    buildFeatures {
        compose = true
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


}

kapt {
    correctErrorTypes = true
}