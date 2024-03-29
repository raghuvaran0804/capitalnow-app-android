package com.capitalnowapp.mobile.models

import com.capitalnowapp.mobile.models.loan.LoanStatus
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable


class TwlLoanStatusData : Serializable{
    @SerializedName("loan_status")
    @Expose
    var loanStatus: List<LoanStatus>? = null

    @SerializedName("lid")
    @Expose
    var lid: String? = null

    @SerializedName("twl_id")
    @Expose
    var twlId: String? = null

    @SerializedName("show_rate_us")
    @Expose
    var showRateUs: Boolean? = null

    @SerializedName("title")
    @Expose
    var title: String? = null

    @SerializedName("actions")
    @Expose
    var twlLoanCancelActions: List<TwlLoanCancelAction>? = null

    @SerializedName("delivery_info")
    @Expose
    var deliveryInfo: DeliveryInfo? = null

}
