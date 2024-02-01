plugins {
    `kotlin-dsl`
}

dependencies {
    implementation(libs.gradle.kotlin.dsl.plugin)
    implementation(libs.android.gradlePlugin)
    implementation(libs.kotlin.gradlePlugin.api)
    implementation(libs.kotlin.gradlePlugin)
    // 依赖 ksp
    implementation(libs.ksp.gradlePlugin)
    // https://mvnrepository.com/artifact/com.google.devtools.ksp/symbol-processing-api
    // runtimeOnly("com.google.devtools.ksp:symbol-processing-api:1.9.0-1.0.13")
}


gradlePlugin {
    plugins {
        register("commonLibPlugin") {
            id = "commonLibPlugin"
            implementationClass = "CommonLibPlugin"
        }
        register("commonDemoModulePlugin") {
            id = "commonDemoModulePlugin"
            implementationClass = "CommonDemoModulePlugin"
        }
        register("androidLibraryPublishPlugin") {
            id = "androidLibraryPublishPlugin"
            implementationClass = "AndroidLibraryPublishPlugin"
        }
    }
}