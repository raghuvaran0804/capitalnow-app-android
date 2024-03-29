package com.capitalnowapp.mobile.kotlin.activities.offer

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable


class PinCodesData : Serializable{
    @SerializedName("pscl_pincode")
    @Expose
    var psclPincode: String? = null
}
