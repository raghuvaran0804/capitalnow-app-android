package com.capitalnowapp.mobile.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable


class TwlLoanCancelAction :Serializable{
    @SerializedName("title")
    @Expose
    var title: String? = null

    @SerializedName("status")
    @Expose
    var status: Int? = null

    @SerializedName("reasons")
    @Expose
    var twlcancelReasons: List<CancelReason>? = null

}
