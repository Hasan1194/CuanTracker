plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("kotlin-parcelize")
    id("com.google.devtools.ksp")
}

android {
    namespace = "com.h1194.cuantracker"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.h1194.cuantracker"
        minSdk = 26
        targetSdk = 34
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        viewBinding = true
        buildConfig = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    // Material Design
    implementation ("com.google.android.material:material:1.3.0-alpha03")

    // MPAndroidChart
    implementation ("com.github.PhilJay:MPAndroidChart:v3.1.0")

    // Room
    implementation(libs.room.ktx)
    implementation(libs.androidx.room.runtime)
    ksp(libs.room.compiler)

    // Kotlin Extensions and Coroutines support for Room
    implementation ("androidx.room:room-ktx:2.5.0")
    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.0")

    // Lifecycle components
    implementation ("androidx.lifecycle:lifecycle-runtime-ktx:2.6.0")
    implementation ("androidx.lifecycle:lifecycle-viewmodel-ktx:2.5.1")

    // Coroutines
    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")
    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.6.4")

    // RecyclerView
    implementation ("androidx.recyclerview:recyclerview:1.3.0")

    // POI
    implementation ("org.apache.poi:poi-ooxml:5.2.3")
}