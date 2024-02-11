plugins {
    id("com.android.application")
    kotlin("android")
}

android {
    namespace = "com.inkapplications.sleeps.android"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.inkapplications.sleeps.android"
        minSdk = 21
        targetSdk = 34
        versionCode = project.properties.getOrDefault("versionCode", "1").toString().toInt()
        versionName = project.properties.getOrDefault("versionName", "SNAPSHOT").toString()
        buildConfigField("String", "COMMIT", project.properties.getOrDefault("commit", null)?.let { "\"$it\"" } ?: "null")

        vectorDrawables {
            useSupportLibrary = true
        }
    }
    signingConfigs {
        create("parameterSigning") {
            storeFile = project.properties.getOrDefault("signingFile", null)
                ?.toString()
                ?.let { File("${project.rootDir}/$it") }
            keyAlias = project.properties.getOrDefault("signingAlias", null)?.toString()
            keyPassword = project.properties.getOrDefault("signingKeyPassword", null)?.toString()
            storePassword = project.properties.getOrDefault("signingStorePassword", null)?.toString()
        }
    }
    buildTypes {
        debug {
            applicationIdSuffix = ".debug"
            resValue("string", "app_name", "Sleeps*")
        }
        release {
            resValue("string", "app_name", "Sleeps")
            isMinifyEnabled = true
            isShrinkResources = true
            applicationIdSuffix = if (project.properties.getOrDefault("snapshot", "false") == "true") ".snapshot" else null
            signingConfig = if (project.hasProperty("signingFile")) {
                signingConfigs.getByName("parameterSigning")
            } else {
                signingConfigs.getByName("debug")
            }
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
        freeCompilerArgs += listOf(
            "-P",
            "plugin:androidx.compose.compiler.plugins.kotlin:stabilityConfigurationPath=" +
                "${project.projectDir.absolutePath}/compose-compiler.conf"
        )
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.7"
    }
}

dependencies {
    implementation(libs.bundles.androidx)
    implementation(platform(libs.compose.bom))
    implementation(libs.bundles.compose)
    implementation(libs.sqldelight.android)
    implementation(libs.ui.compose)
    implementation(libs.watermelon.android)
    implementation(libs.kimchi.core)
    implementation(projects.state)
}
