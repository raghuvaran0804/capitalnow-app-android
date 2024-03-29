package com.capitalnowapp.mobile.models

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName




class GetHoldStatusResponse {

    @SerializedName("razor_pay_api_key")
    @Expose
    var razorPayApiKey: String? = null

    @SerializedName("bank_change_type")
    @Expose
    var bankChangeType: String? = null

    @SerializedName("status")
    @Expose
    var status: Boolean? = null

    @SerializedName("message")
    @Expose
    var message: String? = null

    @SerializedName("status_code")
    @Expose
    var statusCode: Int? = null

    @SerializedName("data")
    @Expose
    var data: getHoldStatusData? = null

}
