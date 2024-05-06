package com.capitalnowapp.mobile.constants;

import android.Manifest;

import com.capitalnowapp.mobile.BuildConfig;

public class Constants {

    public static final String LOADING_MESSAGE = "Loading.. Please wait...";

    public static final int DEVICE_MIN_WIDTH = 1100; // i.e. pixels, based on this, we will consider device as small or large
    public static final int VOLLEY_SOCKET_TIMEOUT = 30000; // 30 seconds.

    // SHARED PREFERENCE CONSTANTS
    public static final String SHARED_PREFERENCE_KEY = "CapitalNowPrefs";
    public static final String SP_IS_GET_STARTED_SHOWN = "IsGetStartedShown";
    public static final String SP_IS_REGISTER_DEVICE = "IsGetRegisterDevice";
    public static final String SP_IS_USER_LOGGED_IN = "IsUserLoggedIn";
    public static final String IS_PERMISSION_AGGREED = "IsPermissionAggreed";
    public static final String SP_ENCRYPTED_USER_ID = "EncryptedUserId";
    public static final String SP_USER_STATUS_ID = "UserStatusId";
    public static final String SP_REQUIRED_DOCUMENTS = "RequiredDocuments";
    public static final String SP_APPLY_LOAN_DATA = "ApplyLoanData";
    public static final String SP_APPLY_LOAN_EMI_DATA = "ApplyLoanEMIData";
    public static final String SP_APPLY_NEW_LOAN_DATA = "ApplyNewLoanData";
    public static final String SP_LIMIT_EXHAUSTED_MESSAGE = "Message";
    public static final String SP_IS_LIMIT_EXHAUSTED = "IsLimitExhausted";
    public static final String SP_DEVICE_UNIQUE_ID = "DeviceUniqueId";
    public static final String SP_DEVICE_TOKEN = "DeviceToken";
    public static final String SP_LINKED_IN_ACCESS_TOKEN = "LinkedInAccessToken";
    public static final String SP_LINKED_IN_ACCESS_TOKEN_EXPIRY_DATE = "LinkedInAccessTokenExpireDate";
    public static final String SP_REFER_CODE = "refer_code";
    public static final String SP_REFER_CODE_IS_REGISTERED = "refer_code_registered";
    public static final String RAZOR_PAY_API_KEY = "RAZOR_PAY_API_KEY";

    public static final String SelectedDealerId = "selectedDealerId";
    public static final String SelectedVehicleId = "selectedVehicleId";
    public static final String SelectedVehiclePrice = "selectedVehiclePrice";
    public static final String SelectedVehicleArea = "selectedVehicleArea";
    public static final String SelectedVehicleCity = "selectedVehicleCity";
    public static final String SelectedVehicleDealer = "selectedVehicleDealer";
    public static final String SelectedVehicleBrand = "selectedVehicleBrand";
    public static final String Loan_Limit = "limit";
    public static final String From_Vehicle_Details = "From_Vehicle_Details";

    // Bundle Constants
    public static final String BUNDLE_USER_DATA = "UserData";
    public static final String BUNDLE_USER_ONE_STEP_REG_DATA = "UserOneStepRegData";
    public static final String BUNDLE_LINKED_IN_AUTHORIZATION_CODE = "LinkedInAuthorizationCode";
    public static final String BUNDLE_LINKED_IN_ERROR_CODE = "LinkedInErrorCode";
    public static final String BUNDLE_LINKED_IN_ERROR_DESCRIPTION = "LinkedInErrorDescription";
    public static final String BUNDLE_PN_TITLE = "PN_Title";
    public static final String BUNDLE_PN_MESSAGE = "PN_Message";
    public static final String BUNDLE_PN_IMAGE_URL = "PN_ImageURL";
    public static final String BUNDLE_PN_REDIRECT_CODE = "PN_RedirectCode";
    public static final String BUNDLE_REFER_CODE_SMS_MSG = "refer_code_sms_msg";
    public static final String BUNDLE_REFER_CODE_EMAIL_MSG = "refer_code_email_msg";
    public static final String BUNDLE_REFER_CODE_SOCIAL_MSG = "refer_code_social_msg";
    public static final String BUNDLE_PAYMENT_STATUS = "PAYMENT_STATUS";
    public static final String LOGGED_TIME = "logged_time";
    public static final String USER_TOKEN = "user_token";
    public static final int STATUS_CODE_UNAUTHORISED = 401;

    // Permissions
    public static String[] PERMISSIONS_READ_EXTERNAL_STORAGE = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
    public static String PERMISSION_READ_EXTERNAL_STORAGE = Manifest.permission.READ_EXTERNAL_STORAGE;
    public static String PERMISSION_READ_CALENDAR = Manifest.permission.READ_CALENDAR;
    public static String[] PERMISSION_READ_CALANDARS = {Manifest.permission.READ_CALENDAR};
    public static String PERMISSION_READ_SMS = Manifest.permission.READ_SMS;
    public static String[] PERMISSION_READ_SMSS = {Manifest.permission.READ_SMS};
    public static String[] PERMISSIONS_GET_CURRENT_LOCATION = {Manifest.permission.ACCESS_COARSE_LOCATION};
    public static String PERMISSION_GET_CURRENT_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;

    // Activity Request Codes
    public static final int REQUEST_CODE_READ_EXTERNAL_STORAGE = 100;
    public static final int REQUEST_CODE_CHOOSE_DOCUMENT_TO_UPLOAD = 101;
    public static final int REQUEST_CODE_GET_CURRENT_LOCATION = 103;
    public static final int REQUEST_CODE_FOR_LINKED_IN_LOGIN = 105;
    public static final int REQUEST_CODE_FOR_GOOGLE_LOGIN = 107;
    public static final int REQUEST_CODE_FOR_GOOGLE_CONTACTS_RECOVERABLE = 108;
    public static final int REQUEST_CODE_FOR_CONTACTS_1 = 111;
    public static final int OPEN_DOCUMENT_REQUEST_CODE = 112;


    // Alert Dialog Request Codes
    public static final int ALERT_DIALOG_REQUEST_CODE_COMMON = 0;

    // User Status Codes
    public static final String USER_STATUS_ONE_STEP_REG = "1";
    public static String CURRENT_SCREEN = "";

    // Status Redirect Codes
    public static final int STATUS_REDIRECT_CODE_PENDING_DOCUMENTS = 1;
    public static final int STATUS_REDIRECT_CODE_LATEST_DOCUMENTS = 2;
    public static final int STATUS_REDIRECT_CODE_FIVE_REFERENCES = 3;
    public static final int STATUS_REDIRECT_CODE_APPLY_LOAN = 4;
    public static final int STATUS_REDIRECT_CODE_PAN = 5;
    public static final int STATUS_REDIRECT_CODE_AADHAR = 6;
    public static final int STATUS_REDIRECT_CODE_SIGNATURE = 7;
    public static final int STATUS_REDIRECT_CODE_BANK_STATEMENT = 8;
    public static final int STATUS_REDIRECT_CODE_BORROWER_AGGREMENT = 9;
    public static final int STATUS_REDIRECT_CODE_BANK_MISSING_DETAILS = 10;
    public static final int STATUS_REDIRECT_CODE_NACH = 11;
    public static final int STATUS_REDIRECT_CODE_FACEMATCH = 12;
    public static final int STATUS_REDIRECT_CODE_SELECT_BANK = 13;
    public static final int STATUS_REDIRECT_CODE_OFFER_REG_1 = 14;
    public static final int STATUS_REDIRECT_CODE_OFFER_REG_2 = 15;
    public static final int STATUS_REDIRECT_CODE_OFFER_REG_3 = 16;
    public static final int STATUS_REDIRECT_CODE_OFFER_REG_4 = 17;
    public static final int STATUS_REDIRECT_CODE_OFFER_PAN = 18;
    public static final int STATUS_REDIRECT_CODE_OFFER_APPLY_LOAN = 19;
    public static final int STATUS_REDIRECT_CODE_OFFER_SAL_SLIP = 20;
    public static final int STATUS_REDIRECT_CODE_OFFER_OFFLINE_AADHAR = 21;
    public static final int STATUS_REDIRECT_CODE_OFFER_SELFIE = 22;
    public static final int STATUS_REDIRECT_CODE_OFFER_BANK_STATEMENT = 23;
    public static final int STATUS_REDIRECT_CODE_OFFER_FINAL_OFFER = 24;
    public static final int STATUS_REDIRECT_CODE_OFFER_E_NACH = 25;
    public static final int STATUS_REDIRECT_CODE_OFFER_KFS = 26;
    public static final int STATUS_REDIRECT_CODE_OFFER_FINAL_STATUS = 27;
    public static final int STATUS_REDIRECT_CODE_OFFER_REF = 28;
    public static final int STATUS_REDIRECT_CODE_OFFER_LOADER = 29;
    public static final int STATUS_REDIRECT_CODE_OFFER_GENERATE_OFFER = 30;
    public static final int STATUS_REDIRECT_CODE_OFFER_UNDER_REVIEW = 31;
    public static final int STATUS_REDIRECT_SALARY_NEW = 32;
    public static final int STATUS_REDIRECT_REGISTRATION = 33;
    public static final int STATUS_REDIRECT_REG_1 = 34;
    public static final int STATUS_REDIRECT_REG_2 = 35;
    public static final int STATUS_REDIRECT_REG_3 = 36;
    public static final int STATUS_REDIRECT_HOLD = 37;
    public static final int STATUS_REDIRECT_CODE_NEW_LOAN = 38;
    public static final int STATUS_REDIRECT_CODE_NEW_APPLY_LOAN = 39;
    public static final int STATUS_REDIRECT_CODE_UNDER_REVIEW = 40;

    public static final int STATUS_REDIRECT_DIGILOCKER = 41;

    // Location Fetching Related Constants

    // Location Updates Interval - 5 minutes
    public static final int UPDATE_INTERVAL = 5 * 60 * 1000;

    // Fastest Updates Interval - 3 minutes
    // Location updates will be received if another app is requesting the locations then our app can handle
    public static final int FASTEST_UPDATE_INTERVAL = 3 * 60 * 1000;

    public static final String IS_REQUESTING_UPDATES = "is_requesting_updates";
    public static final String LAST_KNOWN_LOCATION = "last_known_location";

    public enum OrientationType {
        PORTRAIT,
        LANDSCAPE
    }

    public enum ButtonType {
        POSITIVE,
        NEGATIVE
    }

    public enum RequestCode {
        DEFAULT
    }

    public enum RequestFrom {
        LOGIN_PAGE,
        FORGOT_PASSWORD_PAGE,
        HOME_PAGE,
        APPLY_LOAN,
        VEHICLE_APPLY_LOAN,
        MY_LOANS,
        PROFILE_PAGE,
        APPLY_LOAN_OTP_PAGE,
        PENDING_DOCUMENTS,
        LATEST_DOCUMENTS,
        ONE_STEP_REGISTRATION,
        TWO_STEP_REGISTRATION,
        REFER_AND_EARN,
        REQUEST_BANK_CHANGE,

        ADDRESS_PINCODE
    }

    public static final String APPLY_LOAN_DUE_DATE_DISPLAY_FORMAT = "dd/MM/y";
    public static final String SERVER_DATE_FORMAT = "y-MM-dd";
    public static final String LOAN_CLOSED_DATE_DISPLAY_FORMAT = "dd/MMM/y";
    public static final String DOB_DATE_FORMAT = "dd-MM-y";
    public static final String YYYY_MM_DD_HH_MM_SS = "yyyy-mm-dd HH:mm:ss";

    public static final String APP_TYPE = "A";

    public static final String LINKED_IN_CLIENT_ID = "86uor9s1q8pzow";
    public static final String LINKED_IN_CLIENT_SECRET = "9OCay3ZgjSwiHb9Q";

    public static final String LINKED_IN_REDIRECT_URL = "https://localhost";
    public static final String LINKED_IN_STATE = "qc_linkedin_login";

    public static final String LINKED_IN_URL = "https://api.linkedin.com/v1/people/~:(id,first-name,last-name,formatted-name,email-address,picture-url,public-profile-url,num-connections)";
    public static final String LINKED_IN_AUTHORIZATION_URL = "https://www.linkedin.com/oauth/v2/authorization";
    public static final String LINKED_IN_ACCESS_TOKEN_URL = "https://www.linkedin.com/uas/oauth2/accessToken";
    public static final String GET_LINKED_IN_MEMBER_PROFILE_URL = "https://api.linkedin.com/v2/me?projection=(id,firstName,lastName,profilePicture(displayImage~:playableStreams))";
    public static final String GET_LINKED_IN_MEMBER_EMAIL_ADDRESS_URL = "https://api.linkedin.com/v2/emailAddress?q=members&projection=(elements*(handle~))";

    public static final String CN_API_KEY = "CN5YAGNGU1A";

    public static final String CN_SMS_SENDER_ADDRESS = "QCRDIT";

    public static final String FRESH_CHAT_APP_ID = "71b55ed0-91a4-4963-9533-3a6cdeb4d916";
    public static final String FRESH_CHAT_APP_KEY = "3f0924bd-2cc3-460a-95e8-738143baee50";

    public static final String MASTER_DATA_CITIES = "1";
    public static final String MASTER_DATA_COLLEGE_NAMES = "2";
    public static final String MASTER_DATA_CREDIT_CARD_TYPES = "3";

    public static final String SMS_RETRIEVER_HASH_KEY = "Ib0UrXiNG3w";

    public static final int FILE_UPLOAD_LIMIT = 10; // ic_federal_lin MB's

    public static final int TWOMB_FILE_UPLOAD_LIMIT = 4; // ic_federal_lin MB's

    public static final String APP_LINK = "https://play.google.com/store/apps/details?id=com.capitalnowapp.mobile";

    /* All Application Service URL's Listed Below */

    public static final String SERVER_URL = "https://www.capitalnow.in/";
    public static final String MASTER_JSON = "https://s3.ap-south-1.amazonaws.com/cdn.cn/registration_fields.json";
    public static final String MASTER_JSON_REGISTRATION = "https://s3.ap-south-1.amazonaws.com/cdn.cn/registration_fields_dev.json";
    public static final String DEPARTMENT_JSON_REGISTRATION = "https://s3.ap-south-1.amazonaws.com/cdn.cn/department_list.json";
    public static final String DESIGNATION_JSON_REGISTRATION = "https://s3.ap-south-1.amazonaws.com/cdn.cn/designtation_list.json";
    public static final String PINCODE_API = "http://www.postalpincode.in/api/pincode/";

    //Development & Production
    public static final String MAIN_URL = BuildConfig.DEBUG ?
            "https://api.capitalnow.in/index.php/CNApp_Web_Services_1_1_2/" : "https://api.capitalnow.in/index.php/CNApp_Web_Services_1_1_2/";
            //"https://api.staging.capitalnow.in/index.php/CNApp_Web_Services_1_1_2/" : "https://api.capitalnow.in/index.php/CNApp_Web_Services_1_1_1";
    //Response Codes
    public static final String MAIN_URL_1 = "https://api2.capitalnow.in";
    //public static final String MAIN_URL_1 = "https://api2.staging.capitalnow.in";
    public static final String STATUS_SUCCESS = "success";
    public static final String STATUS_ERROR = "error";
    public static final String STATUS_FAILURE = "failure";
    public static final String LIMIT_EXHAUSTED = "limit_exhausted";
    public static final Boolean StatusSuccess = true;
    public static final Boolean StatusFailure = false;

    public static final String APPLY_LOAN_DATA = "apply_loan_service_data";
    public static final String INSERT_FIVE_REFS = "insert_five_references_data";
    public static final String UPLOAD_DOCUMENTS = "insert_docs";
    public static final String MY_LOANS = "update_my_loan_service_data";
    public static final String ACTIVE_LOANS = "active_loans";
    public static final String ACTIVE_LOANS_NODE = "auth/active-loans-v2";
    public static final String CLEAR_LOANS = "cleared_loans";
    public static final String CLEAR_LOANS_NODE = "user/cleared-loan";
    public static final String TWL_CLEAR_LOANS = "twl_cleared_loans";
    public static final String PROFILE_DATA = "profile_data";
    public static final String PROFILE_BANNERS = "banner_images";
    public static final String FROM_TWL = "fromTwlApplyNow";
    public static final String PROFILE_BANNERS_NODE = "app/banner-images";
    public static final String ORDER_DATA = "order_data";
    public static final String TWL_ORDER_DATA = "twl_order_data";
    public static final String SAVE_PAYMENT_DATA = "save_payment_data";
    public static final String SAVE_TWL_PAYMENT_DATA = "twl_save_payment_data";
    public static final String SAVE_PAYU_DATA = "storePayUSuccessRepayment";
    public static final String SAVE_TWL_PAYU_DATA = "twlStorePayUSuccessRepayment";
    public static final String SAVE_CCA_DATA = "storeCcSuccessRepayment";
    public static final String SAVE_TWL_CCA_DATA = "twlStoreCcSuccessRepayment";
    public static final String GENERATE_OTP = "apply_loan";
    public static final String VERIFY_OTP = "verify_otp";
    public static final String CHECK_CAN_APPLY  = "check_can_apply ";
    public static final String SOCIAL_REGISTER = "socialRegister";
    public static final String NODE_SOCIAL_REGISTER = "auth/social-register";
    public static final String PROFILE_SAVE_AJAX_DATA = "profile_save_ajax_data";
    public static final String SAVE_ONETIME_REG = "save_onetime_reg";
    public static final String SAVE_ONETIME_REG_NODE = "user/save-one-time-registration";
    public static final String SAVE_ADDRESS = "addressSave/";
    public static final String SAVE_BANK_UPDATE_DETAILS = "submit_request_bank_change";
    public static final String LENDER_BANK_DETAILS = "lender_bank_details";
    public static final String NOTIFICATIONS = "notificationslist";
    public static final String APP_RATING = "user_app_rated";
    public static final String GET_APP_VERSION = "get_app_version";
    public static final String GET_MASTER_DATA = "get_city_clg_card_data";
    public static final String GET_MASTER_DATA_NODE = "app/get-city-clg-card-data";
    public static final String SAVE_TWO_STEP_REG = "check_profile_good_bad";
    public static final String GET_USER_REFERRAL_CODE = "user_referral_code";
    public static final String GET_USER_REFERRAL_CODE_NODE = "user/user-referral-code";
    public static final String GET_USER_REWARD_CASHBACK_DATA = "get_reward_cashback_data";
    public static final String AgreePreLoanAgreement = "agreePreLoanAgreement";
    public static final String GetCompanies = "getCompanies";
    public static final String GetCompanies_Node = "app/get-companies";

    public static final String GetDesignation_Node = "app/get-designation";
    public static final String NODE_GET_APP_VERSION = "app/get-app-version";
    /*new keys added*/
    public static final String USER_ONE_STEP_REG_DATA = "user_one_step_data";
    public static final String USER_DETAILS_DATA = "user_details_data";
    public static final String USER_REGISTRATION_DATA = "user_registration_data";
    public static final String PERMISSIONS_REQUESTED = "permissions_requested_to_user";
    public static final String MADE_IN_INDIA_SHOWN = "made_in_india";
    public static final String DOCS_HELP_SHOWN = "help_shown";

    public static final String PENDING_DOCS_DATA = "pending_docs_data";

    public interface FileUploadAjaxCallKeys {
        public static final String ID_PROOF = "id_proof";
        public static final String ADDRESS_PROOF = "address_proof";
        public static final String PRESENT_ADDRESS_PROOF = "present_address_proof";
        public static final String SAL_SLIP = "sal_slip";
        public static final String BANK_STATEMENTS = "bank_statements";
        public static final String SIGNATURE = "customersignature";

        public static final String PRE_ADDRESS = "pre_address";
        public static final String LATEST_SAL_SLIP = "lat_sal_slip";
        public static final String PROOF_OF_ID = "proof_of_id";
        public static final String PROOF_OF_PER_ADDRESS = "proof_of_per_address";
        public static final String PROOF_OF_EMP = "proof_of_emp";
        public static final String LOAN_NOC = "loan_noc";
        public static final String BOUNCE_CLEARANCE_PROOF = "bounce_clearance_proof";
    }

    public interface CCA {
        public static final String GET_RSA = "https://app.capitalnow.in/cca_mobile_handler/GetRSA.php";
    }

    public interface AvenuesParams {
        public static final String ACCESS_CODE = "access_code";
        public static final String MERCHANT_ID = "merchant_id";
        public static final String ORDER_ID = "order_id";
        public static final String AMOUNT = "amount";
        public static final String CURRENCY = "currency";
        public static final String ENC_VAL = "enc_val";
        public static final String DataAccept = "data_accept";
        public static final String REDIRECT_URL = "redirect_url";
        public static final String CANCEL_URL = "cancel_url";
        public static final String RSA_KEY_URL = "rsa_key_url";
        public static final String Merchant_Param2 = "merchant_param2";

        public static final String PARAMETER_SEP = "&";
        public static final String PARAMETER_EQUALS = "=";
    }

    public interface CCAOrderStatus {
        public static final String Success = "Success";
        public static final String Fail = "Failure";
        public static final String Cancel = "Aborted";
        public static final String Awaited = "Awaited";

        // Not a status.. This is the msg gets when initiation failed with status Aborted
        public static final String Initiation_Failed = "initiation failed";
    }

    public interface CHATBOT {
        public static final String SAY_HELLO = "SAY_HELLO";
        public static final String BOT_MESSAGE = "BOT_MESSAGE";
        public static final String QUESTIONS = "QUESTIONS";
        public static final String SELECTED_QUESTION = "SELECTED_QUESTION";
    }

    public interface FIN_BIT_REFERRER {
        public static final String Req_Bank_Change = "bank_change_req";
        public static final String Latest_Docs = "latest_docs";
    }

}
