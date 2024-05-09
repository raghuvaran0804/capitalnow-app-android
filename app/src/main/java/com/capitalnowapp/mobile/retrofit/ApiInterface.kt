package com.capitalnowapp.mobile.retrofit

import com.capitalnowapp.mobile.beans.OrderData
import com.capitalnowapp.mobile.kotlin.activities.offer.GetPinCodesReq
import com.capitalnowapp.mobile.kotlin.activities.offer.GetPinCodesResponse
import com.capitalnowapp.mobile.models.AadharOtpReq
import com.capitalnowapp.mobile.models.AadharOtpResponse
import com.capitalnowapp.mobile.models.AnalyseCapabilityReq
import com.capitalnowapp.mobile.models.ApplyLoanServiceDataReq
import com.capitalnowapp.mobile.models.ApplyLoanServiceDataResponse
import com.capitalnowapp.mobile.models.ApplyTWLoanReq
import com.capitalnowapp.mobile.models.ApplyTWLoanResponse
import com.capitalnowapp.mobile.models.AreaListResponse
import com.capitalnowapp.mobile.models.AutofillResponse
import com.capitalnowapp.mobile.models.BankListRes
import com.capitalnowapp.mobile.models.BannerImageRequest
import com.capitalnowapp.mobile.models.BannerImageResponse
import com.capitalnowapp.mobile.models.BbpsBillPayResponse
import com.capitalnowapp.mobile.models.CancelTwlLoanReq
import com.capitalnowapp.mobile.models.CancelTwlLoanResponse
import com.capitalnowapp.mobile.models.CheckLoanStatusForDeletionReq
import com.capitalnowapp.mobile.models.CheckLoanStatusForDeletionResponse
import com.capitalnowapp.mobile.models.CheckPromoCodeReq
import com.capitalnowapp.mobile.models.CheckPromoCodeResponse
import com.capitalnowapp.mobile.models.CityListResponse
import com.capitalnowapp.mobile.models.ColorListResponse
import com.capitalnowapp.mobile.models.ContactUsRequest
import com.capitalnowapp.mobile.models.ContactUsResponse
import com.capitalnowapp.mobile.models.CreateKycReq
import com.capitalnowapp.mobile.models.CreateMandateReq
import com.capitalnowapp.mobile.models.CreateMandateResponse
import com.capitalnowapp.mobile.models.CreateVKYCResponse
import com.capitalnowapp.mobile.models.DealerListResponse
import com.capitalnowapp.mobile.models.DeleteConsentReq
import com.capitalnowapp.mobile.models.DeleteConsentResponse
import com.capitalnowapp.mobile.models.EasebuzzResponseRequest
import com.capitalnowapp.mobile.models.EasebuzzResponseResponse
import com.capitalnowapp.mobile.models.FileUploadAjaxRequest
import com.capitalnowapp.mobile.models.FileUploadResponse
import com.capitalnowapp.mobile.models.GenericRequest
import com.capitalnowapp.mobile.models.GenericResponse
import com.capitalnowapp.mobile.models.GetAdditionalDocReq
import com.capitalnowapp.mobile.models.GetAdditionalDocResponse
import com.capitalnowapp.mobile.models.GetAnalysisListResponse
import com.capitalnowapp.mobile.models.GetAnalysisTypeReq
import com.capitalnowapp.mobile.models.GetAnalysisTypeResponse
import com.capitalnowapp.mobile.models.GetAreaListReq
import com.capitalnowapp.mobile.models.GetBankLinkReq
import com.capitalnowapp.mobile.models.GetBbpsLinkGeneratorReq
import com.capitalnowapp.mobile.models.GetBbpsLinkGeneratorResponse
import com.capitalnowapp.mobile.models.GetBrandListReq
import com.capitalnowapp.mobile.models.GetBrandListResponse
import com.capitalnowapp.mobile.models.GetCaptchaReq
import com.capitalnowapp.mobile.models.GetCaptchaResponse
import com.capitalnowapp.mobile.models.GetCibilReq
import com.capitalnowapp.mobile.models.GetCibilResponse
import com.capitalnowapp.mobile.models.GetCityListReq
import com.capitalnowapp.mobile.models.GetColorListReq
import com.capitalnowapp.mobile.models.GetCouponsReq
import com.capitalnowapp.mobile.models.GetCouponsResponse
import com.capitalnowapp.mobile.models.GetDealerListReq
import com.capitalnowapp.mobile.models.GetEligibleOfferDetailReq
import com.capitalnowapp.mobile.models.GetEligibleOfferDetailsResponse
import com.capitalnowapp.mobile.models.GetEligibleOffersReq
import com.capitalnowapp.mobile.models.GetEligibleOffersResponse
import com.capitalnowapp.mobile.models.GetHoldStatusReq
import com.capitalnowapp.mobile.models.GetHoldStatusResponse
import com.capitalnowapp.mobile.models.GetLoanRangeReq
import com.capitalnowapp.mobile.models.GetLoanRangeResponse
import com.capitalnowapp.mobile.models.GetMandateList
import com.capitalnowapp.mobile.models.GetMandateListResponse
import com.capitalnowapp.mobile.models.GetPendingDocReq
import com.capitalnowapp.mobile.models.GetPendingDocResponse
import com.capitalnowapp.mobile.models.GetProcessPageContentResponse
import com.capitalnowapp.mobile.models.GetSpendsDataReq
import com.capitalnowapp.mobile.models.GetSpendsResponse
import com.capitalnowapp.mobile.models.GetTransactionReq
import com.capitalnowapp.mobile.models.GetTransactionResponse
import com.capitalnowapp.mobile.models.GetTwlTenureDataReq
import com.capitalnowapp.mobile.models.GetUpdateBankDataReq
import com.capitalnowapp.mobile.models.GetVarientListReq
import com.capitalnowapp.mobile.models.GetVehicleDetailsReq
import com.capitalnowapp.mobile.models.GetVehicleDetailsResponse
import com.capitalnowapp.mobile.models.GetVehiclesListReq
import com.capitalnowapp.mobile.models.GetVerifyAlternateEmailResponse
import com.capitalnowapp.mobile.models.IdTextData
import com.capitalnowapp.mobile.models.MemberUpgradeConsentReq
import com.capitalnowapp.mobile.models.MemberUpgradeConsentResponse
import com.capitalnowapp.mobile.models.OfferScrollResponse
import com.capitalnowapp.mobile.models.OpenDigiLockerReq
import com.capitalnowapp.mobile.models.OpenDigiLockerResponse
import com.capitalnowapp.mobile.models.PinCodeResponse
import com.capitalnowapp.mobile.models.RazorPayCreateMandateReq
import com.capitalnowapp.mobile.models.RazorPayCreateMandateResponse
import com.capitalnowapp.mobile.models.RazorPaySubmitMandateReq
import com.capitalnowapp.mobile.models.RazorPaySubmitMandateResponse
import com.capitalnowapp.mobile.models.RegisterLoanReq
import com.capitalnowapp.mobile.models.RegisterLoanResponse
import com.capitalnowapp.mobile.models.Registrations.DesignationListResponse
import com.capitalnowapp.mobile.models.Registrations.GetDesignationListReq
import com.capitalnowapp.mobile.models.Registrations.GetVerifyAlternateMobileResponse
import com.capitalnowapp.mobile.models.Registrations.PinCodeReq
import com.capitalnowapp.mobile.models.Registrations.SaveRegistrationOneReq
import com.capitalnowapp.mobile.models.Registrations.SaveRegistrationOneResponse
import com.capitalnowapp.mobile.models.Registrations.SaveRegistrationThreeReq
import com.capitalnowapp.mobile.models.Registrations.SaveRegistrationThreeResponse
import com.capitalnowapp.mobile.models.Registrations.SaveRegistrationTwoReq
import com.capitalnowapp.mobile.models.Registrations.SaveRegistrationTwoResponse
import com.capitalnowapp.mobile.models.Registrations.VerifyAltMobileByOTPReq
import com.capitalnowapp.mobile.models.Registrations.VerifyAltMobileByOTPResponse
import com.capitalnowapp.mobile.models.Registrations.VerifyAlternateMobileReq
import com.capitalnowapp.mobile.models.SaveAppReq
import com.capitalnowapp.mobile.models.SaveCCDataResponse
import com.capitalnowapp.mobile.models.SaveCalendarEventsReq
import com.capitalnowapp.mobile.models.SaveDataResponse
import com.capitalnowapp.mobile.models.SaveLoanConsentResponse
import com.capitalnowapp.mobile.models.SaveMessageReq
import com.capitalnowapp.mobile.models.SavePrimaryMandateReq
import com.capitalnowapp.mobile.models.SavePrimaryMandateResponse
import com.capitalnowapp.mobile.models.SaveReferencesReq
import com.capitalnowapp.mobile.models.SaveReferencesResponse
import com.capitalnowapp.mobile.models.SaveSalaryReq
import com.capitalnowapp.mobile.models.SaveSalaryResponse
import com.capitalnowapp.mobile.models.SendLocationReq
import com.capitalnowapp.mobile.models.SendOTPDeletionReq
import com.capitalnowapp.mobile.models.SendOTPDeletionResponse
import com.capitalnowapp.mobile.models.SkipAadharData
import com.capitalnowapp.mobile.models.SkipPanData
import com.capitalnowapp.mobile.models.SubmitBankChangeReq
import com.capitalnowapp.mobile.models.SubmitBankChangeResponse
import com.capitalnowapp.mobile.models.SubmitContactUsReq
import com.capitalnowapp.mobile.models.SubmitContactUsResponse
import com.capitalnowapp.mobile.models.SubmitInitialDocsReq
import com.capitalnowapp.mobile.models.SubmitMandateReq
import com.capitalnowapp.mobile.models.SubmitMandateResponse
import com.capitalnowapp.mobile.models.SubmitVKYCReq
import com.capitalnowapp.mobile.models.SubmitVKYCResponse
import com.capitalnowapp.mobile.models.TwlActiveLoansReq
import com.capitalnowapp.mobile.models.TwlActiveLoansResponse
import com.capitalnowapp.mobile.models.TwlBannerImagesReq
import com.capitalnowapp.mobile.models.TwlBannerImagesResponse
import com.capitalnowapp.mobile.models.TwlLoanDetailsReq
import com.capitalnowapp.mobile.models.TwlLoanDetailsResponse
import com.capitalnowapp.mobile.models.TwlLoanStatusResponse
import com.capitalnowapp.mobile.models.TwlProcessingOrderReq
import com.capitalnowapp.mobile.models.TwlTenureDataResponse
import com.capitalnowapp.mobile.models.UpdateBankDataResponse
import com.capitalnowapp.mobile.models.UpdateNewLocationReq
import com.capitalnowapp.mobile.models.UpdateNewLocationResponse
import com.capitalnowapp.mobile.models.UploadAdditionalDocReq
import com.capitalnowapp.mobile.models.UploadAdditionalDocResponse
import com.capitalnowapp.mobile.models.UploadPendingDocReq
import com.capitalnowapp.mobile.models.UploadPendingDocResponse
import com.capitalnowapp.mobile.models.VarientListResponse
import com.capitalnowapp.mobile.models.VehiclesListResponse
import com.capitalnowapp.mobile.models.VerifyAadharOtpReq
import com.capitalnowapp.mobile.models.VerifyAlternateEmailReq
import com.capitalnowapp.mobile.models.VerifyEmailByOTPReq
import com.capitalnowapp.mobile.models.VerifyEmailByOTPReqResponse
import com.capitalnowapp.mobile.models.VerifyOTPDeletionReq
import com.capitalnowapp.mobile.models.VerifyOTPDeletionResponse
import com.capitalnowapp.mobile.models.WebLinkRes
import com.capitalnowapp.mobile.models.coupons.CouponsResponse
import com.capitalnowapp.mobile.models.coupons.RedeemCouponReq
import com.capitalnowapp.mobile.models.coupons.RedeemCouponResponse
import com.capitalnowapp.mobile.models.coupons.SendEmailReq
import com.capitalnowapp.mobile.models.loan.LoanStatusResponse
import com.capitalnowapp.mobile.models.login.GetOTPRequest
import com.capitalnowapp.mobile.models.login.RegisterDeviceRequest
import com.capitalnowapp.mobile.models.login.RegisterDeviceResponse
import com.capitalnowapp.mobile.models.login.VerifyOTPRequest
import com.capitalnowapp.mobile.models.managerdetails.ManagerResponse
import com.capitalnowapp.mobile.models.offerModel.CSActiveLoanReq
import com.capitalnowapp.mobile.models.offerModel.CSActiveLoanResponse
import com.capitalnowapp.mobile.models.offerModel.CSGenericResponse
import com.capitalnowapp.mobile.models.offerModel.PrSaveSignReq
import com.capitalnowapp.mobile.models.profile.ProfileResponse
import com.capitalnowapp.mobile.models.rewardsNew.GetCouponCategoriesReq
import com.capitalnowapp.mobile.models.rewardsNew.GetCouponCategoriesResponse
import com.capitalnowapp.mobile.models.rewardsNew.GetCouponInfoReq
import com.capitalnowapp.mobile.models.rewardsNew.GetCouponInfoResponse
import com.capitalnowapp.mobile.models.rewardsNew.GetEmailCouponDetailsReq
import com.capitalnowapp.mobile.models.rewardsNew.GetEmailCouponDetailsResponse
import com.capitalnowapp.mobile.models.rewardsNew.GetRedeemCouponDetailsReq
import com.capitalnowapp.mobile.models.rewardsNew.GetRedeemCouponDetailsResponse
import com.capitalnowapp.mobile.models.rewardsNew.GetRedeemCouponReq
import com.capitalnowapp.mobile.models.rewardsNew.GetRedeemCouponResponse
import com.capitalnowapp.mobile.models.rewardsNew.GetRedeemedCouponsReq
import com.capitalnowapp.mobile.models.rewardsNew.GetRedeemedCouponsResponse
import com.capitalnowapp.mobile.models.userdetails.UserDetailsResponse
import com.google.gson.JsonObject
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part


interface ApiInterface {

    @POST("v2/auth/send-otp")
    fun requestOTP(@Body getOTPRequest: GetOTPRequest?): Call<GenericResponse?>?

    @POST("auth/resend-otp")
    fun resendOTP(@Body getOTPRequest: GetOTPRequest?): Call<GenericResponse?>?

    @POST("auth/send-voice-otp")
    fun requestCallOTP(@Body getOTPRequest: GetOTPRequest?): Call<GenericResponse?>?

    @POST("v2/auth/verify-otp")
    fun verifyOTP(@Body verifyOTPRequest: VerifyOTPRequest?): Call<GenericResponse?>?

    @POST("app/mob-auto-fill")
    fun autoFill(@Body verifyOTPRequest: VerifyOTPRequest?): Call<AutofillResponse?>?

    @Multipart
    @POST("uploadFileToServer")
    fun uploadFile(
        @Part file: MultipartBody.Part?,
        @Part("api_key") apiKey: RequestBody?,
        @Part("user_id") userId: RequestBody?
    ): Call<FileUploadResponse?>?

    @POST("uploadedFileUrlAjax")
    fun updateFileAjay(@Body fileUploadAjaxRequest: FileUploadAjaxRequest?): Call<ResponseBody?>?

    @Multipart
    @POST("uploadProfileImage")
    fun uploadProfileImage(
        @Part file: MultipartBody.Part?,
        @Part("api_key") apiKey: RequestBody?,
        @Part("user_id") userId: RequestBody?,
        @Part("device_unique_id") deviceId: RequestBody?
    ): Call<FileUploadResponse?>?

    /*@Multipart
    @POST("save_pancard/")
    fun uploadPanImage(
        @Part file: MultipartBody.Part?,
        @Part("api_key") apiKey: RequestBody?,
        @Part("user_id") userId: RequestBody?,
        @Part("pan_number") panNumber: RequestBody?
    ): Call<FileUploadResponse>?*/

    @Multipart
    @POST("user/save-pancard/")
    fun uploadPanImage(
        @Part file: MultipartBody.Part?,
        @Part("api_key") apiKey: RequestBody?,
        @Part("user_id") userId: RequestBody?,
        @Part("pan_number") panNumber: RequestBody?
    ): Call<FileUploadResponse>?

    @Multipart
    @POST("user/save-aadhar-data")
    fun saveAadharData(
        @Part fileFront: MultipartBody.Part?,
        @Part fileBack: MultipartBody.Part?,
        @Header("authorizationtoken") apiKey: String?,
        //@Part("api_key") apiKey: RequestBody?,
        @Part("user_id") userId: RequestBody?,
        @Part("aadharno") aadharNumber: RequestBody?
    ): Call<FileUploadResponse>?


    @Multipart
    @POST("insert_docs_new")
    fun uploadLatestDocs(
        @Part file: MultipartBody.Part?,
        @Part bankaccountstatement: Array<MultipartBody.Part?>?,
        @Part("wudoc_salslip_pwd") salSlipPwd: RequestBody?,
        @Part("wudoc_bankstat_pwd") bankStmtPwd: RequestBody?,
        @Part("api_key") apiKey: RequestBody?,
        @Part("device_unique_id") deviceId: RequestBody?,
        @Part("user_id") userId: RequestBody?
    ): Call<JsonObject?>?

    @Multipart
    @POST("saveLatestPayslip")
    fun saveLatestPayslip(
        @Part file: MultipartBody.Part?,
        @Part("wudoc_salslip_pwd") salSlipPwd: RequestBody?,
        @Part("api_key") apiKey: RequestBody?,
        @Part("device_unique_id") deviceId: RequestBody?,
        @Part("user_id") userId: RequestBody?
    ): Call<JsonObject?>?

    @POST("saveInitialDocuments")
    fun submitInitialDocs(@Body submitInitialDocsReq: SubmitInitialDocsReq?): Call<GenericResponse?>?

    /*@POST("contactUs")
    fun submitContactUs(@Body contactUsRequest: ContactUsRequest?): Call<ContactUsResponse?>?*/

    /*@POST("contact_us")
    fun getContactUsData(@Body contactUsRequest: ContactUsRequest?): Call<ContactUsResponse?>?*/

    @POST("contact_us")
    fun getContactUsData(@Body contactUsRequest: ContactUsRequest?): Call<ContactUsResponse?>?

    @POST("getUserDetails")
    fun getUserDetails(@Body contactUsRequest: ContactUsRequest?): Call<ProfileResponse?>?

    @POST("manager_details")
    fun getManagerData(@Body contactUsRequest: ContactUsRequest?): Call<ManagerResponse?>?

    /*@POST("getProfile")
    fun getProfile(@Body genericRequest: GenericRequest?): Call<UserDetailsResponse?>?*/

    @POST("user/get-profile")
    fun getProfile(@Body genericRequest: GenericRequest?): Call<UserDetailsResponse?>?

    @POST("notificationscount")
    fun getNotificationCount(@Body genericRequest: GenericRequest?): Call<GenericResponse?>?

    @POST("readnotification")
    fun readNotifications(@Body genericRequest: GenericRequest?): Call<GenericResponse?>?

    /*@POST("check_number_exists")
    fun requestOTP(@Body getOTPRequest: GetOTPRequest?): Call<GenericResponse?>?*/

    /*@POST("resendOTP")
    fun resendOTP(@Body getOTPRequest: GetOTPRequest?): Call<GenericResponse?>?*/

    /*@POST("sendVoiceOTP")
    fun requestCallOTP(@Body getOTPRequest: GetOTPRequest?): Call<GenericResponse?>?*/

    /* @POST("verifyLoginOTP")
     fun verifyOTP(@Body verifyOTPRequest: VerifyOTPRequest?): Call<GenericResponse?>?*/

    @POST("registerDevice")
    fun registerDevice(@Body registerDeviceRequest: RegisterDeviceRequest?): Call<RegisterDeviceResponse?>?

    @POST("logicalbanya_data")
    fun getLogicalBanyaData(@Body genericRequest: GenericRequest?): Call<CouponsResponse?>?

    @POST("logicalbanya_redeem")
    fun redeemCoupon(@Body redeemCouponReq: RedeemCouponReq?): Call<RedeemCouponResponse?>?

    @POST("getLoanStatus")
    fun getLoanStatus(@Body genericRequest: GenericRequest?): Call<LoanStatusResponse?>?

    @POST("twlLoanStatus")
    fun twlLoanStatus(@Body genericRequest: GenericRequest?): Call<TwlLoanStatusResponse?>?

    @POST("sendcoupon")
    fun sendEmail(@Body sendEmailReq: SendEmailReq?): Call<GenericResponse?>?

    @POST("addressSave")
    fun addressSave(@Body sendLocationReq: SendLocationReq?): Call<GenericResponse?>?

    /*@POST("set_profile")
    fun getCibilScore(@Body getCibilReq: GetCibilReq?): Call<GetCibilResponse?>?*/

    @POST("user/get-cibil-v2")
    fun getCibilScore(@Body getCibilReq: GetCibilReq?): Call<GetCibilResponse?>?

    /*@POST("skipPanData")
    fun skipPanData(@Body skipPanData: SkipPanData?): Call<GetCibilResponse?>?*/

    @POST("user/skip-pan-data")
    fun skipPanData(@Body skipPanData: SkipPanData?): Call<GetCibilResponse?>?

    @POST("skipadhData")
    fun skipAadharData(@Body skipAadharData: SkipAadharData?): Call<GenericResponse?>?

    /*@POST("asend_otp")
    fun aadharOtpReq(@Body aadharOtpReq: AadharOtpReq?): Call<AadharOtpResponse?>?*/

    @POST("auth/aadhar-send-otp")
    fun aadharOtpReq(@Body aadharOtpReq: AadharOtpReq?): Call<AadharOtpResponse?>?

    /*@POST("averify_otp")
    fun verifyAadharotp(@Body verifyAadharOtpReq: VerifyAadharOtpReq?): Call<GenericResponse?>?*/
    @POST("auth/aadhar-verify-otp")
    fun verifyAadharotp(@Body verifyAadharOtpReq: VerifyAadharOtpReq?): Call<GenericResponse?>?

    /*@POST("get_bank_list")
    fun getBankList(@Body genericRequest: GenericRequest): Call<BankListRes?>?*/

    @POST("app/get-bank-list")
    fun getBankList(@Body genericRequest: GenericRequest): Call<BankListRes?>?

    /*@POST("get_bank_weblink")
    fun getBankWeblink(@Body getBankLinkReq: GetBankLinkReq): Call<WebLinkRes?>?*/

    @POST("auth/get-bank-weblink-v3")
    fun getBankWeblink(@Body getBankLinkReq: GetBankLinkReq): Call<WebLinkRes?>?
    @POST("auth/get-bank-weblink-by-type")
    fun getBankWebLinkType(@Body getBankLinkReq: GetBankLinkReq): Call<WebLinkRes?>?

    @POST("analyse_capability")
    fun analyseCapability(@Body analyseCapabilityReq: AnalyseCapabilityReq?): Call<RegisterDeviceResponse?>?

    /*@POST("getCityList")
    fun getCityList(@Body getCityListReq: GetCityListReq?): Call<CityListResponse?>?*/

    @POST("app/get-city-list")
    fun getCityList(@Body getCityListReq: GetCityListReq?): Call<CityListResponse?>?

    /*@POST("getAreaList")
    fun getAreaList(@Body getAreaListReq: GetAreaListReq?): Call<AreaListResponse?>?*/

    @POST("app/get-area-list")
    fun getAreaList(@Body getAreaListReq: GetAreaListReq?): Call<AreaListResponse?>?

    /*@POST("getBrandList")
    fun getBrandList(@Body getBrandListReq: GetBrandListReq): Call<GetBrandListResponse?>?*/

    @POST("app/get-brand-list")
    fun getBrandList(@Body getBrandListReq: GetBrandListReq): Call<GetBrandListResponse?>?

    /*@POST("getDealerList")
    fun getDealerList(@Body getDealerListReq: GetDealerListReq?): Call<DealerListResponse?>?*/

    @POST("app/get-dealer-list")
    fun getDealerList(@Body getDealerListReq: GetDealerListReq?): Call<DealerListResponse?>?

    /*@POST("getVarientList")
    fun getVarientList(@Body getVarientListReq: GetVarientListReq?): Call<VarientListResponse?>?*/

    @POST("app/get-varient-list")
    fun getVarientList(@Body getVarientListReq: GetVarientListReq?): Call<VarientListResponse?>?

    /*@POST("getVehiclesList")
    fun getVehiclesList(@Body getVehiclesListReq: GetVehiclesListReq?): Call<VehiclesListResponse?>?*/

    @POST("app/get-vehicles-list")
    fun getVehiclesList(@Body getVehiclesListReq: GetVehiclesListReq?): Call<VehiclesListResponse?>?

    /*@POST("getColorList")
    fun getColorList(@Body getColorListReq: GetColorListReq?): Call<ColorListResponse?>?*/

    @POST("app/get-color-list")
    fun getColorList(@Body getColorListReq: GetColorListReq?): Call<ColorListResponse?>?

    @POST("updateBankData")
    fun updateBankData(@Body getUpdateBankDataReq: GetUpdateBankDataReq?): Call<UpdateBankDataResponse?>?

    /*@POST("getVehicleDetails")
    fun getVehicleDetails(@Body getVehicleDetailsReq: GetVehicleDetailsReq?): Call<GetVehicleDetailsResponse?>?*/

    @POST("app/get-vehicle-details")
    fun getVehicleDetails(@Body getVehicleDetailsReq: GetVehicleDetailsReq?): Call<GetVehicleDetailsResponse?>?

    @POST("save_loan_consent")
    fun saveLoanConsent(@Body idTextData: IdTextData?): Call<SaveLoanConsentResponse?>?

    /*@POST("apply_twl")
    fun applyTWLoan(@Body applyTWLoanReq: ApplyTWLoanReq?): Call<ApplyTWLoanResponse?>?*/

    @POST("user/apply-loan-twl")
    fun applyTWLoan(@Body applyTWLoanReq: ApplyTWLoanReq?): Call<ApplyTWLoanResponse?>?

    /*@POST("getTwlTenureData")
    fun getTwlTenureData(@Body getTwlTenureDataReq: GetTwlTenureDataReq?): Call<TwlTenureDataResponse?>?*/

    @POST("user/twl-tenure-data")
    fun getTwlTenureData(@Body getTwlTenureDataReq: GetTwlTenureDataReq?): Call<TwlTenureDataResponse?>?

    @POST("twl_banner_images")
    fun twlBannerImages(@Body twlBannerImagesReq: TwlBannerImagesReq?): Call<TwlBannerImagesResponse?>?

    @POST("twlProcessingOrder")
    fun twlProcessingOrder(@Body twlProcessingOrderReq: TwlProcessingOrderReq?): Call<OrderData?>?

    /* @POST("createMandate")
     fun createMandate(@Body createMandateReq: CreateMandateReq?): Call<CreateMandateResponse?>?*/
    @POST("auth/authenticate-mandate-v3")
    fun createMandate(@Body createMandateReq: CreateMandateReq?): Call<CreateMandateResponse?>?

    @POST("submitMandate")
    fun submitMandate(@Body submitMandateReq: SubmitMandateReq?): Call<SubmitMandateResponse?>?

    @POST("twlActiveLoans")
    fun twlActiveLoans(@Body twlActiveLoansReq: TwlActiveLoansReq?): Call<TwlActiveLoansResponse?>?

    @POST("twlLoanDetails")
    fun twlLoanDetails(@Body twlLoanDetailsReq: TwlLoanDetailsReq?): Call<TwlLoanDetailsResponse?>?

    @POST("twlCancelLoan")
    fun twlCancelLoan(@Body cancelTwlLoanReq: CancelTwlLoanReq?): Call<CancelTwlLoanResponse?>?

    @Multipart
    @POST("uploadTWLQuotation/")
    fun uploadTWLQuotation(
        @Part file: MultipartBody.Part?,
        @Part("api_key") apiKey: RequestBody?,
        @Part("twl_id") twlId: RequestBody?,
    ): Call<FileUploadResponse>?

    /*@POST("createVKYC")
    fun createVKYC(@Body createKycReq: CreateKycReq?): Call<CreateVKYCResponse?>?*/

    @POST("auth/create-vkyc")
    fun createVKYC(@Body createKycReq: CreateKycReq?): Call<CreateVKYCResponse?>?

    /* @POST("submitVKYC")
     fun submitVKYC(@Body submitVKYCReq: SubmitVKYCReq?): Call<SubmitVKYCResponse?>?*/
    @POST("auth/submit-vkyc")
    fun submitVKYC(@Body submitVKYCReq: SubmitVKYCReq?): Call<SubmitVKYCResponse?>?

    /* @POST("sendVerifyEmail")
     fun sendVerifyEmail(@Body verifyAlternateEmailReq: VerifyAlternateEmailReq?): Call<GetVerifyAlternateEmailResponse?>?
     */
    @POST("auth/send-verify-email")
    fun sendVerifyEmail(@Body verifyAlternateEmailReq: VerifyAlternateEmailReq?): Call<GetVerifyAlternateEmailResponse?>?

    /*@POST("verifyEmailByOTP")
    fun verifyEmailByOTP(@Body verifyEmailByOTPReq: VerifyEmailByOTPReq?): Call<VerifyEmailByOTPReqResponse?>?
*/
    @POST("auth/verify-email-otp")
    fun verifyEmailByOTP(@Body verifyEmailByOTPReq: VerifyEmailByOTPReq?): Call<VerifyEmailByOTPReqResponse?>?

    @POST("getMandateList")
    fun getMandateList(@Body getMandateList: GetMandateList?): Call<GetMandateListResponse?>?

    @POST("savePrimaryMandate")
    fun savePrimaryMandate(@Body savePrimaryMandateReq: SavePrimaryMandateReq?): Call<SavePrimaryMandateResponse?>?

    @POST("submit_bank_change")
    fun submitBankChange(@Body submitBankChangeReq: SubmitBankChangeReq?): Call<SubmitBankChangeResponse?>?

    @POST("saveLatestSalSlip")
    fun saveLatestSalSlip(@Body submitBankChangeReq: SubmitBankChangeReq?): Call<SubmitBankChangeResponse?>?

    // Node Apis

    @POST("app/save-sms-data")
    fun saveAppData(@Body saveAppReq: SaveAppReq?): Call<SaveDataResponse?>?

    @POST("app/save-app-data")
    fun saveSmsData(@Body saveMessageReq: SaveMessageReq?): Call<SaveDataResponse?>?

    @POST("app/save-event-data")
    fun saveEventData(@Body saveCalendarEventsReq: SaveCalendarEventsReq?): Call<SaveDataResponse?>?

    @POST("app/check-promo-code")
    fun checkPromoCode(@Body checkPromoCodeReq: CheckPromoCodeReq?): Call<CheckPromoCodeResponse?>?

    @POST("app/web-contact-us")
    fun submitContactUs(@Body submitContactUsReq: SubmitContactUsReq?): Call<SubmitContactUsResponse?>

    @POST("auth/create-emandate-registration-link")
    fun razorPayCreateMandate(@Body razorPayCreateMandateReq: RazorPayCreateMandateReq?): Call<RazorPayCreateMandateResponse?>?

    @POST("auth/submit-emandate-status")
    fun razorPaySubmitMandate(@Body razorPaySubmitMandateReq: RazorPaySubmitMandateReq?): Call<RazorPaySubmitMandateResponse?>?

    @POST("cs/active-loans")
    fun csActiveLoan(@Body csActiveLoanReq: CSActiveLoanReq?): Call<CSActiveLoanResponse?>?

    @POST("rewards/get-coupon-categories")
    fun getCouponCategories(@Body couponCategoriesReq: GetCouponCategoriesReq?): Call<GetCouponCategoriesResponse?>?

    @POST("rewards/get-coupons")
    fun getCoupon(@Body couponsReq: GetCouponsReq?): Call<GetCouponsResponse?>?

    @POST("rewards/get-coupon-info")
    fun getCouponInfo(@Body couponInfoReq: GetCouponInfoReq?): Call<GetCouponInfoResponse?>?

    @POST("rewards/redeem-coupon")
    fun getRedeemCoupon(@Body redeemCouponReq: GetRedeemCouponReq?): Call<GetRedeemCouponResponse?>?

    @POST("rewards/get-redeemed-coupon-details")
    fun getRedeemCouponDetails(@Body redeemCouponDetailsReq: GetRedeemCouponDetailsReq?): Call<GetRedeemCouponDetailsResponse?>?

    @POST("rewards/email-coupon-details")
    fun getEmailCouponDetails(@Body emailCouponDetailsReq: GetEmailCouponDetailsReq?): Call<GetEmailCouponDetailsResponse?>?

    @POST("rewards/get-redeemed-coupons")
    fun getRedeemedCoupons(@Body redeemedCouponsReq: GetRedeemedCouponsReq?): Call<GetRedeemedCouponsResponse?>?

    @POST("cs/pr-save-sign")
    fun prSaveSign(@Body prSaveSignReq: PrSaveSignReq?): Call<CSGenericResponse?>?

    /* @POST("insert_five_references_data")
     fun saveReferences(@Body saveReferencesReq: SaveReferencesReq?): Call<SaveReferencesResponse?>?*/

    @POST("user/insert-five-references-data")
    fun saveReferences(@Body saveReferencesReq: SaveReferencesReq?): Call<SaveReferencesResponse?>?

    @POST("cs/check-flow-condition")
    fun saveSalary(@Body saveSalaryReq: SaveSalaryReq?): Call<SaveSalaryResponse?>?

    @POST("cs/get-pincodes-of-city")
    fun getPincodes(@Body pinCodesReq: GetPinCodesReq?): Call<GetPinCodesResponse?>?

    @POST("app/memb-upgrade-consent-log")
    fun memberUpgradeConsent(@Body memberUpgradeConsentReq: MemberUpgradeConsentReq?): Call<MemberUpgradeConsentResponse?>?

    @Multipart
    @POST("app/web-contact-us-two")
    fun submitContactUsWeb(
        @Part selectedFilePath1: MultipartBody.Part?,
        @Part selectedFilePath2: MultipartBody.Part?,
        @Part selectedFilePath3: MultipartBody.Part?,
        @Part("api_key") apiKey: RequestBody?,
        @Part("web_cnt_existing_customer") webCntExistingCustomer: RequestBody?,
        @Part("web_cnt_name") webCntName: RequestBody?,
        @Part("web_cnt_email") webCntEmail: RequestBody?,
        @Part("web_cnt_mobile_number") webCntMobileNumber: RequestBody?,
        @Part("platform") platform: RequestBody?,
        @Part("web_cnt_query") webCntQuery: RequestBody?,
        @Part("web_cnt_sub_query") webCntSubQuery: RequestBody?,
        @Part("web_cnt_message") webCntMessage: RequestBody?,
    ): Call<FileUploadResponse>?

    @POST("app/get-additional-docs")
    fun getAdditionalDocuments(@Body getAdditionalDocReq: GetAdditionalDocReq?): Call<GetAdditionalDocResponse?>?

    @POST("app/upload-add-docs")
    fun uploadAdditionalDocuments(@Body uploadAdditionalDocReq: UploadAdditionalDocReq?): Call<UploadAdditionalDocResponse?>?

    @POST("app/get-pending-documents")
    fun getPendingDocuments(@Body getPendingDocReq: GetPendingDocReq?): Call<GetPendingDocResponse?>?

    @POST("app/upload-pending-docs")
    fun uploadPendingDocuments(@Body uploadPendingDocReq: UploadPendingDocReq?): Call<UploadPendingDocResponse?>?

    @POST("updateAdharManualDocs")
    fun updateAdharManualDocs(@Body updateAdharManualDocsReq: SkipAadharData?): Call<GenericResponse?>?

    @POST("auth/get-analysis-type")
    fun getAnalysisType(@Body analysisTypeReq: GetAnalysisTypeReq?): Call<GetAnalysisTypeResponse?>?

    @POST("app/save-creditcard-data")
    fun saveCreditCardData(@Body genericRequest: GenericRequest?): Call<SaveCCDataResponse?>?

    @POST("app/spends-analysis")
    fun spendsAnalysis(@Body spendsDataReq: GetSpendsDataReq?): Call<GetSpendsResponse?>?

    @POST("auth/get-captcha-aadhar")
    fun getCaptchaAadhaar(@Body captchaReq: GetCaptchaReq?): Call<GetCaptchaResponse?>?

    @POST("app/update-location")
    fun updateNewLocation(@Body updateNewLocationReq: UpdateNewLocationReq?): Call<UpdateNewLocationResponse?>?

    @POST("apply_loan_service_data")
    fun applyLoanServiceData(@Body applyLoanServiceDataReq: ApplyLoanServiceDataReq?): Call<ApplyLoanServiceDataResponse?>?

    @POST("user/save-registration-one")
    fun saveRegistrationOne(@Body saveRegistrationOneReq: SaveRegistrationOneReq?): Call<SaveRegistrationOneResponse?>?

    @POST("user/save-registration-two")
    fun saveRegistrationTwo(@Body saveRegistrationTwoReq: SaveRegistrationTwoReq?): Call<SaveRegistrationTwoResponse?>?

    @POST("user/save-registration-three")
    fun saveRegistrationThree(@Body saveRegistrationThreeReq: SaveRegistrationThreeReq?): Call<SaveRegistrationThreeResponse?>?

    @POST("getHoldStatus")
    fun getHoldStatus(@Body holdStatusReq: GetHoldStatusReq?): Call<GetHoldStatusResponse?>?

    @POST("user/state-city-from-pincode")
    fun stateAndCityFromPinCode(@Body pinCodeReq: PinCodeReq?): Call<PinCodeResponse?>?

    @POST("auth/sec-mobile-send-otp")
    fun verifyAlternateMobile(@Body verifyAlternateMobileReq: VerifyAlternateMobileReq?): Call<GetVerifyAlternateMobileResponse?>?

    @POST("auth//sec-mobile-verify-otp")
    fun verifyAltMobileByOTP(@Body verifyAltMobileByOTPReq: VerifyAltMobileByOTPReq?): Call<VerifyAltMobileByOTPResponse?>?

    @POST("app/get-designation")
    fun getDesignationList(@Body designationListReq: GetDesignationListReq?): Call<DesignationListResponse?>?

    @POST("user/check-loan-status")
    fun checkLoanStatus(@Body checkLoanStatusForDeletionReq: CheckLoanStatusForDeletionReq?): Call<CheckLoanStatusForDeletionResponse?>?

    @POST("ev/ev-send-otp")
    fun evSendOTP(@Body sendOTPDeletionReq: SendOTPDeletionReq?): Call<SendOTPDeletionResponse?>?

    @POST("auth/verify-otp-account-delete")
    fun verifyOtpAccountDelete(@Body verifyOTPDeletionReq: VerifyOTPDeletionReq?): Call<VerifyOTPDeletionResponse?>?

    @POST("user/delete-consent")
    fun deleteConsent(@Body deleteConsentReq: DeleteConsentReq?): Call<DeleteConsentResponse?>?

    @POST("app/get-loan-range")
    fun getLoanRange(@Body loanRangeReq: GetLoanRangeReq?): Call<GetLoanRangeResponse?>?

    @POST("app/register-loan-req")
    fun registerLoan(@Body registerLoanReq: RegisterLoanReq?): Call<RegisterLoanResponse?>?

    @POST("app/homepage-banner-images-v2")
    fun homePageBannerImages(@Body bannerImageRequest: BannerImageRequest?): Call<BannerImageResponse?>?

    @POST("user/get-eligible-offers")
    fun getEligibleOffers(@Body eligibleOffersReq: GetEligibleOffersReq?): Call<GetEligibleOffersResponse?>?

    @POST("user/get-eligible-offer-details")
    fun getEligibleOfferDetails(@Body eligibleOfferDetailReq: GetEligibleOfferDetailReq?): Call<GetEligibleOfferDetailsResponse?>?

    @POST("bbps/pay-bill")
    fun bbpsBillPay(@Body genericRequest: GenericRequest?): Call<BbpsBillPayResponse?>?

    @POST("bbps/link-generation")
    fun bbpsLinkGenerator(@Body bbpsLinkGeneratorReq: GetBbpsLinkGeneratorReq?): Call<GetBbpsLinkGeneratorResponse?>?

    @POST("user/get-process-page-content")
    fun getProcessPageContent(@Body genericRequest: GenericRequest?): Call<GetProcessPageContentResponse?>?

    @POST("app/offer-scroll-v2")
    fun offerScroll(@Body genericRequest: GenericRequest?): Call<OfferScrollResponse?>?

    @POST("auth/easebuzz-response")
    fun easebuzzResponseHandler(@Body easebuzzResponseRequest: EasebuzzResponseRequest?): Call<EasebuzzResponseResponse?>?

    @POST("auth/transactions-history")
    fun setTransactionData(@Body transactionReq: GetTransactionReq?): Call<GetTransactionResponse?>?

    @POST("signzy/get-signzy-url")
    fun openDigiLocker(@Body openDigiLockerReq: OpenDigiLockerReq?): Call<OpenDigiLockerResponse?>?

    @POST("auth/get-analysis-list")
    fun getAnalysisList(@Body getAnalysisTypeReq: GetAnalysisTypeReq): Call<GetAnalysisListResponse?>?


}