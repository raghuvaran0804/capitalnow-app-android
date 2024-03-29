package com.capitalnowapp.mobile.models.offerModel

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable


class SaveUserDetailsReq : Serializable {

    @SerializedName("api_key")
    @Expose
    var apiKey: String? = null

    @SerializedName("page_no")
    @Expose
    var pageNo: String? = null

    @SerializedName("p_first_name")
    @Expose
    var pFirstName: String? = null

    @SerializedName("p_last_name")
    @Expose
    var pLastName: String? = null

    @SerializedName("p_per_father_name")
    @Expose
    var pPerFatherName: String? = null

    @SerializedName("p_gender")
    @Expose
    var pGender: String? = null

    @SerializedName("p_email")
    @Expose
    var pEmail: String? = null

    @SerializedName("p_dob")
    @Expose
    var pDob: String? = null

    @SerializedName("p_employment_type")
    @Expose
    var pEmploymentType: Int? = null

    @SerializedName("p_monthly_salary")
    @Expose
    var pMonthlySalary: String? = null

    var additionalProperties: Map<String, String> = HashMap()

}