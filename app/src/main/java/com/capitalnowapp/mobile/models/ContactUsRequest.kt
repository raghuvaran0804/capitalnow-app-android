package com.capitalnowapp.mobile.models

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class ContactUsRequest: Serializable {

    @SerializedName("api_key")
    @Expose
    private var apiKey: String? = null

    @SerializedName("user_id")
    @Expose
    private var userId: String? = null

    @SerializedName("name")
    @Expose
    private var name: String? = null

    @SerializedName("mail_id")
    @Expose
    private var mailId: String? = null

    @SerializedName("message")
    @Expose
    private var message: String? = null

    @SerializedName("token")
    @Expose
    private var usertoken: String? = null

    fun setUsertoken(usertoken: String?) {
        this.usertoken = usertoken
    }

    fun getApiKey(): String? {
        return apiKey
    }

    fun setApiKey(apiKey: String?) {
        this.apiKey = apiKey
    }

    fun getUserId(): String? {
        return userId
    }

    fun setUserId(userId: String?) {
        this.userId = userId
    }

    fun getName(): String? {
        return name
    }

    fun setName(name: String?) {
        this.name = name
    }

    fun getMailId(): String? {
        return mailId
    }

    fun setMailId(mailId: String?) {
        this.mailId = mailId
    }

    fun getMessage(): String? {
        return message
    }

    fun setMessage(message: String?) {
        this.message = message
    }

}