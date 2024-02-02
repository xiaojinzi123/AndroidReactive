plugins {
    id("commonLibPlugin")
    id("com.google.devtools.ksp")
}

android {
    namespace = "com.xiaojinzi.demo.module.base"
    buildFeatures {
        compose = true
    }
}

ksp {
    arg("ModuleName", "module_base")
}

dependencies {

    api(project(":reactive"))
    api(project(":reactive-template"))
    api(project(":reactive-template-compose"))
    api(project(":reactive-demo:lib-res"))

    api(libs.kcomponent.core)
    ksp(libs.kcomponent.compiler)

}