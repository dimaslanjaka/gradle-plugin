import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.util.regex.Pattern

class BuildLogicFunctionalTest {
    //@TempDir
    var testProjectDir = File("build/test-results", javaClass.name)
    private var settingsFile: File? = null
    private var buildFile: File? = null
    var rootProjectDir = File(testProjectDir, "/../../../")

    @BeforeEach
    fun setup() {
        resolveDir(testProjectDir)
        settingsFile = File(testProjectDir, "settings.gradle")
        buildFile = File(testProjectDir, "build.gradle")
        println(rootProjectDir)
        println(testProjectDir)
        println(settingsFile)
        println(buildFile)
    }

    @Throws(IOException::class)
    fun testPlugin() {
        val repl = replaceStr(rootProjectDir.absolutePath, "\\\\\\\\", "[\\\\/]")
        writeFile(settingsFile, "rootProject.name = 'testPlugin';\nincludeBuild(\"$repl\")")
        val buildFileContent = "plugins{id(\"com.dimaslanjaka\")}"
        writeFile(buildFile, buildFileContent)

        GradleRunner.create()
            .withProjectDir(testProjectDir).build()
    }

    @Test
    @Throws(IOException::class)
    fun testHelloWorldTask() {
        writeFile(settingsFile, "rootProject.name = 'hello-world'")
        val buildFileContent = "task helloWorld {" +
                "    doLast {" +
                "        println 'Hello world!'" +
                "    }" +
                "}"
        writeFile(buildFile, buildFileContent)
        val result = GradleRunner.create()
            .withProjectDir(testProjectDir)
            .withArguments("helloWorld")
            .build()
        Assertions.assertTrue(result.output.contains("Hello world!"))
        Assertions.assertEquals(TaskOutcome.SUCCESS, result.task(":helloWorld")!!.outcome)
    }

    private fun replaceStr(str: String, replacement: String, regex: String): String {
        val pattern = Pattern.compile(regex, Pattern.MULTILINE)
        val matcher = pattern.matcher(str)

        // The substituted value will be contained in the result variable
        val result = matcher.replaceAll(replacement)
        println("Substitution result: $result");
        return result;
    }

    private fun resolveDir(dir: File) {
        if (!dir.parentFile.exists()) {
            if (!dir.parentFile.mkdirs()) {
                println("Cannot create folder " + dir.parentFile.absolutePath)
            }
        }
        if (!dir.exists()) {
            if (!dir.mkdirs()) {
                println("Cannot create folder " + dir.absolutePath)
            }
        }
    }

    @Throws(IOException::class)
    private fun writeFile(destination: File?, content: String) {
        resolveDir(destination!!.parentFile)
        BufferedWriter(FileWriter(destination)).use { output -> output.write(content) }
    }
}