package com.capitalnowapp.mobile.models.offerModel

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable


class OfferWebLinkData : Serializable{
    @SerializedName("webview_url")
    @Expose
    var webviewUrl: String? = null
}
