package com.capitalnowapp.mobile.models.offerModel

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable


class SaveBankDetailsReq : Serializable {
    @SerializedName("api_key")
    @Expose
    var apiKey: String? = null

    @SerializedName("page_no")
    @Expose
    var pageNo: String? = null

    @SerializedName("p_salary_bank_account")
    @Expose
    var pSalaryBankAccount: String? = null

    @SerializedName("p_bank_account_type")
    @Expose
    var pBankAccountType: String? = null

    @SerializedName("p_bank_account_holder")
    @Expose
    var pBankAccountHolder: String? = null

    @SerializedName("p_account_no")
    @Expose
    var pAccountNo: String? = null

    @SerializedName("p_ifsc_code")
    @Expose
    var pIfscCode: String? = null
}