plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.serialization)
//    alias(libs.plugins.google.services)
    id("com.google.devtools.ksp")
    alias(libs.plugins.google.gms.google.services)
}

android {
    namespace = "com.stephennnamani.burgerrestaurantapp"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.stephennnamani.burgerrestaurantapp"
        minSdk = 33
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        buildConfigField("String", "PAYPAL_CLIENT_ID", "\"AXJfaZVxftW7n91sCNIBBXE_BuM_7CLYE3awQeNuHbgOZ01s7e5KFMGqNhJhv4-qSqsaMsjfF59ozwKo\"")
        buildConfigField("String", "PAYPAL_RETURN_URL", "\"com.stephennnamani.burgerrestaurantapp\"")
        buildConfigField("String", "PAYMENTS_BASE_URL", "\"https://us-central1-burger-restaurant-app.cloudfunctions.net/api/\"")
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
    buildFeatures {
        compose = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.navigation.compose)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    // Firebase
    implementation(libs.firebase.auth)
    implementation(libs.androidx.credentials)
    implementation(libs.androidx.credentials.play.services.auth)
    implementation(libs.googleid)
    implementation(libs.firebase.firestore)
    implementation(libs.firebase.database)
    implementation(libs.firebase.storage)
    implementation(platform("com.google.firebase:firebase-bom:34.4.0"))



    // Koin
    implementation(libs.koin.android)
    implementation(libs.koin.androidx.compose)
    implementation(libs.koin.androidx.compose.navigation)

    // Koin Annotations and ksp compiler
    implementation(libs.koin.annotations)
    ksp(libs.koin.ksp.compiler)

    // Kotlinx serialization
    implementation(libs.kotlinx.serialization)

    // Coil
    implementation(libs.coil3)
    implementation(libs.coil3.compose)
    implementation(libs.coil3.compose.core)
    implementation(libs.coil3.network.ktor)
    implementation("io.coil-kt.coil3:coil-network-okhttp:3.3.0")

    // Coroutines
    implementation(libs.kotlinx.coroutines.android)

    //Retrofit
    implementation("com.squareup.retrofit2:retrofit:3.0.0")
    implementation("com.squareup.okhttp3:okhttp:5.2.0")
    implementation("com.squareup.retrofit2:converter-gson:3.0.0")
    implementation("com.jakewharton.retrofit:retrofit2-kotlinx-serialization-converter:1.0.0")
}