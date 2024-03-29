package com.capitalnowapp.mobile.models

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName
import java.io.Serializable


class RequiredPendingDocument : Serializable{
    @SerializedName("label")
    @Expose
    var label: String? = null

    @SerializedName("sub_label")
    @Expose
    var subLabel: Any? = null

    @SerializedName("key")
    @Expose
    var key: String? = null

    @SerializedName("count")
    @Expose
    var count: Int? = null

    @SerializedName("doc_status")
    @Expose
    var docStatus: Int? = null

    @SerializedName("apply_loan_redirection")
    @Expose
    var applyLoanRedirection: Boolean? = null

    @SerializedName("status_redirect")
    @Expose
    var statusRedirect: Int? = null

    @SerializedName("is_pass_req")
    @Expose
    var isPassReq: Boolean? = null

    @SerializedName("pass_key")
    @Expose
    var passKey: Any? = null
}
