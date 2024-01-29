import com.android.build.gradle.LibraryExtension
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

open class CommonModulePlugin : CommonLibPlugin() {
    override fun apply(project: Project) {
        super.apply(project = project)
        with(project) {
            extensions.configure<LibraryExtension> {
                buildFeatures {
                    compose = true
                }
            }
        }
    }

}