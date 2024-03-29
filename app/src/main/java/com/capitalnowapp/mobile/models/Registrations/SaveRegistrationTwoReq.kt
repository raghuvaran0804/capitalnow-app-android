package com.capitalnowapp.mobile.models.Registrations

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable


class SaveRegistrationTwoReq : Serializable{

    @SerializedName("api_key")
    @Expose
    var apiKey: String? = null

    @SerializedName("alt_mobile_no")
    @Expose
    var altMobileNo: String? = null

    @SerializedName("alt_email")
    @Expose
    var altEmail: String? = null

    @SerializedName("exp_years")
    @Expose
    var expYears: String? = null

    @SerializedName("sal_date")
    @Expose
    var salDate: String? = null

    @SerializedName("page_no")
    @Expose
    var pageNo: String? = null

}
