package com.capitalnowapp.mobile.models.offerModel

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable


class PartnerInfo :Serializable{

    @SerializedName("lending_partner_id")
    @Expose
    var lendingPartnerId: Int? = null

    @SerializedName("lp_logo")
    @Expose
    var lpLogo: String? = null

    @SerializedName("lp_name")
    @Expose
    var lpName: String? = null

    @SerializedName("lp_theme_color")
    @Expose
    var lpThemeColor: String? = null

}
