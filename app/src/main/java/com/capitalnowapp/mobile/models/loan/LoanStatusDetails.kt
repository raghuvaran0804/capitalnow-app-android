package com.capitalnowapp.mobile.models.loan

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class LoanStatusDetails : Serializable {
    @SerializedName("loan_status")
    @Expose
    val loanStatus: List<LoanStatus>? = null

    @SerializedName("lid")
    @Expose
    val lid: String? = null

    @SerializedName("title")
    @Expose
    val title: String? = null

    @SerializedName("show_rate_us")
    @Expose
    val showRateUs: Boolean = false
}