plugins {
    id("library")
}

kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(libs.coroutines.core)
                implementation(libs.ink.regolith.processes)
                api(libs.ink.regolith.init)
                api(libs.ink.ui.structures)
                api(libs.ink.spondee.units)
                api(libs.ink.watermelon.datetime)
                api(libs.kotlinx.datetime)
                api(libs.kimchi.core)
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
