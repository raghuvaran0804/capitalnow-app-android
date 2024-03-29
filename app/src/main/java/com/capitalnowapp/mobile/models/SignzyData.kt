package com.capitalnowapp.mobile.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable


class SignzyData : Serializable{

    @SerializedName("signzy_url")
    @Expose
    var signzyUrl: String? = null

    @SerializedName("request_id")
    @Expose
    var requestId: String? = null

    @SerializedName("auth_token")
    @Expose
    var authToken: String? = null

}
