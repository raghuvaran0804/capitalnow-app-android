package com.capitalnowapp.mobile.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable


class ExpenseCategory : Serializable{

    @SerializedName("category")
    @Expose
    var category: String? = null

    @SerializedName("Amount")
    @Expose
    var amount: Float? = null

    @SerializedName("Percentage")
    @Expose
    var percentage: Float? = null

}
