pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "Convenience Store Assessment"
include(":app")
include(":products:domain")
include(":products:data")
include(":products:ui")
include(":stocks:domain")
include(":stocks:data")
include(":stocks:ui")
include(":authentication:domain")
include(":authentication:data")
include(":authentication:ui")
include(":dashboard:domain")
include(":dashboard:data")
include(":dashboard:ui")
include(":suppliers:domain")
include(":suppliers:data")
include(":suppliers:ui")
include(":transactions:ui")
include(":transactions:domain")
include(":transactions:data")
