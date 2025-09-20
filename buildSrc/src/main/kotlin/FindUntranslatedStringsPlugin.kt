import org.gradle.api.Plugin
import org.gradle.api.Project


class FindUntranslatedStringsPlugin : Plugin<Project>{
    override fun apply(target: Project) {
        target.tasks.register("untranslatedStrings", FindUntranslatedStringsTask::class.java)
    }
}
