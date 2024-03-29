package com.capitalnowapp.mobile.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable


class BankList : Serializable{

    @SerializedName("cem_id")
    @Expose
    var cemId: String? = null

    @SerializedName("cem_bank_name")
    @Expose
    var cemBankName: String? = null

    @SerializedName("cem_bank_ac_id")
    @Expose
    var cemBankAcId: String? = null

    @SerializedName("cem_bank_ifsc")
    @Expose
    var cemBankIfsc: String? = null

    var isChecked: Boolean = false

}
