import com.android.build.gradle.LibraryExtension
import org.gradle.api.Project
import org.gradle.api.artifacts.dsl.ArtifactHandler
import org.gradle.api.tasks.bundling.Jar
import org.gradle.kotlin.dsl.artifacts
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.register

class AndroidLibraryPublishPlugin : CommonLibPlugin() {

    override fun apply(project: Project) {
        super.apply(project)
        with(project) {
            plugins.apply {
                if (this.findPlugin("com.android.library") == null) {
                    apply("com.android.library")
                }
                if (this.findPlugin("org.jetbrains.kotlin.android") == null) {
                    apply("org.jetbrains.kotlin.android")
                }
                if (this.findPlugin("maven-publish") == null) {
                    apply("maven-publish")
                }
            }
            extensions.configure<LibraryExtension> {
                tasks.register<Jar>("androidSourcesJar") {
                    archiveClassifier.set("sources")
                    from(sourceSets.getAt("main").java.srcDirs)
                }
            }
            artifacts {
                add("archives", tasks.getAt("androidSourcesJar"))
            }
            tasks.configureEach {
                if (this.name == "generateMetadataFileForReleasePublication") {
                    this.dependsOn("androidSourcesJar")
                }
            }
        }
    }

}