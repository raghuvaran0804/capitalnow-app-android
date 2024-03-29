package com.capitalnowapp.mobile.models.loan

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable


class Insurance : Serializable{

    @SerializedName("min")
    @Expose
    var min: Int? = null

    @SerializedName("max")
    @Expose
    var max: Int? = null

    @SerializedName("can_applicable")
    @Expose
    var canApplicable: Boolean? = null

    @SerializedName("perc_type")
    @Expose
    var percType: Int? = null

    @SerializedName("perc")
    @Expose
    var perc: String? = null

    @SerializedName("tnc_str1")
    @Expose
    var tncStr1: String? = null

    @SerializedName("tnc_str2")
    @Expose
    var tncStr2: String? = null

    @SerializedName("partner")
    @Expose
    var partner: String? = null

    @SerializedName("tnc_link")
    @Expose
    var tncLink: String? = null

}
