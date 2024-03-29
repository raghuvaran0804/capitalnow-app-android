package com.capitalnowapp.mobile.models.offerModel

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable


class CSActiveLoanData : Serializable{

    @SerializedName("loan_data")
    @Expose
    var loanData: CSLoanData? = null

    @SerializedName("installments")
    @Expose
    var installments: List<CSInstallment>? = null

}
