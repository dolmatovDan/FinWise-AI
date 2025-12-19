import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
}


android {
    namespace = "com.spbsu_team7.finwise"
    compileSdk = 36

    android.buildFeatures.buildConfig = true

    defaultConfig {
        applicationId = "com.spbsu_team7.finwise"
        minSdk = 34
        targetSdk = 36
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
            buildConfigField("String", "BASE_URL_AUTH", "\"http://10.132.38.110:8082\"")
            buildConfigField("String", "BASE_URL_USER", "\"http://10.132.38.110:8080\"")
        }

        debug {
            buildConfigField("String", "BASE_URL_AUTH", "\"http://10.132.38.110:8082\"")
            buildConfigField("String", "BASE_URL_USER", "\"http://10.132.38.110:8080\"")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }


    hilt {
        enableAggregatingTask = false
    }

}

kotlin {
    compilerOptions {
        jvmTarget = JvmTarget.fromTarget("17")
    }
}


dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    implementation(platform(libs.androidx.compose.bom))

    implementation(libs.bundles.compose)
    implementation(libs.bundles.viewmodel)
    implementation(libs.bundles.charts)
    implementation(libs.bundles.hilt)
    implementation(libs.bundles.retrofit)

    ksp(libs.hilt.compiler)
}