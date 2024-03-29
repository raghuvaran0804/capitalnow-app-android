package com.capitalnowapp.mobile.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable


class Spends : Serializable {

    @SerializedName("month")
    @Expose
    var month: Int? = null

    @SerializedName("monthName")
    @Expose
    var monthName: String? = null

    @SerializedName("year")
    @Expose
    var year: Int? = null

    @SerializedName("totalCredits")
    @Expose
    var totalCredits: Float? = null

    @SerializedName("totalDebits")
    @Expose
    var totalDebits: Float? = null

    @SerializedName("dailyAvgSpend")
    @Expose
    var dailyAvgSpend: Int? = null

    @SerializedName("incomeCategory")
    @Expose
    var incomeCategory: List<Any>? = null

    @SerializedName("expenseCategory")
    @Expose
    var expenseCategory: List<ExpenseCategory>? = null

    @SerializedName("topTransactions")
    @Expose
    var topTransactions: List<TopTransaction>? = null

    var isSelected: Boolean? = false

}
