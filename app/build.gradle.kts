
plugins {
    alias(libs.plugins.android.application)

    id("com.google.gms.google-services")
}

android {
    namespace = "com.veritas.veritas"
    compileSdk = 35

    android {
        compileOptions {
            isCoreLibraryDesugaringEnabled = true
        }
    }

    defaultConfig {
        applicationId = "com.veritas.veritas"
        minSdk = 28
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        addManifestPlaceholders(
            mapOf(
                "VKIDClientID" to "53557156", // ID вашего приложения (app_id).
                "VKIDClientSecret" to "DKcv7OtfcgaqgybrFk9n", // Ваш защищенный ключ (client_secret).
                "VKIDRedirectHost" to "vk.com", // Обычно используется vk.com.
                "VKIDRedirectScheme" to "vk53557156", // Обычно используется vk{ID приложения}.
            )
        )
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation (libs.preference.v110)
    implementation(libs.swiperefreshlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    implementation(libs.okhttp)
    implementation(libs.gson)
    implementation(libs.vkid)
    coreLibraryDesugaring(libs.desugar.jdk.libs)
    implementation(libs.onetap.xml)

    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.database)
}

