package com.capitalnowapp.mobile.models.offerModel

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable


class TancText : Serializable{

    @SerializedName("message")
    @Expose
    var message: String? = null

    @SerializedName("consent_key")
    @Expose
    var consentKey: String? = null

    @SerializedName("find_text")
    @Expose
    var findText: String? = null

    @SerializedName("replace_links")
    @Expose
    var replaceLinks: String? = null


}
