package com.capitalnowapp.mobile.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable


class VkycData : Serializable{

    @SerializedName("id")
    @Expose
    var id: String? = null

    @SerializedName("reference_id")
    @Expose
    var referenceId: String? = null

    @SerializedName("user_id")
    @Expose
    var userId: String? = null

    @SerializedName("webview_url")
    @Expose
    var webviewUrl: String? = null

    @SerializedName("redirection_url")
    @Expose
    var redirectionUrl: String? = null

}
