package org.kobalt

import javafx.application.Application
import javafx.application.Platform
import javafx.concurrent.Worker
import javafx.scene.Scene
import javafx.scene.layout.StackPane
import javafx.scene.web.WebView
import javafx.stage.Stage
import netscape.javascript.JSObject

class Main : Application() {

    private lateinit var messageService: MessageService

    override fun start(stage: Stage?) {
        val webView = WebView()
        val webEngine = webView.engine
        
        // Initialize Bridge and Service
        val bridge = Bridge(webEngine)
        messageService = MessageService(bridge)

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
                    win.setMember("app", bridge)
                    println("LOG: Kotlin bridge successfully injected as 'window.app'")

                    messageService.startScheduling()

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
            messageService.stopScheduling()
            Platform.exit()
        }
    }
}

