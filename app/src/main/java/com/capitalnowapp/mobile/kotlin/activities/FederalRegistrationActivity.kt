package com.capitalnowapp.mobile.kotlin.activities

//import io.branch.referral.util.BranchEvent
import android.accounts.Account
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Window
import android.widget.FrameLayout
import android.widget.TextView
import androidx.annotation.Nullable
import com.appsflyer.AppsFlyerLib
import com.capitalnowapp.mobile.R
import com.capitalnowapp.mobile.activities.BaseActivity
import com.capitalnowapp.mobile.activities.LinkedInWebViewActivity
import com.capitalnowapp.mobile.activities.LoginActivity
import com.capitalnowapp.mobile.asynctasks.CNAsyncRequest
import com.capitalnowapp.mobile.beans.SocialRegistration
import com.capitalnowapp.mobile.beans.UserBasicData
import com.capitalnowapp.mobile.constants.Constants
import com.capitalnowapp.mobile.customviews.CNButton
import com.capitalnowapp.mobile.customviews.CNProgressDialog
import com.capitalnowapp.mobile.customviews.CNTextView
import com.capitalnowapp.mobile.databinding.ActivityFederalRegistrationBinding
import com.capitalnowapp.mobile.util.TrackingUtil
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.GraphRequest
import com.facebook.appevents.AppEventsLogger
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.auth.api.signin.GoogleSignInStatusCodes
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.gson.Gson
import com.linkedin.platform.APIHelper
import com.linkedin.platform.LISessionManager
import com.linkedin.platform.errors.LIApiError
import com.linkedin.platform.errors.LIAuthError
import com.linkedin.platform.listeners.ApiListener
import com.linkedin.platform.listeners.ApiResponse
import com.linkedin.platform.listeners.AuthListener
import com.linkedin.platform.utils.Scope
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.util.Arrays
import java.util.Calendar


class FederalRegistrationActivity : BaseActivity() {
    private lateinit var tvLogoutFedReg: TextView
    private var fbCallbackManager: CallbackManager? = null
    private val currentLocation: String? = null
    private var deviceUniqueId: kotlin.String? = ""
    private var deviceToken: kotlin.String? = ""
    private var mRequestingLocationUpdates = false
    private var mGoogleSignInClient: GoogleSignInClient? = null
    private var mGoogleSignInAccount: GoogleSignInAccount? = null
    private var mGoogleAccount: Account? = null
    private var userBasicData: UserBasicData? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityFederalRegistrationBinding.inflate(layoutInflater)
        val rootView = binding.root
        setContentView(rootView)
        tvLogoutFedReg = findViewById(R.id.tvLogoutFedReg)
        tvLogoutFedReg.setOnClickListener {
            val obj = JSONObject()
            try {
                obj.put("mobileNumber","")
                obj.put(getString(R.string.interaction_type),"Logout Clicked")
            } catch (e: JSONException) {
                throw RuntimeException(e)
            }
            TrackingUtil.pushEvent(obj, getString(R.string.social_login_interacted))
            showLogoutDialog()
        }

        initView(binding)
    }

    private fun initView(binding: ActivityFederalRegistrationBinding?) {
        val obj = JSONObject()
        try {
            obj.put("mobileNumber","")
        } catch (e: JSONException) {
            throw RuntimeException(e)
        }
        TrackingUtil.pushEvent(obj, getString(R.string.social_login_landed))
        /* Login With Google Starts Here */
        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestIdToken(resources.getString(R.string.google_oauth_2_0_client_id)) //.requestServerAuthCode(getResources().getString(R.string.google_oauth_2_0_client_id))
                .build()

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)


        // Facebook Login Starts Here
        fbCallbackManager = CallbackManager.Factory.create()


        // This is Facebook Login button, we are not using this.
        /*fb_login_button = (LoginButton) findViewById(R.id.fb_login_button);
            fb_login_button.setReadPermissions(Arrays.asList("public_profile", "email"));

            // Callback registration
            fb_login_button.registerCallback(fbCallbackManager, facebookCallback);*/
       LoginManager.getInstance().registerCallback(fbCallbackManager, facebookCallback as FacebookCallback<LoginResult>?)


        binding?.ivGoogle?.setOnClickListener {
            val obj = JSONObject()
            try {
                obj.put("mobileNumber","")
                obj.put(getString(R.string.interaction_type),"Google Button Clicked")
            } catch (e: JSONException) {
                throw RuntimeException(e)
            }
            TrackingUtil.pushEvent(obj, getString(R.string.social_login_interacted))
            initiateGoogleLogin()
        }
        binding?.ivFb?.setOnClickListener {
            initiateFbLogin()
        }
        binding?.ivLn?.setOnClickListener {
            val obj = JSONObject()
            try {
                obj.put("mobileNumber","")
                obj.put(getString(R.string.interaction_type),"Linkedin Button Clicked")
            } catch (e: JSONException) {
                throw RuntimeException(e)
            }
            TrackingUtil.pushEvent(obj, getString(R.string.social_login_interacted))
            initiateLnLogin()
        }
    }

    private fun showLogoutDialog() {

        val alertDialog = Dialog(activityContext)
        alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        alertDialog.setContentView(R.layout.logout_dialog)
        alertDialog.window!!.setLayout(
            FrameLayout.LayoutParams.MATCH_PARENT,
            FrameLayout.LayoutParams.MATCH_PARENT
        )
        alertDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        alertDialog.setCanceledOnTouchOutside(true)
        val button = alertDialog.findViewById<CNButton>(R.id.btLogout)
        val cancel = alertDialog.findViewById<CNButton>(R.id.btCancel)
        alertDialog.findViewById<CNTextView>(R.id.tvUserName).text = "Hi Guest"
        button.setOnClickListener {
            userDetails = null
            sharedPreferences.putString(Constants.USER_DETAILS_DATA, Gson().toJson(userDetails))
            sharedPreferences.putString(Constants.USER_REGISTRATION_DATA, null)
            val logInIntent = Intent(activityContext, LoginActivity::class.java)
            logInIntent.flags =
                Intent.FLAG_ACTIVITY_NO_ANIMATION or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(logInIntent)
            overridePendingTransition(R.anim.left_in, R.anim.right_out)
            finish()
        }
        cancel.setOnClickListener {
            alertDialog.dismiss()
        }

        alertDialog.setCancelable(true)
        alertDialog.show()
    }

    private fun initiateLnLogin() {
        val linkedInAccessToken: String = sharedPreferences.getString(Constants.SP_LINKED_IN_ACCESS_TOKEN)
        if (linkedInAccessToken.isEmpty()) {
            val intent = Intent(this, LinkedInWebViewActivity::class.java)
            startActivityForResult(intent, Constants.REQUEST_CODE_FOR_LINKED_IN_LOGIN)
            overridePendingTransition(R.anim.slide_in_up, R.anim.slide_down_out)
        } else {
            getLinkedInUserDetails(linkedInAccessToken)
        }
    }

    private fun initiateFbLogin() {


        /*
         * In order to resolve FacebookCallback.onCancel is getting called every time (if you are using your own button instead of Facebook Login button)
         * when trying to login issue, we are calling logout before login.
         */LoginManager.getInstance().logOut()

        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile, email"))
    }

    var facebookCallback: FacebookCallback<*> = object : FacebookCallback<LoginResult> {
        override fun onSuccess(loginResult: LoginResult) {
            Log.d(LoginActivity.TAG, "FB AccessToken : " + loginResult.accessToken.token)
            val request = GraphRequest.newMeRequest(
                    loginResult.accessToken
            ) { `object`, response ->
                try {
                    if (`object` != null) {
                        val socialRegistration = SocialRegistration()
                        socialRegistration.oauthProvider = "facebook"
                        socialRegistration.oauthId = `object`.getString("id")
                        socialRegistration.name = `object`.getString("name")
                        if (`object`.has("email")) socialRegistration.email = `object`.getString("email") else socialRegistration.email = ""
                        socialRegistration.locale = null
                        socialRegistration.link = null
                        socialRegistration.count = 0
                        socialRegistration.picture = null
                        socialRegistration.mobileVersion = Build.VERSION.RELEASE
                        socialRegistration.location = currentLocation
                        socialRegistration.deviceUniqueId = deviceUniqueId
                        socialRegistration.deviceToken = deviceToken
                        socialRegistration.phoneNumbersArray = JSONArray()
                        socialRegistration.emailAddressesArray = JSONArray()
                        doSocialRegistration(socialRegistration)
                    } else {
                        CNProgressDialog.hideProgressDialog()
                        displayToast("Unable to log in into facebook now. Please try after some time..!")
                    }
                } catch (e: java.lang.Exception) {
                    CNProgressDialog.hideProgressDialog()
                    e.printStackTrace()
                }
            }
            val parameters = Bundle()
            parameters.putString("fields", "id,name,email")
            request.parameters = parameters
            request.executeAsync()
        }

        override fun onCancel() {
            CNProgressDialog.hideProgressDialog()
            displayToast("Cancelled facebook login.")
        }

        override fun onError(exception: FacebookException) {
            CNProgressDialog.hideProgressDialog()
            displayToast("An error occurred while login into facebook.")
        }
    }

    private fun initiateGoogleLogin() {
        // startActivity(Intent(this, RegistrationHomeActivity::class.java))

        // Check for existing Google Sign In account, if the user is already signed in
        // the GoogleSignInAccount will be non-null.
        mGoogleSignInClient!!.signOut()
        val account = GoogleSignIn.getLastSignedInAccount(this)

        if (account == null) {
            val signInIntent = mGoogleSignInClient!!.signInIntent
            startActivityForResult(signInIntent, Constants.REQUEST_CODE_FOR_GOOGLE_LOGIN)
        }

    }


    fun handleGoogleSignInResult(completedTask: Task<GoogleSignInAccount?>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)

            // Signed in successfully, show authenticated UI.
            updateUIWithGoogleSignInResponse(account)
        } catch (e: ApiException) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            e.printStackTrace()
            Log.w(LoginActivity.TAG, "signInResult:failed code=" + e.statusCode)
            if (e.statusCode == GoogleSignInStatusCodes.SIGN_IN_CANCELLED) {
                displayLongToast("Google Sign in action cancelled")
            } else {
                displayLongToast("Login with Google account failed.")
            }
        }
    }

    fun updateUIWithGoogleSignInResponse(@Nullable account: GoogleSignInAccount?) {
        if (account != null) {
            mGoogleSignInAccount = account
            mGoogleAccount = account.account
            finishGoogleRegistration()
        }
    }

    private fun finishGoogleRegistration() {
        val socialRegistration = SocialRegistration()
        socialRegistration.oauthProvider = "google"
        socialRegistration.oauthId = mGoogleSignInAccount!!.id
        socialRegistration.name = mGoogleSignInAccount!!.displayName
        if (mGoogleSignInAccount!!.email != null && !mGoogleSignInAccount!!.email!!.isEmpty()) socialRegistration.email = mGoogleSignInAccount!!.email else socialRegistration.email = ""
        socialRegistration.locale = null
        socialRegistration.link = null
        socialRegistration.count = 0
        if (mGoogleSignInAccount!!.photoUrl != null) socialRegistration.picture = mGoogleSignInAccount!!.photoUrl.toString() else socialRegistration.picture = null
        socialRegistration.mobileVersion = Build.VERSION.RELEASE
        socialRegistration.location = currentLocation
        socialRegistration.deviceUniqueId = deviceUniqueId
        socialRegistration.deviceToken = deviceToken
        socialRegistration.phoneNumbersArray = JSONArray()
        socialRegistration.emailAddressesArray = JSONArray()
        doSocialRegistration(socialRegistration)
    }

    fun getLinkedInUserDetails(accessToken: String) {
        val headers = HashMap<String, String>()
        headers["Authorization"] = "Bearer $accessToken"
        headers["Content-Type"] = "application/json"
        val getLinkedInUserDetailsAsyncRequest: CNAsyncRequest = @SuppressLint("StaticFieldLeak")
        object : CNAsyncRequest(activityContext, Constants.GET_LINKED_IN_MEMBER_PROFILE_URL, MethodType.GET_WITH_HEADERS, headers) {
            override fun onResponseReceived(response: String) {
                if (response == "failure" || response == "error" || response == "timeout") {
                    // If we got Unauthorized or Some Other Error response from LinkedIn server then,
                    // We are Refreshing Access Token by initiating LinkedIn LogIn Process again.
                    loginWithLinkedIn()
                } else {
                    try {
                        //Convert the string result to a JSON Object
                        val jsonObject = JSONObject(response)
                        if (jsonObject != null) {
                            var firstName = ""
                            var lastName = ""
                            var profilePictureURL = ""
                            if (jsonObject.has("firstName")) firstName = jsonObject.getJSONObject("firstName").getJSONObject("localized").getString("en_US")
                            if (jsonObject.has("lastName")) lastName = jsonObject.getJSONObject("lastName").getJSONObject("localized").getString("en_US")
                            if (jsonObject.has("profilePicture")) {
                                val profilePictureDisplayImages = jsonObject.getJSONObject("profilePicture").getJSONObject("displayImage~").getJSONArray("elements")
                                if (profilePictureDisplayImages != null && profilePictureDisplayImages.length() > 0) {
                                    for (i in 0 until profilePictureDisplayImages.length()) {
                                        val profilePictureObj = profilePictureDisplayImages.getJSONObject(i)
                                        profilePictureURL = profilePictureObj.getJSONArray("identifiers").getJSONObject(0).getString("identifier")
                                    }
                                }
                            }
                            val socialRegistration = SocialRegistration()
                            socialRegistration.oauthProvider = "linkedin"
                            socialRegistration.oauthId = jsonObject.getString("id")
                            socialRegistration.name = "$firstName $lastName"
                            socialRegistration.email = ""
                            socialRegistration.gender = null
                            socialRegistration.locale = null
                            socialRegistration.link = ""
                            socialRegistration.count = 0
                            socialRegistration.picture = profilePictureURL
                            socialRegistration.mobileVersion = Build.VERSION.RELEASE
                            socialRegistration.location = currentLocation
                            socialRegistration.deviceUniqueId = deviceUniqueId
                            socialRegistration.deviceToken = deviceToken
                            socialRegistration.phoneNumbersArray = JSONArray()
                            socialRegistration.emailAddressesArray = JSONArray()
                            getLinkedInUserEmailAddress(accessToken, socialRegistration)
                        }
                    } catch (e: Exception) {
                        CNProgressDialog.hideProgressDialog()
                        e.printStackTrace()
                    }
                }
            }
        }
        getLinkedInUserDetailsAsyncRequest.execute()
    }

    fun getLinkedInUserEmailAddress(accessToken: String, socialRegistration: SocialRegistration) {
        val headers = HashMap<String, String>()
        headers["Authorization"] = "Bearer $accessToken"
        headers["Content-Type"] = "application/json"
        val getLinkedInUserEmailAddressAsyncRequest: CNAsyncRequest = @SuppressLint("StaticFieldLeak")
        object : CNAsyncRequest(activityContext, Constants.GET_LINKED_IN_MEMBER_EMAIL_ADDRESS_URL, MethodType.GET_WITH_HEADERS, headers) {
            override fun onResponseReceived(response: String) {
                try {
                    //Convert the string result to a JSON Object
                    val jsonObject = JSONObject(response)
                    if (jsonObject != null) {
                        var email = ""
                        if (jsonObject.has("elements") && jsonObject.getJSONArray("elements").length()>0) {
                            val emailObj = jsonObject.getJSONArray("elements").getJSONObject(0)
                            if (emailObj != null) {
                                email = emailObj.getJSONObject("handle~").getString("emailAddress")
                            }
                        }
                        socialRegistration.email = email
                        doSocialRegistration(socialRegistration)
                    }
                } catch (e: java.lang.Exception) {
                    CNProgressDialog.hideProgressDialog()
                    e.printStackTrace()
                }
            }
        }
        getLinkedInUserEmailAddressAsyncRequest.execute()
    }

    fun loginWithLinkedIn() {
        try {
            LISessionManager.getInstance(getApplicationContext()).init(this, buildScope(), object : AuthListener {
                override fun onAuthSuccess() {
                    // Authentication was successful. You can now do other calls with the SDK.
                    Log.d(LoginActivity.TAG, "LinkedIn AccessToken : " + LISessionManager.getInstance(getApplicationContext()).session.accessToken.toString())
                    CNProgressDialog.showProgressDialog(activityContext, Constants.LOADING_MESSAGE)
                    val apiHelper = APIHelper.getInstance(getApplicationContext())
                    apiHelper.getRequest(activityContext, Constants.LINKED_IN_URL, object : ApiListener {
                        override fun onApiSuccess(apiResponse: ApiResponse) {
                            try {
                                val jsonObject = apiResponse.responseDataAsJson
                                if (jsonObject != null) {
                                    val socialRegistration = SocialRegistration()
                                    socialRegistration.oauthProvider = "linkedin"
                                    socialRegistration.oauthId = jsonObject.getString("id")
                                    socialRegistration.name = jsonObject.getString("formattedName")
                                    socialRegistration.email = jsonObject.getString("emailAddress")
                                    socialRegistration.gender = null
                                    socialRegistration.locale = null
                                    socialRegistration.link = jsonObject.getString("publicProfileUrl")
                                    socialRegistration.count = jsonObject.getInt("numConnections")
                                    socialRegistration.picture = jsonObject.getString("pictureUrl")
                                    socialRegistration.mobileVersion = Build.VERSION.RELEASE
                                    socialRegistration.location = currentLocation
                                    socialRegistration.deviceUniqueId = deviceUniqueId
                                    socialRegistration.deviceToken = deviceToken
                                    doSocialRegistration(socialRegistration)
                                }
                            } catch (e: java.lang.Exception) {
                                CNProgressDialog.hideProgressDialog()
                                e.printStackTrace()
                            }
                        }

                        override fun onApiError(liApiError: LIApiError) {
                            CNProgressDialog.hideProgressDialog()

                            // Error making GET request!
                            Log.d(LoginActivity.TAG, "LinkedIn ApiError : $liApiError")
                            displayToast("An error occurred while login into LinkedIn.")
                        }
                    })
                }

                override fun onAuthError(error: LIAuthError) {
                    CNProgressDialog.hideProgressDialog()
                    Log.d(LoginActivity.TAG, "LinkedIn oAuth Error : $error")
                    displayToast("An error occurred while login into LinkedIn.")
                }
            }, true)
        } catch (e: java.lang.Exception) {
            CNProgressDialog.hideProgressDialog()
            e.printStackTrace()
        }
    }

    // Build the list of member permissions our LinkedIn session requires
    private fun buildScope(): Scope? {
        return Scope.build(Scope.R_BASICPROFILE, Scope.R_EMAILADDRESS)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == Constants.REQUEST_CODE_FOR_GOOGLE_LOGIN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleGoogleSignInResult(task)
            return
        }

        if (resultCode == Activity.RESULT_OK) {
            /*if (requestCode == LISessionManager.LI_SDK_AUTH_REQUEST_CODE) {
                LISessionManager.getInstance(getApplicationContext()).onActivityResult(this, requestCode, resultCode, data);
            }*/
            if (requestCode == Constants.REQUEST_CODE_FOR_LINKED_IN_LOGIN) {
                val bundle = data!!.extras
                val linkedInAuthorizationToken = bundle!!.getString(Constants.BUNDLE_LINKED_IN_AUTHORIZATION_CODE)
                if (linkedInAuthorizationToken == null) {
                    val linkedInAuthorizationError = bundle.getString(Constants.BUNDLE_LINKED_IN_ERROR_CODE)
                    val linkedInAuthorizationErrorDescription = bundle.getString(Constants.BUNDLE_LINKED_IN_ERROR_DESCRIPTION)
                    displayLongToast(linkedInAuthorizationErrorDescription)
                } else {
                    getLinkedInAccessToken(linkedInAuthorizationToken)
                }
            } else if (requestCode == Constants.REQUEST_CODE_GET_CURRENT_LOCATION) {
                Log.e(LoginActivity.TAG, "User agreed to make required location settings changes.")
                // Nothing to do. startLocationupdates() gets called in onResume again.
            } else {
                fbCallbackManager!!.onActivityResult(requestCode, resultCode, data)
            }
        } else if (resultCode == Activity.RESULT_CANCELED) {
            if (requestCode == Constants.REQUEST_CODE_FOR_GOOGLE_CONTACTS_RECOVERABLE) {
                Log.e(LoginActivity.TAG, "Get google contacts failed.")
            } else if (requestCode == Constants.REQUEST_CODE_GET_CURRENT_LOCATION) {
                Log.e(LoginActivity.TAG, "User chose not to make required location settings changes.")
                mRequestingLocationUpdates = false
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    fun getLinkedInAccessToken(authorizationToken: String) {
        val params = HashMap<String, String>()
        params["grant_type"] = "authorization_code"
        params["code"] = authorizationToken
        params["redirect_uri"] = Constants.LINKED_IN_REDIRECT_URL
        params["client_id"] = Constants.LINKED_IN_CLIENT_ID
        params["client_secret"] = Constants.LINKED_IN_CLIENT_SECRET
        val getLinkedInAccessTokenAsyncRequest: CNAsyncRequest = @SuppressLint("StaticFieldLeak")
        object : CNAsyncRequest(activityContext, Constants.LINKED_IN_ACCESS_TOKEN_URL, MethodType.POST_WITH_URL_ENCODED, params) {
            override fun onResponseReceived(response: String) {
                try {
                    //Convert the string result to a JSON Object
                    val resultJson = JSONObject(response)

                    //Extract data from JSON Response
                    val expiresIn = if (resultJson.has("expires_in")) resultJson.getInt("expires_in") else 0
                    val accessToken = if (resultJson.has("access_token")) resultJson.getString("access_token") else null
                    Log.i("accessToken", "" + accessToken)
                    if (expiresIn > 0 && accessToken != null) {
                        Log.i("Authorize", "This is the access Token: $accessToken. It will expires in $expiresIn secs")

                        //Calculate date of expiration
                        val calendar = Calendar.getInstance()
                        calendar.add(Calendar.SECOND, expiresIn)
                        val expireDate = calendar.timeInMillis

                        //Store both expires in and access token in shared preferences
                        sharedPreferences.putString(Constants.SP_LINKED_IN_ACCESS_TOKEN, accessToken)
                        sharedPreferences.putLong(Constants.SP_LINKED_IN_ACCESS_TOKEN_EXPIRY_DATE, expireDate)
                        getLinkedInUserDetails(accessToken)
                    }
                } catch (e: java.lang.Exception) {
                    CNProgressDialog.hideProgressDialog()
                    e.printStackTrace()
                }
            }
        }
        getLinkedInAccessTokenAsyncRequest.execute()
    }

    fun doSocialRegistration(socialProfile: SocialRegistration?) {
        socialProfile?.user_id = userDetails.userId
        socialProfile?.mobile_no = userDetails.userMobile
        CNProgressDialog.showProgressDialog(activityContext, Constants.LOADING_MESSAGE)
        val token = userToken
        cnModel.doSocialRegistration(socialProfile,token)
    }

    fun updateSocialProfileResult(response: JSONObject) {
        try {
            CNProgressDialog.hideProgressDialog()
            if (response.getBoolean("status")) {
                //adgydeCounting(getString(R.string.user_registration_in_progress))
                getProfile(userDetails.userId)
            } else {
                showAlertDialog(response.getString("message"))
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    fun adgydeCounting(value: String) {
        val params = HashMap<String, Any>()
        val key = getString(R.string.registration_key)
        params[key] = value //patrametre name,value change to event
        AppsFlyerLib.getInstance().logEvent(this, key, params)

        val logger = AppEventsLogger.newLogger(this)
        val bundle = Bundle()
        bundle.putString(key, value)
        logger.logEvent(getString(R.string.registration_key), bundle)

        /*BranchEvent("UserRegistration")
            .addCustomDataProperty("UserRegistration", "User_Registration")
            .setCustomerEventAlias("User_Registration")
            .logEvent(this@FederalRegistrationActivity)*/
    }
}
