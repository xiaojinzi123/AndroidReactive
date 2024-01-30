plugins {
    id("commonModulePlugin")
    id("maven-publish")
}

android {
    namespace = "com.xiaojinzi.reactive"
    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_17.toString()
    }
}

dependencies {

    implementation(kotlin("reflect"))
    api(libs.xiaojinzi.android.support.ktx)
    api(libs.xiaojinzi.android.support.annotation)
    api(libs.xiaojinzi.android.support.compose)
    api(libs.compose.runtime)
    api(libs.compose.runtime.android)
    api(libs.compose.ui.android)
    api(libs.compose.foundation.android)
    api(libs.lifecycle.viewmodel.compose)
    api(libs.androidx.core)
    api(libs.androidx.appcompat)

}

// androidSourcesJar task
tasks.register<Jar>("androidSourcesJar") {
    archiveClassifier.set("sources")
    from(android.sourceSets["main"].java.srcDirs)
}

artifacts {
    archives(tasks.getAt("androidSourcesJar"))
}

tasks.configureEach {
    if (this.name == "generateMetadataFileForReleasePublication") {
        this.dependsOn("androidSourcesJar")
    }
}

afterEvaluate {
    publishing {
        publications {
            create<MavenPublication>("release") {
                from(components["release"])
                groupId = "com.github.xiaojinzi123"
                artifactId = "android-reactive"
                version = "0.0.1"
            }
        }
    }
}
