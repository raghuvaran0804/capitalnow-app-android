package com.capitalnowapp.mobile.models.userdetails

import com.capitalnowapp.mobile.beans.UserBasicData
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class UserDetails: Serializable {

    @SerializedName("full_name")
    @Expose
    var fullName: String? = ""
    @SerializedName("first_name")
    @Expose
    var firstName: String? = ""
    @SerializedName("middle_name")
    @Expose
    var middleName: String? = ""
    @SerializedName("last_name")
    @Expose
    var lastName: String? = ""

    @SerializedName("user_status")
    @Expose
    var userStatus: String? = null

    @SerializedName("mode_of_pay")
    @Expose
    var modeOfPay: String? = null

    @SerializedName("native_city_id")
    @Expose
    var nativeCityId: String? = null

    @SerializedName("location_saved")
    @Expose
    var locationSaved: String? = null

    @SerializedName("call_log_saved")
    @Expose
    var callLogSaved: String? = null

    @SerializedName("user_id")
    @Expose
    var userId: String? = null

    @SerializedName("qc_id")
    @Expose
    var qcId: String? = null

    @SerializedName("dob")
    @Expose
    var dob: String? = null

    @SerializedName("gender")
    @Expose
    var gender: String? = null

    @SerializedName("pincode")
    @Expose
    var pincode: String? = null

    @SerializedName("permanent_pincode")
    @Expose
    var permanentPincode: String? = null

    @SerializedName("active_profile")
    @Expose
    var activeProfile: String? = null

    @SerializedName("freeze_one")
    @Expose
    var freezeOne: String? = null

    @SerializedName("email")
    @Expose
    var email: String? = null

    @SerializedName("user_mobile")
    @Expose
    var userMobile: String? = null

    @SerializedName("alt_email")
    @Expose
    var altEmail: String? = null

    @SerializedName("alt_mobile")
    @Expose
    var altMobile: String? = null

    @SerializedName("work_city")
    @Expose
    var workCity: String? = null

    @SerializedName("native_city")
    @Expose
    var nativeCity: String? = null

    @SerializedName("how_you_know_qc")
    @Expose
    var howYouKnowCn: String? = null

    @SerializedName("how_you_know_cn")
    @Expose
    var howYouKnowCn1: String? = null

    @SerializedName("emp_type")
    @Expose
    var empType: String? = null

    @SerializedName("company_name")
    @Expose
    var companyName: String? = null

    @SerializedName("company_id")
    @Expose
    var companyId: Int? = -1

    @SerializedName("department")
    @Expose
    var department: String? = null

    @SerializedName("designation")
    @Expose
    var designation: String? = null

    @SerializedName("monthly_sal")
    @Expose
    var monthlySal: String? = null

    @SerializedName("exp_in_months")
    @Expose
    var expInMonths: String? = null

    @SerializedName("exp_in_years")
    @Expose
    var expInYears: String? = null

    @SerializedName("sal_date")
    @Expose
    var salDate: String? = null

    @SerializedName("sec_email_verified")
    @Expose
    var secEmailVerified: String? = null

    @SerializedName("sec_mobile_verified")
    @Expose
    var secMobileVerified: String? = null

    @SerializedName("pan")
    @Expose
    var pan: String? = null

    @SerializedName("grad_year")
    @Expose
    var gradYear: String? = null

    @SerializedName("college_id")
    @Expose
    var collegeId: String? = null

    @SerializedName("is_married")
    @Expose
    var isMarried: String? = null

    @SerializedName("residence_type_id")
    @Expose
    var residenceTypeId: String? = null

    @SerializedName("user_status_id")
    @Expose
    var userStatusId: String? = null

    @SerializedName("credit_card_id")
    @Expose
    var creditCardId: String? = null

    @SerializedName("frequently_used_apps")
    @Expose
    var frequentlyUsedApps: ArrayList<String>? = null

    @SerializedName("has_taken_first_loan")
    @Expose
    var hasTakenFirstLoan: Int? = null

    @SerializedName("freez_pan")
    @Expose
    var freezPan: Int? = null

    @SerializedName("has_membership")
    @Expose
    var hasMembership: Int? = null

    @SerializedName("freshchat_id")
    @Expose
    var freshchatId: Int? = null

    @SerializedName("user_basic_data")
    @Expose
    var userBasicData: UserBasicData? = null

    @SerializedName("work_city_id")
    @Expose
    var workCityId: String? = null

    @SerializedName("references")
    @Expose
    var references: Boolean? = null

    @SerializedName("proinfo_industry")
    @Expose
    var industry: String? = null

    @SerializedName("user_survey_purpose_of_loan")
    @Expose
    var loanPurposeId: Int? = 0

    @SerializedName("user_survey_custom_purpose")
    @Expose
    var loanCustomPurpose: String? = null

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

    @SerializedName("pre_state_city")
    @Expose
    var preStateCity: String? = null

    @SerializedName("off_address_line1")
    @Expose
    var offAddressLine1: String? = null

    @SerializedName("off_address_line2")
    @Expose
    var offAddressLine2: String? = null

    @SerializedName("off_pincode")
    @Expose
    var offPincode: String? = null

    @SerializedName("off_state_city")
    @Expose
    var offStateCity: String? = null

    @SerializedName("show_ipl_history")
    @Expose
    var showIplHistory: String? = null

    @SerializedName("sec_email_is_official")
    @Expose
    var isOfficial: Int? = -1

}