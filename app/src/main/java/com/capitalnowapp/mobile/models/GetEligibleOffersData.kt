package com.capitalnowapp.mobile.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable


class GetEligibleOffersData : Serializable {
    @SerializedName("loan_types")
    @Expose
    var loanTypes: List<GetEligibleOffersLoanType>? = null

    @SerializedName("remember")
    @Expose
    var remember: List<String>? = null



}
