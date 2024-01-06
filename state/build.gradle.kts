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
                implementation(libs.ink.spondee.units)
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
