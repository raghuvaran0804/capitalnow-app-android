package com.capitalnowapp.mobile.kotlin.activities

import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.capitalnowapp.mobile.R
import com.capitalnowapp.mobile.activities.BaseActivity
import com.capitalnowapp.mobile.constants.Constants
import com.capitalnowapp.mobile.kotlin.adapters.chatbot.ChatBotCustomAdapter
import com.capitalnowapp.mobile.models.chatbot.ChatBotMessage
import kotlinx.android.synthetic.main.activity_chat.rvMessages

class ChatActivity : BaseActivity() {
    private val TAG = "MainActivity"
    private lateinit var adapterChatBot: ChatBotCustomAdapter
    var messageList: ArrayList<ChatBotMessage> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            setContentView(R.layout.activity_chat_new)

            rvMessages.layoutManager = LinearLayoutManager(this)
            seyHello()
            setQuestions()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun seyHello() {
        val msg = ChatBotMessage()
        msg.messageType = Constants.CHATBOT.SAY_HELLO
        msg.message = userDetails.firstName
        messageList.add(msg)
        adapterChatBot = ChatBotCustomAdapter(messageList, this)
        rvMessages.adapter = adapterChatBot
    }

    private fun setQuestions() {

        val msg = ChatBotMessage()
        msg.messageType = Constants.CHATBOT.QUESTIONS
        msg.questionsList = ArrayList()

        msg.questionsList!!.add(getString(R.string.apply_loan))
        msg.questionsList!!.add(getString(R.string.loan_status))
        msg.questionsList!!.add(getString(R.string.upload_documents))
        msg.questionsList!!.add(getString(R.string.request_bank_chnage))
        msg.questionsList!!.add(getString(R.string.outstanding))

        messageList.add(msg)
        adapterChatBot.reloadMsgs(messageList)
    }

    fun selectedQuestion(message: String) {
        val msg = ChatBotMessage()
        msg.messageType = Constants.CHATBOT.SELECTED_QUESTION
        msg.message = message

        messageList.add(msg)
        when (message) {
            getString(R.string.apply_loan) -> {
                msg.messageType = Constants.CHATBOT.BOT_MESSAGE
                msg.message = getString(R.string.cb_apply_loan_msg)
                msg.optionCount = 2
                msg.option1 = getString(R.string.proceed_contactus)
                msg.option2 = getString(R.string.proceed_email_support)
            }
            getString(R.string.loan_status) -> {
                msg.messageType = Constants.CHATBOT.BOT_MESSAGE
                msg.message = getString(R.string.cb_loan_status_msg)
                msg.optionCount = 3
                msg.option1 = getString(R.string.proceed_contactus)
                msg.option2 = getString(R.string.proceed_email_support)
                msg.option3 = getString(R.string.proceed_active_loans)
            }
            getString(R.string.upload_documents) -> {
                msg.messageType = Constants.CHATBOT.BOT_MESSAGE
                msg.message = getString(R.string.cb_upload_doc_msg)
                msg.optionCount = 1
                msg.option1 = getString(R.string.proceed_upload_doc)

            }
            getString(R.string.request_bank_chnage) -> {
                msg.messageType = Constants.CHATBOT.BOT_MESSAGE
                msg.message = getString(R.string.cb_request_bank_change_msg)
                msg.optionCount = 1
                msg.option1 = getString(R.string.proceed_request_bank_change)
            }
            getString(R.string.rewards) -> {
                msg.messageType = Constants.CHATBOT.BOT_MESSAGE
                msg.message = getString(R.string.cb_rewards_msg)
                msg.optionCount = 1
                msg.option1 = getString(R.string.proceed_rewards)
            }
        }

        adapterChatBot.reloadMsgs(messageList)
    }

    fun selectedOption(msg: ChatBotMessage) {
        when (msg.selectedOption) {
            getString(R.string.proceed_contactus) -> {
                proceedToContactUs()
            }
            getString(R.string.proceed_email_support) -> {
                proceedToEmailSupport()
            }
            getString(R.string.proceed_active_loans) -> {
                proceedToActiveLoans()
            }
            getString(R.string.proceed_upload_doc) -> {
                proceedToUploadDoc()
            }
            getString(R.string.proceed_request_bank_change) -> {
                proceedToRBC()
            }
            getString(R.string.proceed_rewards) -> {
                proceedToRewards()
            }
        }
    }
}
