plugins {
    id("library")
}

kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(kotlinLibraries.coroutines.core)
                implementation(inkLibraries.regolith.processes)
                api(inkLibraries.regolith.init)
                api(inkLibraries.ui.structures)
                api(inkLibraries.spondee.units)
                api(inkLibraries.watermelon.datetime)
                api(inkLibraries.kimchi.core)
                api(kotlinLibraries.kotlinx.datetime)
            }
        }

        val jvmMain by getting {
            dependencies {
                implementation(thirdParty.sunrisesunsetcalculator)
            }
        }

        val commonTest by getting {
            dependencies {
                implementation(kotlinLibraries.coroutines.test)
                implementation(kotlinLibraries.kotlin.test.core)
            }
        }
    }
}
