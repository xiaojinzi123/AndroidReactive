import org.jetbrains.kotlin.gradle.plugin.mpp.pm20.util.archivesName

plugins {
    id("commonLibPlugin")
    id("androidLibraryPublishPlugin")
}

android {
    namespace = "com.xiaojinzi.reactive"
}

dependencies {

    implementation(kotlin("reflect"))
    api(libs.xiaojinzi.android.support.ktx)
    api(libs.xiaojinzi.android.support.annotation)
    api(libs.androidx.core)
    api(libs.androidx.appcompat)

}

archivesName.set("reactive-core")

afterEvaluate {
    publishing {
        publications {
            create<MavenPublication>("release") {
                from(components["release"])
                groupId = "com.github.xiaojinzi123"
                artifactId = "reactive-core"
                version = libs.versions.publish.get()
            }
        }
    }
}
