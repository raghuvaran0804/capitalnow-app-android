package com.capitalnowapp.mobile.models.Registrations

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable


class SaveRegistrationThreeReq : Serializable{

    @SerializedName("api_key")
    @Expose
    var apiKey: String? = null

    @SerializedName("pre_address_line1")
    @Expose
    var preAddressLine1: String? = null

    @SerializedName("pre_address_line2")
    @Expose
    var preAddressLine2: String? = null

    @SerializedName("pre_landmark")
    @Expose
    var preLandmark: String? = null

    @SerializedName("pre_pincode")
    @Expose
    var prePincode: String? = null

    @SerializedName("off_address_line1")
    @Expose
    var offAddressLine1: String? = null

    @SerializedName("off_address_line2")
    @Expose
    var offAddressLine2: String? = null

    @SerializedName("off_pincode")
    @Expose
    var offPincode: String? = null

}
