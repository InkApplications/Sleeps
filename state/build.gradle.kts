plugins {
    id("library")
    id("app.cash.sqldelight")
}

sqldelight {
    databases {
        create("Settings") {
            packageName.set("com.inkapplications.sleeps.state.settings")
            schemaOutputDirectory.set(file("src/commonMain/sqldelight/databases"))
        }
    }
}

kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(libs.coroutines.core)
                implementation(libs.regolith.processes)
                implementation(libs.sqldelight.coroutines)
                api(libs.regolith.init)
                api(libs.regolith.sensors)
                api(libs.ui.structures)
                api(libs.spondee.units)
                api(libs.watermelon.datetime)
                api(libs.kimchi.core)
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
