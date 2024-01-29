import com.google.devtools.ksp.gradle.KspExtension
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

class CommonDemoModulePlugin : CommonModulePlugin() {

    override fun apply(project: Project) {
        super.apply(project)
        with(project) {
            plugins.apply {
                apply("com.google.devtools.ksp")
            }
            extensions.configure<KspExtension> {
                var tempProject: Project = project
                var name = tempProject.name
                while (tempProject.parent != null) {
                    tempProject = tempProject.parent!!
                    name = tempProject.name + "_" + name
                }
                arg("ModuleName", name)
            }
        }
        with(project) {
            dependencies.apply {
                add("api", project(":reactive-demo:module-base"))
                add("ksp", libs.findLibrary("kcomponent-compiler").get().get().toString())
            }
        }
    }

}