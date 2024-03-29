package com.capitalnowapp.mobile.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable


class SalaryData : Serializable{
    @SerializedName("flow")
    @Expose
    var flow: String? = null
}
