package com.capitalnowapp.mobile.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable


class TwlActiveLoanData :Serializable {
    @SerializedName("lid")
    @Expose
    var lid: String? = null

    @SerializedName("twl_id")
    @Expose
    var twlId: String? = null

    @SerializedName("loan_ex_days")
    @Expose
    var loanExDays: String? = null

    @SerializedName("loan_issue_date")
    @Expose
    var loanIssueDate: String? = null

    @SerializedName("loan_due_date")
    @Expose
    var loanDueDate: String? = null

    @SerializedName("borrow_amount")
    @Expose
    var borrowAmount: String? = null

    @SerializedName("amt_payable")
    @Expose
    var twlamtPayable: List<TwlAmtPayable>? = null
}