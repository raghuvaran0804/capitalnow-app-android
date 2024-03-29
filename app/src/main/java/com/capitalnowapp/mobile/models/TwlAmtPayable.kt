package com.capitalnowapp.mobile.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable


class TwlAmtPayable :Serializable {
    @SerializedName("id")
    @Expose
    var id: Int? = null

    @SerializedName("title")
    @Expose
    var title: String? = null

    @SerializedName("pay_type")
    @Expose
    var payType: String? = null

    @SerializedName("ins_number")
    @Expose
    var insNumber: Int? = null

    @SerializedName("due_date")
    @Expose
    var dueDate: String? = null

    @SerializedName("due_type")
    @Expose
    var dueType: Int? = null

    @SerializedName("due_amount")
    @Expose
    var dueAmount: Int? = null

    @SerializedName("recommended_img")
    @Expose
    var recommendedImg: String? = null

    @SerializedName("due_text")
    @Expose
    var dueText: String? = null

    @SerializedName("recommended_text")
    @Expose
    var recommendedText: String? = null

    @SerializedName("lid")
    @Expose
    var lid: String? = null

}