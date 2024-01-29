plugins {
    id("commonDemoModulePlugin")
}

android {
    namespace = "com.xiaojinzi.demo.module.user"
    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_17.toString()
    }
}