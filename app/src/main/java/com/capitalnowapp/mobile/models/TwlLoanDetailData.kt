package com.capitalnowapp.mobile.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable


class TwlLoanDetailData :Serializable {

    @SerializedName("lid")
    @Expose
    var lid: String? = null

    @SerializedName("twl_id")
    @Expose
    var twlId: String? = null

    @SerializedName("emi_list")
    @Expose
    var twlEmiDetailsList: List<TwlEmiDetailsList>? = null
}