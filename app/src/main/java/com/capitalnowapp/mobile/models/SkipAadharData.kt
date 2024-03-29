package com.capitalnowapp.mobile.models

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName
import java.io.Serializable


class SkipAadharData :Serializable {
    @SerializedName("user_id")
    @Expose
    var userId: String? = null

    @SerializedName("aadhar_skip")
    @Expose
    var aadharSkip: String? = null

    @SerializedName("api_key")
    @Expose
    var apiKey: String? = null

    @SerializedName("token")
    @Expose
    private var usertoken: String? = null


    fun setUsertoken(usertoken: String?) {
        this.usertoken = usertoken
    }
}