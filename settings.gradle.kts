enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

rootProject.name = "sleepschedule"

dependencyResolutionManagement {
    versionCatalogs {
        create("androidLibraries") {
            from(files("gradle/versions/android.toml"))
        }
        create("thirdParty") {
            from(files("gradle/versions/thirdparty.toml"))
        }
        create("kotlinLibraries") {
            from(files("gradle/versions/kotlin.toml"))
        }
        create("inkLibraries") {
            from(files("gradle/versions/ink.toml"))
        }
    }
}

include("android")
include("state")
