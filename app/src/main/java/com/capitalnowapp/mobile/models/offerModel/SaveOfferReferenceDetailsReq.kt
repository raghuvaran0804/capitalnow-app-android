package com.capitalnowapp.mobile.models.offerModel

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable


class SaveOfferReferenceDetailsReq : Serializable {
    @SerializedName("api_key")
    @Expose
    var apiKey: String? = null

    @SerializedName("page_no")
    @Expose
    var pageNo: String? = null

    @SerializedName("cr_p_name")
    @Expose
    var crPName: String? = null

    @SerializedName("cr_p_mobile_number")
    @Expose
    var crPMobileNumber: String? = null

    @SerializedName("cr_p_relation")
    @Expose
    var crPRelation: String? = null

    @SerializedName("cr_p_address1")
    @Expose
    var crPAddress1: String? = null

    @SerializedName("cr_p_pincode")
    @Expose
    var crPPincode: String? = null

    @SerializedName("cr_p_state")
    @Expose
    var crPState: String? = null

    @SerializedName("cr_p_city")
    @Expose
    var crPCity: String? = null

    @SerializedName("cr_s_name")
    @Expose
    var crSName: String? = null

    @SerializedName("cr_s_mobile_number")
    @Expose
    var crSMobileNumber: String? = null

    @SerializedName("cr_s_relation")
    @Expose
    var crSRelation: String? = null

    @SerializedName("cr_s_address1")
    @Expose
    var crSAddress1: String? = null

    @SerializedName("cr_s_pincode")
    @Expose
    var crSPincode: String? = null

    @SerializedName("cr_s_state")
    @Expose
    var crSState: String? = null

    @SerializedName("cr_s_city")
    @Expose
    var crSCity: String? = null
}