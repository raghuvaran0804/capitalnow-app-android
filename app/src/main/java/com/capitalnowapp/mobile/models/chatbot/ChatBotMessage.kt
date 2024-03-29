package com.capitalnowapp.mobile.models.chatbot

class ChatBotMessage {

    public var messageType: String? = null
    public var message: String? = null

    public var questionsList : ArrayList<String>? = null

    public var optionCount = 0
    public var option1 = ""
    public var option2 = ""
    public var option3 = ""
    public var option4 = ""

    public var selectedOption = ""
}