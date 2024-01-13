plugins {
    id("library")
}

kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(libs.coroutines.core)
                implementation(libs.ink.regolith.init)
                implementation(libs.ink.regolith.processes)
                api(libs.ink.ui.structures)
                api(libs.ink.spondee.units)
                api(libs.ink.watermelon.datetime)
                api(libs.kotlinx.datetime)
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
