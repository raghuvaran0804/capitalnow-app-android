package com.capitalnowapp.mobile.models.loan

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class BankArray {
    @SerializedName("title")
    @Expose
    var title: String? = null

    @SerializedName("values")
    @Expose
    var values: List<UpiArray>? = null
}