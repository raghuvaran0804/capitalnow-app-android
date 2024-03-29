package com.capitalnowapp.mobile.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable


class RazorPayData : Serializable {

    @SerializedName("webview_url")
    @Expose
    var webviewUrl: String? = null

    @SerializedName("reference_id")
    @Expose
    var referenceId: String? = null

    @SerializedName("redirection_url")
    @Expose
    var redirectionUrl: String? = null

    @SerializedName("user_id")
    @Expose
    var userId: Int? = null

    @SerializedName("mandate_id")
    @Expose
    var mandateId: String? = null
}