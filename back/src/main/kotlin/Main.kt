package org.kobalt

import javafx.application.Application
import javafx.application.Platform
import javafx.concurrent.Worker
import javafx.scene.Scene
import javafx.scene.layout.StackPane
import javafx.scene.web.WebEngine
import javafx.scene.web.WebView
import javafx.stage.Stage
import netscape.javascript.JSObject
import java.util.*
import kotlin.random.Random


class Main : Application() {

    private lateinit var webEngine: WebEngine

    private val messageTimer = Timer("MessageScheduler", true)

    // 💡 NEW FUNCTION: Schedules a single message with a random delay
    private fun scheduleRandomMessage() {
        // Generate a random delay between 0 and 10000 milliseconds (0 and 10 seconds)
        val delayMs = Random.nextLong(0, 10001)
        val currentMessage = "PING sent after ${delayMs / 1000.0} seconds"

        println("SCHEDULING: Next PING in $delayMs ms.")

        // Use Timer to schedule the task
        messageTimer.schedule(object : TimerTask() {
            override fun run() {
                // Task 1: Execute the UI update (send the message)
                Platform.runLater {
                    sendToJavaScript(currentMessage)
                }

                // Task 2: Re-schedule the next iteration to create the loop
                scheduleRandomMessage()
            }
        }, delayMs)
    }

    // Function to send data to the JavaScript front-end
    private fun sendToJavaScript(message: String) {
        val escapedString = message.replace("'", "\\'")
        webEngine.executeScript("receiveMessage('${escapedString}')")
        println("LOG: (received message) : $message")
    }
    class KotlinBridge {
        fun sendMessage(message: String){
            println("LOG: (sent message) : $message")
        }
    }
    override fun start(stage: Stage?) {
        val webView = WebView()
        webEngine = webView.engine
        val resourcePath = "/web/index.html"
        val url = javaClass.getResource(resourcePath)
            ?: throw IllegalStateException("Resource not found: $resourcePath")


        // 1. Start loading the page
        webEngine.load(url.toExternalForm())

        // 2. Add a listener to wait for the page to finish loading
        webEngine.loadWorker.stateProperty().addListener { _, _, newState ->
            if (newState == Worker.State.SUCCEEDED) {
                // 3. THIS IS THE CRITICAL STEP: Set the member only when the page is ready
                try {
                    val win = webEngine.executeScript("window") as JSObject
                    win.setMember("app", KotlinBridge())
                    println("LOG: Kotlin bridge successfully injected as 'window.app'")

                    scheduleRandomMessage()

                } catch (e: Exception) {
                    println("ERROR: Failed to inject bridge: ${e.message}")
                }
            }
        }

        val scene = Scene(StackPane(webView), 640.0, 480.0)

        if (stage != null) {
            stage.scene = scene
            stage.show()
        }

        stage?.setOnCloseRequest {
            messageTimer.cancel()
            Platform.exit()
        }
    }
}

