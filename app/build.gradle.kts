plugins {
    alias(libs.plugins.android.application)
    //id ("com.google.gms.google-services")
    id("com.google.gms.google-services") version "4.5.0"
}

android {
    namespace = "com.example.thoughtbook"
    compileSdk {
        version = release(36) {
            minorApiLevel = 1
        }
    }

    defaultConfig {
        applicationId = "com.example.thoughtbook"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            optimization {
                enable = false
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    implementation(libs.activity.ktx)
    implementation(libs.appcompat)
    implementation(libs.constraintlayout)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.espresso.core)
    androidTestImplementation(libs.ext.junit)


        implementation(platform(libs.firebase.bom))
        implementation(libs.firebase.auth)
        implementation(libs.firebase.firestore)

        implementation(libs.retrofit)
        implementation(libs.converter.gson)

        implementation(libs.glide)

}