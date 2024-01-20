plugins {
    `kotlin-dsl`
}
repositories {
    mavenCentral()
    google()
    gradlePluginPortal()
}

dependencies {
    implementation(androidLibraries.android.gradle)
    implementation(kotlinLibraries.kotlin.gradle)
}
