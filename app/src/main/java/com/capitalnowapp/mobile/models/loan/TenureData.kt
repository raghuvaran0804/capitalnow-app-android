package com.capitalnowapp.mobile.models.loan

import com.capitalnowapp.mobile.beans.UserTermsData
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class TenureData : Serializable {
    @SerializedName("title")
    @Expose
    var title: String? = null

    @SerializedName("eligibility_message")
    @Expose
    var eligibilityMessage: String? = null

    @SerializedName("enach_message")
    @Expose
    var enachMessage: String? = null

    @SerializedName("type")
    @Expose
    var type: String? = null

    @SerializedName("max_days_message")
    @Expose
    var maxDaysMessage: String? = null

    @SerializedName("min_amount")
    @Expose
    var minAmount: Int? = null

    @SerializedName("max_amount")
    @Expose
    var maxAmount: Int? = null

    @SerializedName("max_days")
    @Expose
    var maxDays: Int? = null

    @SerializedName("instalments")
    @Expose
    var instalments: List<InstalmentData>? = null

    @SerializedName("processing_fee")
    @Expose
    var processingFee: String? = null

    @SerializedName("insurance_perc")
    @Expose
    var insurancePerc: String? = null

    @SerializedName("processing_text")
    @Expose
    var processingText: String? = null

    @SerializedName("min_processing_fee")
    @Expose
    var minProcessingFee: Int? = null

    @SerializedName("tanc_text")
    @Expose
    val userTermsData: UserTermsData? = null

    @SerializedName("emi_count")
    @Expose
    val emiCount : String? = null

    @SerializedName("insurance")
    @Expose
    var insurance: List<Insurance>? = null
}