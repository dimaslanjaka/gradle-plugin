package cn.renyuzhuo.easygradle.killer

import com.intellij.ide.plugins.PluginManager
import com.intellij.notification.Notification
import com.intellij.notification.NotificationType
import com.intellij.notification.Notifications
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.util.*

class KillGradleAction : AnAction() {

    private val TITLE = "Easy Gradle"
    private val GITHUB_LINK = "https://github.com/renyuzhuo/easygradle/issues"

    private lateinit var pids: Array<String>

    override fun actionPerformed(event: AnActionEvent) {
        try {
            pids = getPids()
        } catch (e: Exception) {
            handleUnexpectedException(e)
            showNotification("Could not get process data!", true)
        }

        if (pids.isEmpty()) {
            showNotification("No Gradle process is running!", false)
        } else {
            var result = true
            for (pid in pids) {
                result = result and killProcess(pid)
            }
            if (result) {
                showNotification("Gradle was killed!", false)
            } else {
                showNotification("Could not kill gradle! Check that your system supports killing processes!", true)
            }
        }

    }

    private fun getPids(): Array<String> {
        return if (System.getProperty("os.name").startsWith("Windows")) {
            getPidsOnWindows()
        } else {
            getPidsOnUnix()
        }
    }

    private fun getPidsOnWindows(): Array<String> {
        val pids = ArrayList<String>()
        val r = Runtime.getRuntime()
        val p: Process
        try {
            p = r.exec("wmic process where \"commandline like '%gradle-launcher%' and name like '%java%'\" get processid")
            p.waitFor()
            val b = BufferedReader(InputStreamReader(p.inputStream))
            var line: String? = b.readLine()

            while (line != null) {
                try {
                    Integer.parseInt(line.trim { it <= ' ' })
                    pids.add(line.trim { it <= ' ' })
                } catch (e: NumberFormatException) {
                }
                line = b.readLine()
            }

            b.close()
        } catch (e: IOException) {
            handleUnexpectedException(e)
            throw UnsupportedOperationException("wmic parsing failed!")
        } catch (e: InterruptedException) {
            handleUnexpectedException(e)
            throw UnsupportedOperationException("wmic parsing failed!")
        }

        return pids.toTypedArray()
    }

    private fun getPidsOnUnix(): Array<String> {
        val pids = ArrayList<String>()
        val r = Runtime.getRuntime()
        val process: Process
        try {
            process = r.exec("pgrep -f gradle-launcher")
            process.waitFor()

            if (process.exitValue() != 0 && process.exitValue() != 1) { //OK found, OK not found
                throw UnsupportedOperationException("pgrep returned error value!")
            } else {
                val b = BufferedReader(InputStreamReader(process.inputStream))
                var line: String? = b.readLine()

                while (line != null) {
                    pids.add(line)
                    line = b.readLine()
                }

                b.close()
            }
        } catch (e: IOException) {
            handleUnexpectedException(e)
            throw UnsupportedOperationException("pgrep parsing failed!")
        } catch (e: InterruptedException) {
            handleUnexpectedException(e)
            throw UnsupportedOperationException("pgrep parsing failed!")
        }

        return pids.toTypedArray()
    }

    private fun killProcess(pid: String): Boolean {
        return if (System.getProperty("os.name").startsWith("Windows")) {
            killProcessOnWindows(pid)
        } else {
            killProcessOnUnix(pid)
        }
    }

    private fun killProcessOnWindows(pid: String): Boolean {
        val r = Runtime.getRuntime()
        val p: Process
        return try {
            p = r.exec("taskkill /F /PID $pid")
            p.waitFor()
            true
        } catch (e: IOException) {
            handleUnexpectedException(e)
            false
        } catch (e: InterruptedException) {
            handleUnexpectedException(e)
            false
        }
    }

    private fun killProcessOnUnix(pid: String): Boolean {
        val r = Runtime.getRuntime()
        val p: Process
        return try {
            p = r.exec("kill -9 $pid")
            p.waitFor()
            p.exitValue() == 0
        } catch (e: IOException) {
            handleUnexpectedException(e)
            false
        } catch (e: InterruptedException) {
            handleUnexpectedException(e)
            false
        }
    }

    private fun handleUnexpectedException(e: Exception) {
        val message = "期待您的反馈：\n" +
                "I would really appreciate if you file this issue here:\n" +
                "$GITHUB_LINK\n"
        PluginManager.getLogger().error(message, e)
    }

    private fun showNotification(text: String, error: Boolean) {
        val n = Notification("cn.renyuzhuo.easygradle", TITLE, text,
                if (error) NotificationType.ERROR else NotificationType.INFORMATION)
        Notifications.Bus.notify(n)
    }

}