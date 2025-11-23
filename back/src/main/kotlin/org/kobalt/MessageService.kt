package org.kobalt

import java.util.*
import kotlin.random.Random

class MessageService(private val bridge: Bridge) {

    private val messageTimer = Timer("MessageScheduler", true)

    fun startScheduling() {
        scheduleRandomMessage()
    }

    fun stopScheduling() {
        messageTimer.cancel()
    }

    private fun scheduleRandomMessage() {
        // Generate a random delay between 0 and 10000 milliseconds (0 and 10 seconds)
        val delayMs = Random.nextLong(0, 10001)
        val currentMessage = "PING sent after ${delayMs / 1000.0} seconds"

        println("SCHEDULING: Next PING in $delayMs ms.")

        // Use Timer to schedule the task
        messageTimer.schedule(object : TimerTask() {
            override fun run() {
                // Task 1: Execute the UI update (send the message)
                bridge.sendToJavaScript(currentMessage)

                // Task 2: Re-schedule the next iteration to create the loop
                scheduleRandomMessage()
            }
        }, delayMs)
    }
}
