pluginManagement {
    includeBuild("./build-logic")
    repositories {
        maven {
            url = uri("https://jitpack.io")
        }
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    versionCatalogs {
        create("libs") {
            from(files("./libs.versions.toml"))
        }
    }
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        maven {
            url = uri("https://jitpack.io")
        }
        google()
        mavenCentral()
    }
}

rootProject.name = "AndroidReactive"
include(":reactive-demo:app")
include(":reactive")
include(":reactive-template")
include(":reactive-template-compose")
include(":reactive-demo:module-base")
include(":reactive-demo:module-user")
include(":reactive-demo:lib-res")
