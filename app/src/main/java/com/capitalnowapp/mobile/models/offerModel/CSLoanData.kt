package com.capitalnowapp.mobile.models.offerModel

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable


class CSLoanData : Serializable{

    @SerializedName("reason")
    @Expose
    var reason: String? = null

    @SerializedName("loan_amount")
    @Expose
    var loanAmount: Int? = null

    @SerializedName("pcl_emi")
    @Expose
    var pclEmi: Int? = null

    @SerializedName("pcl_tenure")
    @Expose
    var pclTenure: Int? = null

    @SerializedName("loan_id")
    @Expose
    var loanId: String? = null

    @SerializedName("utr_number")
    @Expose
    var utrNumber: Any? = null

    @SerializedName("auto_debit")
    @Expose
    var autoDebit: Int? = null

    @SerializedName("loan_is_offered_by")
    @Expose
    var loanIsOfferedBy: String? = null

}
