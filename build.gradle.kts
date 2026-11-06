buildscript {

    dependencies {
        classpath("com.android.tools.build:gradle:${libs.versions.agp}")
        classpath(libs.kcomponent.gradle.plugin)
    }

}

// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.android.application") version libs.versions.agp  apply false
    id("com.android.library") version libs.versions.agp apply false
    id("org.jetbrains.kotlin.jvm") version libs.versions.kotlin apply false
    id("com.google.devtools.ksp") version libs.versions.ksp apply false
    alias(libs.plugins.compose.compiler) apply false
}

// JitPack 会为每个子项目注入一个以项目名命名的 publication，与各模块显式声明的坐标冲突
// （Gradle 报 "multiple publications that have different coordinates"）。这里把它对齐到显式坐标。
gradle.projectsEvaluated {
    rootProject.allprojects.forEach { p ->
        val pubs = p.extensions.findByType(org.gradle.api.publish.PublishingExtension::class.java)
            ?.publications ?: return@forEach
        val injected = pubs.findByName(p.name) as? org.gradle.api.publish.maven.MavenPublication
            ?: return@forEach
        val ours = (pubs.findByName("release") ?: pubs.findByName("maven"))
            as? org.gradle.api.publish.maven.MavenPublication ?: return@forEach
        injected.groupId = ours.groupId
        injected.artifactId = ours.artifactId
        injected.version = ours.version
    }
}
