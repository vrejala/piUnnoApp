plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.unnoapp"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.unnoapp"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        debug {
            // Emulador Android Studio
            buildConfigField("String", "BASE_URL_EMULADOR", "\"http://10.0.2.2:8000/\"")
            // Celular físico (troque pelo IP da sua máquina na rede local)
            buildConfigField("String", "BASE_URL_FISICO", "\"http://192.168.100.22:8000/\"")

            // Supabase: ajuste os valores abaixo
            buildConfigField("boolean", "USE_SUPABASE", "false") // false durante dev local, true quando quiser usar Supabase direto
            buildConfigField("String", "SUPABASE_URL", "\"https://<YOUR_PROJECT>.supabase.co/rest/v1/\"")
            buildConfigField("String", "SUPABASE_ANON_KEY", "\"<YOUR_ANON_KEY>\"")
        }
        release {
            buildConfigField("String", "BASE_URL_EMULADOR", "\"http://10.0.2.2:8000/\"")
            // Celular físico (troque pelo IP da sua máquina na rede local)
            buildConfigField("String", "BASE_URL_FISICO", "\"http://192.168.100.22:8000/\"")

            // Para release você provavelmente vai usar Supabase direto:
            buildConfigField("boolean", "USE_SUPABASE", "true")
            buildConfigField("String", "SUPABASE_URL", "\"https://<YOUR_PROJECT>.supabase.co/rest/v1/\"")
            buildConfigField("String", "SUPABASE_ANON_KEY", "\"<YOUR_ANON_KEY>\"")
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
    buildFeatures {
        viewBinding = true
        buildConfig = true
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    implementation(libs.okhttp.logging)
    implementation("com.google.android.gms:play-services-auth:20.7.0")
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:okhttp:4.9.3")
    implementation("com.squareup.okhttp3:logging-interceptor:4.9.3")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.6.2")

}