package com.capitalnowapp.mobile.models

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName
import java.io.Serializable


class SkipPanData :Serializable {
    @SerializedName("user_id")
    @Expose
    var userId: String? = null

    @SerializedName("pan_skip")
    @Expose
    var panSkip: String? = null

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