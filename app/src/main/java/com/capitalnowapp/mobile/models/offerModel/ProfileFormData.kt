package com.capitalnowapp.mobile.models.offerModel

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable


class ProfileFormData : Serializable{
    @SerializedName("p_email")
    @Expose
    var pEmail: String? = null

    @SerializedName("p_first_name")
    @Expose
    var pFirstName: String? = null

    @SerializedName("p_gender")
    @Expose
    var pGender: String? = null

    @SerializedName("p_last_name")
    @Expose
    var pLastName: String? = null

    @SerializedName("p_dob")
    @Expose
    var pDob: String? = null

    @SerializedName("p_per_father_name")
    @Expose
    var pPerFatherName: String? = null

    @SerializedName("p_monthly_salary")
    @Expose
    var pMonthlySalary: Int? = null

    @SerializedName("p_employment_type")
    @Expose
    var pEmploymentType: Int? = null

    @SerializedName("p_pincode")
    @Expose
    var pPincode: String? = null

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

    @SerializedName("P_per_residence_type")
    @Expose
    var pPerResidenceType: Int? = null

    @SerializedName("P_per_cur_add_staying_duration")
    @Expose
    var pPerCurAddStayingDuration: String? = null

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

    @SerializedName("P_per_marital_status")
    @Expose
    var PPerMaritalStatus: Int? = null

    @SerializedName("P_per_loan_reason")
    @Expose
    var PPerLoanReason: String? = null

    @SerializedName("p_pan_card_no")
    @Expose
    var pPanCardNo: String? = null

    @SerializedName("P_per_higest_qualification")
    @Expose
    var pPerHigestQualification: String? = null

    @SerializedName("p_aadhar_number")
    @Expose
    var paadharNumber: String? = null

    @SerializedName("pcl_min_loan_amount")
    @Expose
    var pclMinLoanAmount: Int? = null

    @SerializedName("pcl_max_loan_amount")
    @Expose
    var pclMaxLoanAmount: Int? = null

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

    @SerializedName("p_salary_bank_account")
    @Expose
    var pSalaryBankAccount: String? = null

    @SerializedName("p_bank_account_type")
    @Expose
    var pBankAccountType: String? = null

    @SerializedName("p_bank_account_holder")
    @Expose
    var pBankAccountHolder: String? = null

    @SerializedName("p_account_no")
    @Expose
    var pAccountNo: String? = null

    @SerializedName("p_ifsc_code")
    @Expose
    var pIfscCode: String? = null

    @SerializedName("p_bankLists")
    @Expose
    var pBankLists: List<ProfileFormDataBankListResponse>? = null

    @SerializedName("page_no")
    @Expose
    var pageNo: Int? = null

    @SerializedName("message")
    @Expose
    var message: String? = null

    @SerializedName("status")
    @Expose
    var status: String? = null

    @SerializedName("text")
    @Expose
    var text: String? = null

    @SerializedName("kfs_webview")
    @Expose
    var kfsWebView: String? = null

    @SerializedName("tnc_txt")
    @Expose
    var statusdata: StatusData? = null

    @SerializedName("kfs_data")
    @Expose
    var kfsData: KfsData? = null

    @SerializedName("banklist")
    @Expose
    var banklist: List<SupportedBankList>? = null

    @SerializedName("text_1")
    @Expose
    var text1: String? = null

    @SerializedName("text_2")
    @Expose
    var text2: String? = null

    @SerializedName("thank_you_1")
    @Expose
    var thankYou1: String? = null

    @SerializedName("thank_you_2")
    @Expose
    var thankYou2: String? = null

    @SerializedName("denied_1")
    @Expose
    var denied1: String? = null

    @SerializedName("denied_2")
    @Expose
    var denied2: String? = null

    @SerializedName("hold_1")
    @Expose
    var hold1: String? = null

    @SerializedName("hold_2")
    @Expose
    var hold2: String? = null

    @SerializedName("webview_url")
    @Expose
    var webViewUrl: String? = null

    @SerializedName("tanc_text")
    @Expose
    var tancText: List<TancText>? = null

    @SerializedName("city_names")
    @Expose
    var csCityNames: ArrayList<CSCityName>? = null

}
