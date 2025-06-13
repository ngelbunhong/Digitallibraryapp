plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("com.google.devtools.ksp")
    id ("kotlin-parcelize")
    id("androidx.navigation.safeargs.kotlin")
}

android {
    namespace = "com.library.digitallibrary"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.library.digitallibrary"
        minSdk = 24
        //noinspection OldTargetApi
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        externalNativeBuild {
            cmake {
                cppFlags += ""
            }
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
    buildFeatures {
        viewBinding = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    externalNativeBuild {
        cmake {
            path = file("src/main/cpp/CMakeLists.txt")
            version = "3.22.1"
        }
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
    implementation(libs.navigation.fragment.ktx)
    implementation(libs.navigation.ui.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    //Room
    implementation("androidx.room:room-runtime:2.7.1")
    ksp ("androidx.room:room-compiler:2.7.1")
    // Optional: for Kotlin coroutines support
    implementation ("androidx.room:room-ktx:2.7.1")

    //LiveData
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.9.1")

    // For loading book images
    implementation ("com.squareup.picasso:picasso:2.71828")

    // Retrofit
    implementation ("com.squareup.retrofit2:retrofit:2.9.0")
    implementation ("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation ("com.squareup.okhttp3:logging-interceptor:4.10.0")

    //ViewPager2
    implementation ("androidx.viewpager2:viewpager2:1.1.0")

    //Glide Image
    implementation ("com.github.bumptech.glide:glide:4.16.0")

    //AndroidSVG
    implementation ("com.caverock:androidsvg-aar:1.4")


    // Add this for Chrome Custom Tabs
    implementation ("androidx.browser:browser:1.8.0")

}