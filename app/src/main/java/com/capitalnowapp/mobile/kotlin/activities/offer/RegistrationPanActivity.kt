package com.capitalnowapp.mobile.kotlin.activities.offer

import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.MailTo
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.text.Editable
import android.text.SpannableString
import android.text.Spanned
import android.text.TextWatcher
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.bumptech.glide.Glide
import com.capitalnowapp.mobile.BuildConfig
import com.capitalnowapp.mobile.R
import com.capitalnowapp.mobile.activities.BaseActivity
import com.capitalnowapp.mobile.constants.Constants
import com.capitalnowapp.mobile.customviews.CNButton
import com.capitalnowapp.mobile.customviews.CNProgressDialog
import com.capitalnowapp.mobile.customviews.CNTextView
import com.capitalnowapp.mobile.databinding.ActivityRegistrationPanBinding
import com.capitalnowapp.mobile.kotlin.activities.CropActivity
import com.capitalnowapp.mobile.kotlin.utils.FileUtils
import com.capitalnowapp.mobile.kotlin.utils.Validator
import com.capitalnowapp.mobile.models.ContactUsResponse
import com.capitalnowapp.mobile.models.offerModel.CSGenericResponse
import com.capitalnowapp.mobile.models.offerModel.ProfileFormDataReq
import com.capitalnowapp.mobile.models.offerModel.ProfileFormDataResponse
import com.capitalnowapp.mobile.models.offerModel.TancText
import com.capitalnowapp.mobile.retrofit.GenericAPIService
import com.capitalnowapp.mobile.util.RealPathUtil
import com.capitalnowapp.mobile.util.Utility
import com.google.gson.Gson
import com.theartofdev.edmodo.cropper.CropImage
import kotlinx.android.synthetic.main.activity_registration_pan.*
import java.io.*
import java.lang.ref.WeakReference
import java.text.SimpleDateFormat
import java.util.*

class RegistrationPanActivity : BaseActivity() {
    companion object {
        private const val CAMERA_PERMISSION_CODE = 100
    }

    private var binding: ActivityRegistrationPanBinding? = null
    private var panNumber: String? = ""
    private var pageNumber: Int? = 18
    private var panImage: Uri? = null
    private var activity: AppCompatActivity? = null
    private val REQUEST_IMAGE_CAPTURE = 102
    private var currentPath = ""
    private var selectedFile: String = ""
    private lateinit var ivPanImg: ImageView
    private lateinit var ivPan: ImageView
    private lateinit var ivFrame: FrameLayout
    private var cropGooglePhotosUri: Boolean? = false
    private var cropFileName: String? = null
    private var profileFormDataResponse = ProfileFormDataResponse()
    private var genericResponse: ContactUsResponse = ContactUsResponse()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegistrationPanBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        activity = this
        initView()
    }

    private fun initView() {
        profileFormData()
        binding?.etPan?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                if (s?.length == 10 && Validator.isValidPanNumber(s.toString())) {
                    panNumber = s.toString()
                    //uploadCsPan()
                }
                binding?.etPan?.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
            }
        })
        binding?.ivPan?.setOnClickListener {
            try {
                checkPermission(
                    Manifest.permission.CAMERA,
                    CAMERA_PERMISSION_CODE
                )
                selectImage()
                panImage = null
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        binding?.tvNext?.setOnClickListener {
            validatePan()
        }
        binding?.ivBack?.setOnClickListener {
            val intent = Intent(this, RegistrationForm3Activity::class.java)
            startActivity(intent)
        }
        binding?.ivHelp?.setOnClickListener {
            showHelpPopup()
        }
        binding?.ivCancel?.setOnClickListener {
            binding?.flFrame?.visibility = View.GONE
            binding?.ivPan?.visibility = View.VISIBLE
        }
    }

    private fun showHelpPopup() {
        val alertDialog = Dialog(this)
        alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        alertDialog.setContentView(R.layout.cn_help_dialog)
        alertDialog.window!!.setLayout(
            FrameLayout.LayoutParams.MATCH_PARENT,
            FrameLayout.LayoutParams.WRAP_CONTENT
        )
        alertDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        alertDialog.setCanceledOnTouchOutside(true)
        val tvEmailText = alertDialog.findViewById<TextView>(R.id.tvEmailText)
        val tvCall = alertDialog.findViewById<TextView>(R.id.tvCall)
        val ivCancel = alertDialog.findViewById<ImageView>(R.id.ivCancel)
        tvEmailText.text = profileFormDataResponse.offerHelp?.email.toString()
        tvCall.text = profileFormDataResponse.offerHelp?.phone.toString()
        tvEmailText.setOnClickListener {
            alertDialog.dismiss()
            composeEmail()
        }
        tvCall.setOnClickListener {
            callToNum()
            alertDialog.dismiss()
        }
        ivCancel.setOnClickListener {
            alertDialog.dismiss()
        }
        alertDialog.show()
    }

    private fun callToNum() {
        try {
            if (genericResponse.phone != "") {
                val num = genericResponse.phone
                val intent = Intent(Intent.ACTION_DIAL)
                intent.data = Uri.parse("tel:$num")
                startActivity(intent)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun composeEmail() {
        try {
            val emailIntent = Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                "mailto", genericResponse.email, null))
            startActivity(Intent.createChooser(emailIntent, "Choose to send email..."))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun validatePan() {
        try {
            panNumber = binding?.etPan?.text.toString().trim { it <= ' ' }
            if (etPan.length() < 10) {
                Toast.makeText(this, "Enter Valid PAN Card Number", Toast.LENGTH_LONG).show()
            } else if (selectedFile.isEmpty()) {
                Toast.makeText(this, "Pan Image is required and can't be empty", Toast.LENGTH_LONG)
                    .show()
                
            } else {
                if(binding?.cbAgreeTerms?.isChecked!! && binding?.cbAgreeTerms1?.isChecked!!) {
                    uploadCsPan()
                }else {
                    Toast.makeText(this, "Please Check the Consent", Toast.LENGTH_SHORT).show()
                }
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setData() {
        try {
            if(profileFormDataResponse.offerHelp?.icon !=null){
                Glide.with(this).load(profileFormDataResponse.offerHelp!!.icon)
                    .into(binding?.ivHelp!!)
            }
            binding?.etPan?.setText(profileFormDataResponse.profileformData?.pPanCardNo)

            var kfsLinkData = profileFormDataResponse.profileformData?.tancText?.get(0)
            var kfsLinkData1 = profileFormDataResponse.profileformData?.tancText?.get(1)
            setBorrowTerms(kfsLinkData,0)
            setBorrowTerms(kfsLinkData1,1)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setBorrowTerms(kfsLinkData: TancText?, i: Int) {
        try {

            var startIndex: Int
            var endIndex: Int
            val ss: SpannableString?
            if (kfsLinkData?.message != null) {
                val words: List<String> = kfsLinkData.findText!!.split("||")
                val links: List<String> = kfsLinkData.replaceLinks!!.split("||")
                ss = SpannableString(kfsLinkData.message)
                for (w in words.withIndex()) {
                    val termsAndCondition: ClickableSpan = object : ClickableSpan() {
                        override fun onClick(textView: View) {
                            var url = links[w.index]
                            showTermsPolicyDialog(w.value, url)

                        }
                    }
                    startIndex = ss.indexOf(w.value, 0)
                    endIndex = startIndex + w.value.length
                    ss.setSpan(
                        termsAndCondition,
                        startIndex,
                        endIndex,
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                    )

                    if(i==0) {
                        binding?.tvPanConfirm?.text = ss
                        binding?.tvPanConfirm?.movementMethod = LinkMovementMethod.getInstance()
                    }
                    else {
                        binding?.tvPanConfirm1?.text = ss
                        binding?.tvPanConfirm1?.movementMethod = LinkMovementMethod.getInstance()
                    }
                }
            }

        } catch (e: Exception){
            e.printStackTrace()
        }
    }

    private fun showTermsPolicyDialog(title: String, link: String) {
        val alert = AlertDialog.Builder(
            (activity as BaseActivity).activityContext,
            R.style.RulesAlertDialogStyle
        )
        val inflater: LayoutInflater = this@RegistrationPanActivity.layoutInflater
        val dialogView: View = inflater.inflate(R.layout.cs_dialog_terms_conditions, null)
        alert.setView(dialogView)
        val tvTitle: CNTextView = dialogView.findViewById(R.id.et_title)
        tvTitle.text = title
        val pb = dialogView.findViewById<ProgressBar>(R.id.pb)
        val webView = dialogView.findViewById<WebView>(R.id.webView)
        webView.settings.javaScriptEnabled = true
        val dialog: Dialog = alert.create()
        val mActivityRef = WeakReference<Activity>(activity)
        webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                if (url.startsWith("mailto:")) {
                    val activity: Activity = mActivityRef.get()!!
                    val mt = MailTo.parse(url)
                    val intent = Intent(Intent.ACTION_SEND)
                    intent.putExtra(Intent.EXTRA_EMAIL, arrayOf<String>(mt.to))
                    intent.putExtra(Intent.EXTRA_TEXT, mt.body)
                    intent.putExtra(Intent.EXTRA_SUBJECT, mt.subject)
                    intent.putExtra(Intent.EXTRA_CC, mt.cc)
                    intent.type = "message/rfc822"
                    activity.startActivity(intent)
                    view.reload()
                    return true
                }else if(url.contains("https://www.capitalnow.in/clsoeapp")) {
                    webView.visibility = View.GONE
                } else {
                    view.loadUrl(url)
                }
                return true
            }

            override fun onPageStarted(view: WebView, url: String, favicon: Bitmap?) {
                pb.visibility = View.VISIBLE
                view.visibility = View.GONE
                super.onPageStarted(view, url, favicon)
            }

            override fun onPageFinished(view: WebView, url: String) {
                pb.visibility = View.GONE
                view.visibility = View.VISIBLE
                super.onPageFinished(view, url)
            }
        }
        webView.loadUrl(link)
        dialog.show()
    }

    private fun profileFormData() {
        try {
            CNProgressDialog.showProgressDialog(this, Constants.LOADING_MESSAGE)
            val genericAPIService = GenericAPIService(this, 0)
            val profileFormDataReq = ProfileFormDataReq()
            profileFormDataReq.pageNo = 18
            val token = userToken
            genericAPIService.profileFormData(profileFormDataReq, token)
            genericAPIService.setOnDataListener { responseBody ->
                CNProgressDialog.hideProgressDialog()
                profileFormDataResponse =
                    Gson().fromJson(responseBody, ProfileFormDataResponse::class.java)
                if (profileFormDataResponse.status == true) {
                        setData()
                }
                genericAPIService.setOnErrorListener {
                    fun errorData(throwable: Throwable?) {
                        //Failure
                        CNProgressDialog.hideProgressDialog()
                    }
                }
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun checkPermission(permission: String, requestCode: Int) {
        if (ContextCompat.checkSelfPermission(
                this@RegistrationPanActivity,
                permission
            ) == PackageManager.PERMISSION_DENIED
        ) {

            // Requesting the permission
            ActivityCompat.requestPermissions(
                this@RegistrationPanActivity,
                arrayOf(permission),
                requestCode
            )
        } else {
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            } else {

            }
        }
    }

    private fun selectImage() {
        try {
            val alertDialog = Dialog(this)
            alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            alertDialog.setContentView(R.layout.selectimage_dialog)
            alertDialog.window!!.setLayout(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
            )
            alertDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            alertDialog.setCanceledOnTouchOutside(true)
            val takePhoto = alertDialog.findViewById<CNButton>(R.id.btnTakePhoto)
            val gallery = alertDialog.findViewById<CNButton>(R.id.btnGallery)
            val cancel = alertDialog.findViewById<ImageView>(R.id.ivCancel)
            takePhoto.setOnClickListener {
                val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                if (takePictureIntent.resolveActivity((activity as RegistrationPanActivity).packageManager) != null) {
                    // Create the File where the photo should go
                    var photoFile: File? = null
                    try {
                        photoFile = createImageFile()
                        panImage = Uri.fromFile(photoFile)
                    } catch (ex: IOException) {
                        // Error occurred while creating the File
                    }
                    try {
                        // Continue only if the File was successfully created
                        if (photoFile != null) {
                            val photoURI: Uri = FileProvider.getUriForFile(
                                (activity as RegistrationPanActivity),
                                BuildConfig.APPLICATION_ID + ".fileprovider",
                                photoFile
                            )
                            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
                            alertDialog.dismiss()
                        }
                    } catch (e: java.lang.Exception) {
                        e.printStackTrace()
                    }
                }
            }
            gallery.setOnClickListener {
                chooseFile()
                alertDialog.dismiss()
            }
            cancel.setOnClickListener {
                alertDialog.dismiss()
            }
            alertDialog.show()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @Throws(IOException::class)
    open fun createImageFile(): File? {
        // Create an image file name
        val timeStamp: String =
            SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val imageFileName = "JPEG_temp$timeStamp"
        val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val image = File.createTempFile(
            imageFileName,  /* prefix */
            ".jpg",  /* suffix */
            storageDir /* directory */
        )
        if (image.absolutePath != "") {
            currentPath = image.absolutePath
        }
        // Save a file: path for use with ACTION_VIEW intents
        return image
    }

    private fun chooseFile() {
            openGallery()
    }

    private fun openGallery() {
        val intent =
            getImageChooserIntent()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            startActivityForResult(intent, Constants.REQUEST_CODE_CHOOSE_DOCUMENT_TO_UPLOAD)
        } else {
            if (intent.resolveActivity(activity?.packageManager!!) != null) {
                startActivityForResult(intent, Constants.REQUEST_CODE_CHOOSE_DOCUMENT_TO_UPLOAD)
            } else {
                //displayToast(resources.getString(R.string.no_support_for_storage))
                Toast.makeText(this, R.string.no_support_for_storage, Toast.LENGTH_LONG).show()

            }
        }
    }

    private fun getImageChooserIntent(): Intent {
        val mimeTypes = arrayOf("image/*")
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION)
        //intent.setType(mimeTypes.length == 1 ? mimeTypes[0] : "*/*");
        intent.type = "image/*|application/pdf"
        if (mimeTypes.isNotEmpty()) {
            intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
        }
        return intent
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        //retrieve scan result
        super.onActivityResult(requestCode, resultCode, intent)
        try {
            if (resultCode == RESULT_OK && requestCode == Constants.REQUEST_CODE_CHOOSE_DOCUMENT_TO_UPLOAD) {
                try {
                    val selectedDocumentUri: Uri = intent!!.data!!
                    selectedDocumentUri.let { activity?.contentResolver?.getType(it) }
                    val fileType = Utility.getMimeType(this, selectedDocumentUri)

                    if (fileType.equals("JPG", ignoreCase = true) || fileType.equals(
                            "JPEG",
                            ignoreCase = true
                        ) || fileType.equals("PNG", ignoreCase = true) || fileType.equals(
                            "PDF",
                            ignoreCase = true
                        )
                    ) {
                        var isGooglePhotosUri = false
                        var selectedFilePath: String? = null
                        if (isGoogleDriveUri(selectedDocumentUri)) {
                            try {
                                val inputStream: InputStream? =
                                    activity?.contentResolver?.openInputStream(selectedDocumentUri)
                                if (inputStream != null) {
                                    selectedFilePath =
                                        RealPathUtil.getPathFromUri(this, selectedDocumentUri)
                                    isGooglePhotosUri = true
                                }
                            } catch (e: FileNotFoundException) {
                                e.printStackTrace()
                            }
                        } else {
                            selectedFilePath =
                                RealPathUtil.getPathFromUri(this, selectedDocumentUri)
                            if (selectedFilePath == null || selectedFilePath == "" || (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R)) {
                                selectedFilePath = FileUtils.makeFileCopyInCacheDir(
                                    selectedDocumentUri,
                                    (activity as RegistrationPanActivity)
                                )
                            }
                            /*selectedFilePath = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                                makeFileCopyInCacheDir(selectedDocumentUri)
                            } else {
                                RealPathUtil.getPathFromUri(context, selectedDocumentUri)
                            }*/
                        }
                        val selectedFile = File(selectedFilePath)

                        val selectedFileSizeInMB = selectedFile.length() / (1024 * 1024)
                        if (selectedFileSizeInMB > Constants.FILE_UPLOAD_LIMIT) {

                            Toast.makeText(this, "Uploading file size must be less than %d MB.", Toast.LENGTH_LONG).show()

                            return
                        } else {
                            selectedFilePath?.let {
                                showCrop(
                                    it,
                                    isGooglePhotosUri,
                                    selectedFilePath,
                                    selectedDocumentUri
                                )
                            }
                        }

                    } else {
                        //displayToast(getString(R.string.upload_docs_format_validation_msg))

                        Toast.makeText(this, R.string.upload_docs_format_validation_msg, Toast.LENGTH_LONG).show()

                    }
                } catch (e: java.lang.Exception) {
                    e.printStackTrace()
                }
            } else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
                try {
                    val result = intent?.getStringExtra("crop")
                    if (resultCode == RESULT_OK) {
                        //uploadFileWithProgress(result!!, cropGooglePhotosUri!!, cropFileName!!)
                        selectedFile = File(result).absolutePath
                        panImage = Uri.parse(File(result).absolutePath)
                        binding?.ivPanImg?.setImageURI(panImage)
                        binding?.flFrame?.visibility = View.VISIBLE
                        binding?.ivPan?.visibility = View.GONE
                        binding?.ivPanImg?.visibility = View.VISIBLE
                        //uploadCsPan()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            } else if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
                if (panImage != null) {
                    val selectedFile = File(panImage!!.path.toString())
                    val reducedFile = saveBitmapToFile(selectedFile)
                    val selectedFilePath = reducedFile!!.path
                    val selectedFileSizeInMB = reducedFile.length() / (1024 * 1024)
                    if (selectedFileSizeInMB > Constants.FILE_UPLOAD_LIMIT) {
                        Toast.makeText(this, "Uploading file size must be less than %d MB.", Toast.LENGTH_LONG).show()

                        return
                    } else {
                        selectedFilePath.let {
                            showCrop(it, false, selectedFilePath, panImage!!)
                        }
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun saveBitmapToFile(file: File): File? {
        return try {

            // BitmapFactory options to downsize the image
            val o = BitmapFactory.Options()
            o.inJustDecodeBounds = true
            o.inSampleSize = 6
            // factor of downsizing the image
            var inputStream = FileInputStream(file)
            //Bitmap selectedBitmap = null;
            BitmapFactory.decodeStream(inputStream, null, o)
            inputStream.close()

            // The new size we want to scale to
            val REQUIRED_SIZE = 75

            // Find the correct scale value. It should be the power of 2.
            var scale = 1
            while (o.outWidth / scale / 2 >= REQUIRED_SIZE &&
                o.outHeight / scale / 2 >= REQUIRED_SIZE
            ) {
                scale *= 2
            }
            val o2 = BitmapFactory.Options()
            o2.inSampleSize = scale
            inputStream = FileInputStream(file)
            val selectedBitmap = BitmapFactory.decodeStream(inputStream, null, o2)
            inputStream.close()

            // here i override the original image file
            file.createNewFile()
            val outputStream = FileOutputStream(file)
            selectedBitmap!!.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
            file
        } catch (e: java.lang.Exception) {
            null
        }
    }

    private fun isGoogleDriveUri(uri: Uri): Boolean {
        return "com.google.android.apps.docs.storage" == uri.authority || "com.google.android.apps.docs.storage.legacy" == uri.authority
    }

    private fun showCrop(
        selectedFile: String,
        googlePhotosUri: Boolean,
        fileName: String,
        selectedDocumentUri: Uri
    ) {
        try {
            cropGooglePhotosUri = googlePhotosUri
            cropFileName = fileName

            val intent = Intent(activity, CropActivity::class.java)
            intent.putExtra("selectedImage", selectedDocumentUri)
            intent.putExtra("isPanPicture", true)
            startActivityForResult(intent, CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    private fun uploadCsPan() {
        try {
            CNProgressDialog.showProgressDialog(this, Constants.LOADING_MESSAGE)
            val genericAPIService = GenericAPIService(this, 0)
            val token = userToken
            genericAPIService.uploadCsPanImage(selectedFile, panNumber, pageNumber, token)
            genericAPIService.setOnDataListener { responseBody ->
                CNProgressDialog.hideProgressDialog()
               val csGenericResponse =
                    Gson().fromJson(responseBody, CSGenericResponse::class.java)
                if (csGenericResponse.status == true) {
                    getApplyLoanDataBase(true)
                } else {
                    Toast.makeText(this, csGenericResponse.message, Toast.LENGTH_SHORT).show()
                }
            }
            genericAPIService.setOnErrorListener {

            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

}

