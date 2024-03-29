package com.capitalnowapp.mobile.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable


class GetEligibleOffersLoanType : Serializable {
    @SerializedName("uct_id")
    @Expose
    var uctId: Int? = null

    @SerializedName("loan_type")
    @Expose
    var loanType: String? = null

    @SerializedName("is_checked")
    @Expose
    var isChecked: String? = null

    @SerializedName("emi_number_text")
    @Expose
    var emiNumberText: String? = null

    @SerializedName("emi_number")
    @Expose
    var emiNumber: String? = null

    @SerializedName("emi_amount")
    @Expose
    var emiAmount: String? = null

    @SerializedName("is_locked")
    @Expose
    var isLocked: Boolean? = null

    var checked: Boolean? = false

    var id: String? = null

}
