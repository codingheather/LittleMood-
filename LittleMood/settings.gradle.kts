pluginManagement {
    repositories {
//        maven { url 'https://jitpack.io' }
//        maven { url=uri("https://www.jitpack.io")}

        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {

        google()
        mavenCentral()
    }
}

rootProject.name = "Little Mood"
include(":app")

