package com.capitalnowapp.mobile.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable


class TwlEmiDetailsList :Serializable {

    @SerializedName("id")
    @Expose
    var id: Int? = null

    @SerializedName("title")
    @Expose
    var title: String? = null

    @SerializedName("ins_number")
    @Expose
    var insNumber: Int? = null

    @SerializedName("ins_due_date")
    @Expose
    var insDueDate: String? = null

    @SerializedName("ins_repay_date")
    @Expose
    var insRepayDate: String? = null

    @SerializedName("ins_due_amount")
    @Expose
    var insDueAmount: Int? = null

    @SerializedName("ins_status")
    @Expose
    var insStatus: String? = null

    @SerializedName("ins_status_id")
    @Expose
    var insStatusId: Int? = null

    @SerializedName("enach_text")
    @Expose
    var enachText: String? = null

    @SerializedName("paid_text")
    @Expose
    var paidText: String? = null
}