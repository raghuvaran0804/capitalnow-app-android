package com.capitalnowapp.mobile.models.Registrations

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable


class SaveRegistrationOneReq : Serializable {

    @SerializedName("api_key")
    @Expose
    var apiKey: String? = null

    @SerializedName("first_name")
    @Expose
    var firstName: String? = null

    @SerializedName("last_name")
    @Expose
    var lastName: String? = null

    @SerializedName("gender")
    @Expose
    var gender: String? = null

    @SerializedName("dob")
    @Expose
    var dob: String? = null

    @SerializedName("pancard_no")
    @Expose
    var pancardNo: String? = null

    @SerializedName("industry_type")
    @Expose
    var industryType: String? = null

    @SerializedName("department")
    @Expose
    var department: String? = null

    @SerializedName("designation")
    @Expose
    var designation: String? = null

    @SerializedName("company_name")
    @Expose
    var companyName: String? = null

    @SerializedName("company_id")
    @Expose
    var companyId: Int? = null

    @SerializedName("monthly_salary")
    @Expose
    var monthlySalary: String? = null

    @SerializedName("employement_type")
    @Expose
    var employementType: String? = null

    @SerializedName("present_pincode")
    @Expose
    var presentPincode: String? = null

    @SerializedName("permanent_pincode")
    @Expose
    var permanentPincode: String? = null

    @SerializedName("mode_of_pay")
    @Expose
    var modeOfPay: String? = null

    @SerializedName("page_no")
    @Expose
    var pageNo: String? = null

    @SerializedName("how_you_know_cn")
    @Expose
    var howYouKnowCn: String? = null
}