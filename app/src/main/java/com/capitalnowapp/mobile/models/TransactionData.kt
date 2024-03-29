package com.capitalnowapp.mobile.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable


class TransactionData : Serializable{
    @SerializedName("image")
    @Expose
    var image: String? = null

    @SerializedName("title")
    @Expose
    var title: String? = null

    @SerializedName("description")
    @Expose
    var description: String? = null

    @SerializedName("transaction_id")
    @Expose
    var transactionId: String? = null

    @SerializedName("updated_at")
    @Expose
    var updatedAt: String? = null

    @SerializedName("amount")
    @Expose
    var amount: Int? = null

    @SerializedName("type")
    @Expose
    var type: String? = null
}
