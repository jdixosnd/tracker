pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
    plugins {
        id("com.android.application") version "8.9.0" apply false
        id("org.jetbrains.kotlin.android") version "1.9.25" apply false
        id("com.google.devtools.ksp") version "1.9.25-1.0.20" apply false
        id("com.google.dagger.hilt.android") version "2.57.1" apply false
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}
rootProject.name = "BabyFeed Tracker"
include(":app")
