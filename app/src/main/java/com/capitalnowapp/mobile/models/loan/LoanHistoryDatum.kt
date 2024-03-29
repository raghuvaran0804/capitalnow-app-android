package com.capitalnowapp.mobile.models.loan

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class LoanHistoryDatum {
    @SerializedName("re_points")
    @Expose
    var rePoints: String? = null

    @SerializedName("cloan_sequence_id")
    @Expose
    var cloanSequenceId: String? = null

    @SerializedName("cloan_total")
    @Expose
    var cloanTotal: String? = null

    @SerializedName("cloan_repay_date")
    @Expose
    var cloanRepayDate: String? = null

    @SerializedName("cloan_max_repay_date")
    @Expose
    var cloanMaxRepayDate: String? = null

    @SerializedName("cloan_exhausted_days")
    @Expose
    var cloanExhaustedDays: String? = null

    @SerializedName("cloan_issue_date")
    @Expose
    var cloanIssueDate: String? = null

    @SerializedName("cloan_actual_repay_date")
    @Expose
    var cloanActualRepayDate: String? = null

    @SerializedName("cloan_issue_type")
    @Expose
    var cloanIssueType: String? = null

    @SerializedName("cloan_issue_bank_amount")
    @Expose
    var cloanIssueBankAmount: String? = null

    @SerializedName("cloan_issue_apay_amount")
    @Expose
    var cloanIssueApayAmount: String? = null

    @SerializedName("cloan_issue_apay_number")
    @Expose
    var cloanIssueApayNumber: String? = null

    @SerializedName("cloan_processing_fee")
    @Expose
    var cloanProcessingFee: String? = null

    @SerializedName("cloan_service_charge")
    @Expose
    var cloanServiceCharge: String? = null

    @SerializedName("cloan_amount")
    @Expose
    var cloanAmount: String? = null

    @SerializedName("cloan_cashback")
    @Expose
    var cloanCashback: Any? = null

    @SerializedName("twloan_cashback")
    @Expose
    var twlLoanCashback: Any? = null

    @SerializedName("loandetails")
    @Expose
    var loandetails: Loandetails? = null
}