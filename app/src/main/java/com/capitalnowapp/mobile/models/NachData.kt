package com.capitalnowapp.mobile.models

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName
import java.io.Serializable


class NachData :Serializable {
    @SerializedName("bank_name")
    @Expose
    var bankName: String? = null

    @SerializedName("bank_ac_number")
    @Expose
    var bankAcNumber: String? = null

    @SerializedName("bank_message")
    @Expose
    var bankMessage: String? = null

}
