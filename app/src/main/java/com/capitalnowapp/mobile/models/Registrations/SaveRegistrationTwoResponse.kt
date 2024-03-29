package com.capitalnowapp.mobile.models.Registrations

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class SaveRegistrationTwoResponse : Serializable {

    @SerializedName("status")
    @Expose
    var status: Boolean? = null

    @SerializedName("code")
    @Expose
    var code: Int? = null

    @SerializedName("message")
    @Expose
    var message: String? = null

    @SerializedName("status_code")
    @Expose
    val statusCode: Int? = null

    @SerializedName("data")
    @Expose
    var data: saveRegTwoResData? = null

}
