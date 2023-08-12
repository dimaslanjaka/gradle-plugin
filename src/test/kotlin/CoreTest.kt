import com.dimaslanjaka.gradle.plugin.Core
import com.dimaslanjaka.java.Resources
import com.dimaslanjaka.kotlin.File
import org.gradle.api.Project
import org.gradle.testkit.runner.GradleRunner
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import com.dimaslanjaka.gradle.api.Project as ApiProject

class CoreTest {
    var testProjectDir = File("build/test-results", javaClass.name)
    lateinit var project: Project

    /**
     * preparation each test
     */
    @BeforeEach
    fun setup() {
        val createProject = testProjectDir.createGradleProject()
        val apiProject = ApiProject(createProject)
        Resources.copy(
                "settings.gradle", File(apiProject.DEFAULT_SETTINGS_BUILD_FILE).toDimaslanjakaGradlePluginFile()
        )
        Resources.copy("build.java.gradle", File(apiProject.DEFAULT_BUILD_FILE).toDimaslanjakaGradlePluginFile())

        project = apiProject.getProject()
    }

    /**
     * Activate core plugin
     */
    @Test
    fun activate() {
        project.plugins.apply(Core::class.java)
    }

    /**
     * Test using direct sample project folder
     */
    @Test
    fun runner() {
        GradleRunner.create().withProjectDir(testProjectDir).build()
    }
}