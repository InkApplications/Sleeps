import org.gradle.api.tasks.testing.logging.TestExceptionFormat

gradle.startParameter.excludedTaskNames.add("lint")

repositories {
    mavenCentral()
    google()
}

allprojects {
    tasks.withType(Test::class) {
        testLogging.exceptionFormat = TestExceptionFormat.FULL
    }
    repositories {
        mavenCentral()
        google()
        mavenLocal()
    }
}
