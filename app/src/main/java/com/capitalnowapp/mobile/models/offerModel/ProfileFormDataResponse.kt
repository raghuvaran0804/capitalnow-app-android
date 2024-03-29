package com.capitalnowapp.mobile.models.offerModel

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable


class ProfileFormDataResponse : Serializable {
    @SerializedName("status")
    @Expose
    var status: Boolean? = null

    @SerializedName("code")
    @Expose
    var code: Int? = null

    @SerializedName("message")
    @Expose
    var message: String? = null

    @SerializedName("data")
    @Expose
    var profileformData: ProfileFormData? = null

    @SerializedName("help")
    @Expose
    var offerHelp: OfferHelp? = null

    @SerializedName("partner_info")
    @Expose
    var partnerInfo: PartnerInfo? = null


}