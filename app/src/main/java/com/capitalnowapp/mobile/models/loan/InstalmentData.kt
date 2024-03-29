package com.capitalnowapp.mobile.models.loan

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class InstalmentData : Serializable {
    var installmentDueDate: String? = null

    @SerializedName("title")
    @Expose
    var title: String? = null

    @SerializedName("tenure_days")
    @Expose
    var tenureDays: Double? = null

    @SerializedName("due_days")
    @Expose
    var due_days: Double? = null

    @SerializedName("barrow_amount")
    @Expose
    var barrowAmount: Double? = null

    @SerializedName("interest_fee")
    @Expose
    var interestFee: Double? = null

    @SerializedName("processing_fee")
    @Expose
    var processingFee: Double? = null

    @SerializedName("emi_count")
    @Expose
    val emiCount : String? = null
}