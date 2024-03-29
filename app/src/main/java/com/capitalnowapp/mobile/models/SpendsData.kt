package com.capitalnowapp.mobile.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable


class SpendsData : Serializable {

    @SerializedName("salaryRecords")
    @Expose
    var salaryRecords: List<SpendsSalaryRecords>? = null

    @SerializedName("banksAccounts")
    @Expose
    var banksAccounts: List<SpendsBanksAccount>? = null

    @SerializedName("spends")
    @Expose
    var spends: List<Spends>? = null

    @SerializedName("lastUpdate")
    @Expose
    var lastUpdated: String? = null

}
