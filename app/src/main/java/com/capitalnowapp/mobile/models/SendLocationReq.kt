package com.capitalnowapp.mobile.models

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName
import java.io.Serializable


class SendLocationReq : Serializable{
    @SerializedName("state")
    @Expose
     var state: String? = null

    @SerializedName("addressline2")
    @Expose
     var addressline2: String? = null

    @SerializedName("area")
    @Expose
    var area: String? = null

    @SerializedName("city")
    @Expose
     var city: String? = null

    @SerializedName("user_id")
    @Expose
     var userId: String? = null

    @SerializedName("addressline1")
    @Expose
     var addressline1: String? = null

    @SerializedName("api_key")
    @Expose
     var apiKey: String? = null

    @SerializedName("pincode")
    @Expose
     var pincode: String? = null

    @SerializedName("token")
    @Expose
    private var usertoken: String? = null


    fun setUsertoken(usertoken: String?) {
        this.usertoken = usertoken
    }

}