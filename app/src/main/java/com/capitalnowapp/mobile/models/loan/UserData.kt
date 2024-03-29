package com.capitalnowapp.mobile.models.loan

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class UserData {
    @SerializedName("loan_history_data")
    @Expose
    var loanHistoryData: List<LoanHistoryDatum>? = null

    @SerializedName("user_id")
    @Expose
    var userId: String? = null

    @SerializedName("loans_to_pay")
    @Expose
    var loansToPay: List<LoansToPay>? = null

    @SerializedName("twl_loans_to_pay")
    @Expose
    var twlLoansToPay: List<LoansToPay>? = null

    @SerializedName("show_loan_history_data")
    @Expose
    var showLoanHistoryData: Boolean? = null

    @SerializedName("loan_history_msg")
    @Expose
    var loanHistoryMsg: String? = null

    @SerializedName("gatewaydowntime")
    @Expose
    var gatewaydowntime: Gatewaydowntime? = null
}