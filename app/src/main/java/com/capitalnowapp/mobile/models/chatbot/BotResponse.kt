package com.capitalnowapp.mobile.models.chatbot

object BotResponse {
    fun basicResponses(_message: String): String {

        val random = (0..2).random()
        val message =_message
        return when {
            //Hello
            message.contains("hello") || message.contains("hai") || message.contains("hi") -> {
                     "What do want to know about?"
            }
            //How are you?
            message.contains("how are you") -> {
                when (random) {
                    0 -> "I'm doing fine, thanks!How about you?"
                    1 -> "Pretty good! How about you?"
                    else -> "error"
                }
            }
            //When the programme doesn't understand...
            else -> {
                when (random) {
                    0 -> "I don't understand..."
                    1 -> "Try asking me something different"
                    2 -> "Idk"
                    else -> "error"
                }
            }
        }
    }
}