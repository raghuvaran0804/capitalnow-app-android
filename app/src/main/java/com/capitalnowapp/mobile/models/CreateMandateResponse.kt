package com.capitalnowapp.mobile.models

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName
import java.io.Serializable


class CreateMandateResponse : Serializable{
    
    @SerializedName("razor_pay_api_key")
    @Expose
     val razorPayApiKey: String? = null

    @SerializedName("status")
    @Expose
     val status: String? = null

    @SerializedName("status_code")
    @Expose
     val statusCode: Int? = null

    @SerializedName("message")
    @Expose
     val message: String? = null

    @SerializedName("data")
    @Expose
     val data: MandateDetails? = null
}