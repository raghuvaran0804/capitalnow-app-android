package com.capitalnowapp.mobile.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable


class EligibleOfferDetailsInstalment : Serializable{

    @SerializedName("emi_amount")
    @Expose
    var emiAmount: String? = null

    @SerializedName("discount_amount")
    @Expose
    var discountAmount: String? = null

    @SerializedName("due_date")
    @Expose
    var dueDate: String? = null

    @SerializedName("principal_amount_title")
    @Expose
    var principalAmountTitle: String? = null

    @SerializedName("principal_amount")
    @Expose
    var principalAmount: String? = null

    @SerializedName("interest_amount")
    @Expose
    var interestAmount: String? = null

    @SerializedName("discount_interest_amount")
    @Expose
    var discountInterestAmount: String? = null

}
