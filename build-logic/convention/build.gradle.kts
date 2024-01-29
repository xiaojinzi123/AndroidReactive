plugins {
    `kotlin-dsl`
}

dependencies {
    compileOnly(libs.android.gradlePlugin)
    compileOnly(libs.kotlin.gradlePlugin.api)
    compileOnly(libs.kotlin.gradlePlugin)
    // 依赖 ksp
    compileOnly(libs.ksp.gradlePlugin)
    // https://mvnrepository.com/artifact/com.google.devtools.ksp/symbol-processing-api
    // runtimeOnly("com.google.devtools.ksp:symbol-processing-api:1.9.0-1.0.13")
}


gradlePlugin {
    plugins {
        register("commonLibPlugin") {
            id = "commonLibPlugin"
            implementationClass = "CommonLibPlugin"
        }
        register("commonModulePlugin") {
            id = "commonModulePlugin"
            implementationClass = "CommonModulePlugin"
        }
        register("commonDemoModulePlugin") {
            id = "commonDemoModulePlugin"
            implementationClass = "CommonDemoModulePlugin"
        }
    }
}