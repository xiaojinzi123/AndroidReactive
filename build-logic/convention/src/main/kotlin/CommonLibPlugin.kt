import com.android.build.gradle.LibraryExtension
import org.gradle.api.JavaVersion
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.plugins.precompiled.kotlinDslPluginOptions
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmOptions
import org.jetbrains.kotlin.gradle.dsl.kotlinExtension
import org.jetbrains.kotlin.gradle.model.KotlinAndroidExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinPluginWrapper
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.jetbrains.kotlin.gradle.tasks.internal.KotlinJvmOptionsCompat

open class CommonLibPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        with(project) {
            plugins.apply {
                if (this.findPlugin("com.android.library") == null) {
                    apply("com.android.library")
                }
                if (this.findPlugin("kotlin-android") == null) {
                    apply("kotlin-android")
                }
                if (this.findPlugin("org.jetbrains.kotlin.android") == null) {
                    apply("org.jetbrains.kotlin.android")
                }
                // apply<KotlinPluginWrapper>()
            }
            extensions.configure<LibraryExtension> {
                compileSdk = libs.findVersion("compileSdk").get().toString().toInt()
                defaultConfig {
                    minSdk = libs.findVersion("minSdk").get().toString().toInt()
                    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
                    consumerProguardFiles("consumer-rules.pro")
                }
                buildTypes {
                    release {
                        isMinifyEnabled = false
                        proguardFiles(
                            getDefaultProguardFile("proguard-android-optimize.txt"),
                            "proguard-rules.pro"
                        )
                    }
                }
                compileOptions {
                    sourceCompatibility(JavaVersion.VERSION_17)
                    targetCompatibility(JavaVersion.VERSION_17)
                }
                tasks.withType(KotlinCompile::class.java) {
                    kotlinOptions {
                        jvmTarget = JavaVersion.VERSION_17.toString()
                    }
                }
                composeOptions {
                    kotlinCompilerExtensionVersion =
                        libs.findVersion("kotlinCompilerExtensionVersion").get().toString()
                }
            }
        }
    }

}