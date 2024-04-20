plugins {
    id("library")
}

kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(libs.coroutines.core)
                implementation(libs.regolith.processes)
                api(libs.regolith.init)
                api(libs.regolith.sensors)
                api(libs.ui.structures)
                api(libs.spondee.units)
                api(libs.watermelon.datetime)
                api(libs.kimchi.core)
                api(libs.kotlinx.datetime)
                implementation(libs.regolith.data)
                implementation(libs.watermelon.data)
            }
        }

        val jvmMain by getting {
            dependencies {
                implementation(libs.sunrisesunsetcalculator)
            }
        }

        val commonTest by getting {
            dependencies {
                implementation(libs.coroutines.test)
                implementation(libs.kotlin.test.core)
            }
        }
    }
}
