package com.capitalnowapp.mobile.models.userdetails

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class RegisterUserReq : Serializable{

    @SerializedName("first_name")
    @Expose
    var firstName: String? = ""
    @SerializedName("middle_name")
    @Expose
    var middleName: String? = ""
    @SerializedName("last_name")
    @Expose
    var lastName: String? = ""

    @SerializedName("user_id")
    @Expose
    var userId: String? = ""

    @SerializedName("api_key")
    @Expose
    val apiKey: String? = ""

    @SerializedName("user_mobile")
    @Expose
    val userMobile: String? = ""

    @SerializedName("alt_mobile")
    @Expose
    var altMobile: String? = ""

    @SerializedName("sec_email")
    @Expose
    var secEmail: String? = ""

    @SerializedName("native_city_id")
    @Expose
    var nativeCityId: String? = ""

    @SerializedName("dob")
    @Expose
    var dob: String? = ""

    @SerializedName("proinfo_type")
    @Expose
    var empType: String? = ""

    @SerializedName("company_name")
    @Expose
    var companyName: String? = ""

    @SerializedName("company_id")
    @Expose
    var companyId: Int? = -1

    @SerializedName("department")
    @Expose
    var department: String? = ""

    @SerializedName("designation")
    @Expose
    var designation: String? = ""

    @SerializedName("ctc")
    @Expose
    val ctc: String? = ""

    @SerializedName("pan_number")
    @Expose
    var panNumber: String? = ""

    @SerializedName("rf_type")
    @Expose
    var refType: String? = ""

    @SerializedName("promo_code")
    @Expose
    var promoCode: String? = ""

    @SerializedName("proinfo_monthly_sal")
    @Expose
    var monthlySal: String? = ""

    @SerializedName("clg_grad_yr")
    @Expose
    var yog: String? = ""

    @SerializedName("experience")
    @Expose
    var experience: String? = ""

    @SerializedName("mart_stat")
    @Expose
    var maritalStatus: String? = ""

    @SerializedName("residence")
    @Expose
    var residence: String? = ""

    @SerializedName("clg_name_list")
    @Expose
    var collegeName: String? = ""

    @SerializedName("credit_card_list")
    @Expose
    var cardType: String? = ""

    @SerializedName("getCityList")
    @Expose
    var cityList: String? = ""

    @SerializedName("frequently_used_apps")
    @Expose
    var frequentlyUsedApps: String? = ""

    @SerializedName("gender")
    @Expose
    var gender: String? = ""

    @SerializedName("mode_of_pay")
    @Expose
    var modeOfPay: String? = ""

    @SerializedName("city")
    @Expose
    var workCityId: String? = ""

    @SerializedName("user_survey_purpose_of_loan")
    @Expose
    var loanPurposeId : Int? = 0

    @SerializedName("user_survey_custom_purpose")
    @Expose
    var loanPurposeCustom: String? = ""

    @SerializedName("proinfo_industry")
    @Expose
    var industry: String? = ""



}