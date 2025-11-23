package org.kobalt

import javafx.scene.web.WebEngine

class Bridge(private val webEngine: WebEngine) {

    fun sendMessage(message: String) {
        println("LOG: (sent message) : $message")
    }

    fun sendToJavaScript(message: String) {
        val escapedString = message.replace("'", "\\'")
        // Ensure this runs on the JavaFX Application Thread if not already
        javafx.application.Platform.runLater {
            webEngine.executeScript("receiveMessage('${escapedString}')")
        }
        println("LOG: (received message) : $message")
    }
}
