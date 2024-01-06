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
            }
        }
    }
}
