package com.capitalnowapp.mobile.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable


class ApplyLoanServiceDataResponse : Serializable{

    @SerializedName("razor_pay_api_key")
    @Expose
    var razorPayApiKey: String? = null

    @SerializedName("did")
    @Expose
    var did: String? = null

    @SerializedName("bank_change_type")
    @Expose
    var bankChangeType: String? = null

    @SerializedName("user_lending_partner_id")
    @Expose
    var userLendingPartnerId: String? = null

    @SerializedName("show_loan_partner_menu")
    @Expose
    var showLoanPartnerMenu: Boolean? = null

    @SerializedName("loan_partner_link")
    @Expose
    var loanPartnerLink: String? = null

    @SerializedName("our_partner_link")
    @Expose
    var ourPartnerLink: String? = null

    @SerializedName("app_rated")
    @Expose
    var appRated: Int? = null

    @SerializedName("show_new_rewards")
    @Expose
    var showNewRewards: Boolean? = null

    @SerializedName("recall_data")
    @Expose
    var recallData: Int? = null

    @SerializedName("loan_id")
    @Expose
    var loanId: String? = null

    @SerializedName("status")
    @Expose
    var status: String? = null

    @SerializedName("status_redirect")
    @Expose
    var statusRedirect: Int? = null

    @SerializedName("apply_loan_redirection")
    @Expose
    var applyLoanRedirection: Boolean? = null

}
