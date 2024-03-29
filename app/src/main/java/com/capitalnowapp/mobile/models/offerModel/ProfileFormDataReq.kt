package com.capitalnowapp.mobile.models.offerModel

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable


class ProfileFormDataReq :Serializable {

    @SerializedName("api_key")
    @Expose
    var apiKey: String? = null

    @SerializedName("page_no")
    @Expose
    var pageNo: Int? = null

    var additionalProperties: Map<String, String> = HashMap()
}