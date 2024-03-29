package com.capitalnowapp.mobile.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable


class EligibleOfferDetailsData : Serializable {
    @SerializedName("discount_message")
    @Expose
    var discountMessage: String? = null

    @SerializedName("instalments")
    @Expose
    var instalments: List<EligibleOfferDetailsInstalment>? = null

    @SerializedName("loan_summery")
    @Expose
    var loanSummery: EligibleOfferDetailsLoanSummery? = null

    @SerializedName("tanc_text")
    @Expose
    var tancText: EligibleOfferDetailsTancText? = null

}
