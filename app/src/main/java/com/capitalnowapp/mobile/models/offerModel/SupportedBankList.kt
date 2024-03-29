package com.capitalnowapp.mobile.models.offerModel

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable


class SupportedBankList : Serializable{
    @SerializedName("id")
    @Expose
    var id: Int? = null

    @SerializedName("razorpayBankName")
    @Expose
    var razorpayBankName: String? = null

    @SerializedName("razorpayBankCode")
    @Expose
    var razorpayBankCode: String? = null

    @SerializedName("emandateNetbankingSupported")
    @Expose
    var emandateNetbankingSupported: Boolean? = null

    @SerializedName("emandateDebitcardSupported")
    @Expose
    var emandateDebitcardSupported: Boolean? = null

    @SerializedName("perfiosId")
    @Expose
    var perfiosId: Int? = null

    @SerializedName("perfiosInstitutionId")
    @Expose
    var perfiosInstitutionId: Int? = null

    @SerializedName("perfiosBankName")
    @Expose
    var perfiosBankName: String? = null

    @SerializedName("statementSupported")
    @Expose
    var statementSupported: Boolean? = null

    @SerializedName("netbankingSupported")
    @Expose
    var netbankingSupported: Boolean? = null

}
