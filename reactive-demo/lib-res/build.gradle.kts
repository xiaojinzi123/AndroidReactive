plugins {
    id("commonLibPlugin")
}

android {
    namespace = "com.xiaojinzi.demo.lib.res"
    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_17.toString()
    }
}

dependencies {
    api(libs.xiaojinzi.android.support.annotation)
}