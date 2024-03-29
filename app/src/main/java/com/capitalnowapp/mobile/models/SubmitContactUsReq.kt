package com.capitalnowapp.mobile.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable


class SubmitContactUsReq : Serializable {

    @SerializedName("api_key")
    @Expose
    var apiKey: String? = null

    @SerializedName("web_cnt_existing_customer")
    @Expose
    var webCntExistingCustomer: String? = null

    @SerializedName("web_cnt_name")
    @Expose
    var webCntName: String? = null

    @SerializedName("web_cnt_mobile_number")
    @Expose
    var webCntMobileNumber: String? = null

    @SerializedName("web_cnt_query")
    @Expose
    var webCntQuery: String? = null

    @SerializedName("web_cnt_sub_query")
    @Expose
    var webCntSubQuery: String? = null

    @SerializedName("web_cnt_email")
    @Expose
    var webCntEmail: String? = null

    @SerializedName("web_cnt_message")
    @Expose
    var webCntMessage: String? = null

    @SerializedName("platform")
    @Expose
    var platform: String? = null
}