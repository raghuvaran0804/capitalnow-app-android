package com.capitalnowapp.mobile.models.loan

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class AmtPayable {
    @SerializedName("title")
    @Expose
    var title: String? = null

    @SerializedName("id")
    @Expose
    var id: Int? = -1

    @SerializedName("pay_type")
    @Expose
    var payType: String? = null

    @SerializedName("ins_number")
    @Expose
    var insNumber: Int? = null

    @SerializedName("due_date")
    @Expose
    var dueDate: String? = null

    @SerializedName("due_amount")
    @Expose
    var dueAmount: Int? = null

    @SerializedName("is_recommended")
    @Expose
    var isRecommended: Boolean? = null

    @SerializedName("recommended_text")
    @Expose
    var recommendedText: String? = null

    @SerializedName("recommended_desc")
    @Expose
    var recommendedDesc: String? = null

    @SerializedName("save_amount_text")
    @Expose
    var saveAmountText: String? = null

    @SerializedName("price_breakup")
    @Expose
    var priceBreakup: List<PriceBreakup>? = null

    @SerializedName("offer_details")
    @Expose
    var offerDetails: OfferDetails? = null

    var isSelected: Boolean? = false
    var lid = ""
}