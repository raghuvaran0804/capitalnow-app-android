package com.capitalnowapp.mobile.models.loan

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class LoansToPay {
    @SerializedName("lid")
    @Expose
    var lid: String? = null

    @SerializedName("qcl_id")
    @Expose
    var qclId: String? = null

    @SerializedName("loan_ex_days")
    @Expose
    var loanExDays: Int? = null

    @SerializedName("loan_issue_date")
    @Expose
    var loanIssueDate: String? = null

    @SerializedName("borrow_amount")
    @Expose
    var borrowAmount: Int? = null

    @SerializedName("amt_payable")
    @Expose
    var amtPayable: List<AmtPayable>? = null

    @SerializedName("amt_paid")
    @Expose
    var amtPaid: AmtPaid? = null

    @SerializedName("insurance_data")
    @Expose
    var insuranceData: InsuranceData? = null
}