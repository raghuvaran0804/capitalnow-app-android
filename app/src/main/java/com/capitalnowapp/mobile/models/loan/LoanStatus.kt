package com.capitalnowapp.mobile.models.loan

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class LoanStatus : Serializable {
    @SerializedName("title")
    @Expose
    val title: String? = null

    @SerializedName("status")
    @Expose
    var status: Int? = null

}