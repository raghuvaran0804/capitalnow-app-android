package com.capitalnowapp.mobile.models.offerModel

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable


class OfferBankLinkReq :Serializable {
    @SerializedName("api_key")
    @Expose
    var apiKey: String? = null

    @SerializedName("page_no")
    @Expose
    var pageNo: String? = null

    @SerializedName("type")
    @Expose
    var type: String? = null

    @SerializedName("institution_id")
    @Expose
    var institutionId: String? = null
}