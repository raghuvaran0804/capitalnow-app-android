package com.capitalnowapp.mobile.models.loan

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class PriceBreakup {
    @SerializedName("title")
    @Expose
    var title: String? = null

    @SerializedName("value")
    @Expose
    var value: Int? = null

    @SerializedName("color")
    @Expose
    var color: String? = null
}