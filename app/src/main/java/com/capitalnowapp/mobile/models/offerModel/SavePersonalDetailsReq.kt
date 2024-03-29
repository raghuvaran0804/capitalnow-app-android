package com.capitalnowapp.mobile.models.offerModel

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable


class SavePersonalDetailsReq : Serializable {

    @SerializedName("api_key")
    @Expose
    var apiKey: String? = null

    @SerializedName("page_no")
    @Expose
    var pageNo: String? = null

    @SerializedName("p_address_line_1")
    @Expose
    var pAddressLine1: String? = null

    @SerializedName("p_address_line_2")
    @Expose
    var pAddressLine2: String? = null

    @SerializedName("p_state")
    @Expose
    var pState: String? = null

    @SerializedName("p_city")
    @Expose
    var pCity: String? = null

    @SerializedName("p_pincode")
    @Expose
    var pPincode: String? = null

    @SerializedName("P_per_residence_type")
    @Expose
    var pPerResidenceType: String? = null

    @SerializedName("P_per_cur_add_staying_duration")
    @Expose
    var pPerCurAddStayingDuration: String? = null

    @SerializedName("P_per_higest_qualification")
    @Expose
    var pPerHigestQualification: String? = null

    @SerializedName("P_per_marital_status")
    @Expose
    var pPerMaritalStatus: String? = null

    @SerializedName("P_per_loan_reason")
    @Expose
    var pPerLoanReason: String? = null

    
}