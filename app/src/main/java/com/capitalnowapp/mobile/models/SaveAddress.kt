package com.capitalnowapp.mobile.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class SaveAddress {
    @SerializedName("addressline1")
    @Expose
    var address1: String? = ""
    @SerializedName("city")
    @Expose
    var city: String? = ""
    @SerializedName("state")
    @Expose
    var state: String? = ""
    @SerializedName("pincode")
    @Expose
    var pinCode: String? = ""




}