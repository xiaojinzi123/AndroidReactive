dependencyResolutionManagement {
    repositories {
        maven {
            url = uri("https://plugins.gradle.org/m2/")
        }
        google()
        mavenCentral()
    }
    versionCatalogs {
        create("libs") {
            from(files("../libs.versions.toml"))
        }
    }
}

rootProject.name = "build-logic"
include(":convention")