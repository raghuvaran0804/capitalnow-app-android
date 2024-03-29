package com.capitalnowapp.mobile.models

import com.capitalnowapp.mobile.beans.UserTermsData
import com.capitalnowapp.mobile.models.loan.EmiDateRanges
import com.capitalnowapp.mobile.models.loan.InstalmentData
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable


class TwlTenureData : Serializable {

    @SerializedName("title")
    @Expose
    val title: String? = null

    @SerializedName("type")
    @Expose
     val type: Int? = null

    @SerializedName("emi_count")
    @Expose
     val emiCount: String? = null

    @SerializedName("max_days")
    @Expose
     val maxDays: Int? = null

    @SerializedName("due_days")
    @Expose
     val dueDays: Int? = null

    @SerializedName("max_days_message")
    @Expose
     val maxDaysMessage: String? = null

    @SerializedName("min_amount")
    @Expose
     val minAmount: Int? = null

    @SerializedName("max_amount")
    @Expose
     val maxAmount: String? = null

    @SerializedName("instalments")
    @Expose
     val instalments: List<InstalmentData>? = null

    @SerializedName("eligibility_message")
    @Expose
     val eligibilityMessage: String? = null

    @SerializedName("processing_fee")
    @Expose
     val processingFee: String? = null

    @SerializedName("processing_fee_type")
    @Expose
     val processingFeeType: String? = null

    @SerializedName("cals")
    @Expose
     val cals: String? = null

    @SerializedName("cals_min_am")
    @Expose
     val calsMinAm: String? = null

    @SerializedName("cals_replace_str")
    @Expose
     val calsReplaceStr: String? = null

    @SerializedName("processing_text")
    @Expose
     val processingText: String? = null

    @SerializedName("min_processing_fee")
    @Expose
     val minProcessingFee: Double? = null

    @SerializedName("tanc_text")
    @Expose
     val userTermsData: UserTermsData? = null

    @SerializedName("enach_message")
    @Expose
    var eNachMessage: String? = null

    @SerializedName("start_month")
    @Expose
    val startMonth : String? = null

    @SerializedName("preferred_date")
    @Expose
    val preferredDate : Int? = null

    @SerializedName("emi_date_ranges")
    @Expose
    var emiDateRanges: EmiDateRanges? = null

}