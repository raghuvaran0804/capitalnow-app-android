package com.capitalnowapp.mobile.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable


class TypeAndMobileData : Serializable{

    @SerializedName("vendor_type")
    @Expose
    var vendorType: String? = null

    @SerializedName("mob_numbers")
    @Expose
    var mobNumbers: List<String>? = null
}