package cn.renyuzhuo.easygradle.updater

import com.google.gson.Gson
import com.intellij.ide.plugins.PluginManager
import com.intellij.notification.Notification
import com.intellij.notification.NotificationType
import com.intellij.notification.Notifications
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.URL
import javax.net.ssl.HttpsURLConnection

class UpdateGradleAction : AnAction() {

    private val TITLE = "Easy Gradle"
    private val GITHUB_LINK = "https://github.com/renyuzhuo/easygradle/issues"

    override fun actionPerformed(action: AnActionEvent) {
        try {
            val url = URL("https://services.gradle.org/versions/current")
            val httpsURLConnection = url.openConnection() as HttpsURLConnection

            val responseCode = httpsURLConnection.responseCode
            System.out.println("\nSending 'GET' request to URL : $url")
            println("Response Code : $responseCode")

            var inputLine: String?
            val response = StringBuffer()

            if (responseCode == 200) {
                val read = BufferedReader(InputStreamReader(httpsURLConnection.inputStream))
                inputLine = read.readLine()
                while (inputLine != null) {
                    response.append(inputLine)
                    inputLine = read.readLine()
                }
                println(response.toString())
                read.close()
            }

            val gradleItem = Gson().fromJson<GradleItem>(response.toString(), GradleItem::class.java)
            println(response.toString())
            showNotification(gradleItem.toShowString(), false)

        } catch (e: Exception) {
            e.printStackTrace()
            handleUnexpectedException(e)
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
