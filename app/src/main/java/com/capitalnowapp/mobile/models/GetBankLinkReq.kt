package com.capitalnowapp.mobile.models

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName
import java.io.Serializable


class GetBankLinkReq : Serializable{
    @SerializedName("api_key")
    @Expose
    var apiKey: String? = null

    @SerializedName("user_id")
    @Expose
    var userId: String? = null

    @SerializedName("bank_code")
    @Expose
    var bankCode: String? = null

    @SerializedName("token")
    @Expose
    private var usertoken: String? = null

    @SerializedName("referrer")
    @Expose
    var referrer: String? = null

    @SerializedName("mob_no")
    @Expose
    var mobNo: String? = null

    fun setUsertoken(usertoken: String?) {
        this.usertoken = usertoken
    }
}