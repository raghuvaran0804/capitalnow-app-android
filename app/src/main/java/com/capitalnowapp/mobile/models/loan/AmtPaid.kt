package com.capitalnowapp.mobile.models.loan

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class AmtPaid {
    @SerializedName("title")
    @Expose
    var title: String? = null

    @SerializedName("Amount")
    @Expose
    var amount: Int? = null
}