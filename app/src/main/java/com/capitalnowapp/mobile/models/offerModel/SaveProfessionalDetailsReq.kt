package com.capitalnowapp.mobile.models.offerModel

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable


class SaveProfessionalDetailsReq : Serializable {
    @SerializedName("api_key")
    @Expose
    var apiKey: String? = null

    @SerializedName("page_no")
    @Expose
    var pageNo: String? = null

    @SerializedName("p_salary_mode")
    @Expose
    var pSalaryMode: Int? = null

    @SerializedName("p_pro_address_line_1")
    @Expose
    var pProAddressLine1: String? = null

    @SerializedName("p_pro_address_line_2")
    @Expose
    var pProAddressLine2: String? = null

    @SerializedName("p_pro_state")
    @Expose
    var pProState: String? = null

    @SerializedName("p_pro_city")
    @Expose
    var pProCity: String? = null

    @SerializedName("P_pro_Office_pincode")
    @Expose
    var pProOfficePincode: String? = null

    @SerializedName("P_pro_work_experience")
    @Expose
    var pProWorkExperience: String? = null

    @SerializedName("p_company_name")
    @Expose
    var pCompanyName: String? = null

    @SerializedName("p_official_email")
    @Expose
    var pOfficialEmail: String? = null

}