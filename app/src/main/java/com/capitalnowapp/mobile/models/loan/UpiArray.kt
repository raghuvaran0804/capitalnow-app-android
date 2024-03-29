package com.capitalnowapp.mobile.models.loan

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class UpiArray {
    @SerializedName("title")
    @Expose
    var title: String? = null

    @SerializedName("value")
    @Expose
    var value: String? = null

    @SerializedName("can_copy")
    @Expose
    var canCopy: Boolean? = null
}