package com.capitalnowapp.mobile.kotlin.activities

//import io.branch.referral.util.BranchEvent
import android.Manifest
import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.Window
import android.widget.CheckBox
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import com.capitalnowapp.mobile.BuildConfig
import com.capitalnowapp.mobile.R
import com.capitalnowapp.mobile.activities.BaseActivity
import com.capitalnowapp.mobile.constants.Constants
import com.capitalnowapp.mobile.customviews.CNAlertDialog
import com.capitalnowapp.mobile.customviews.CNButton
import com.capitalnowapp.mobile.customviews.CNProgressDialog
import com.capitalnowapp.mobile.customviews.PanProgressDialog
import com.capitalnowapp.mobile.interfaces.AlertDialogSelectionListener
import com.capitalnowapp.mobile.kotlin.utils.FileUtils
import com.capitalnowapp.mobile.kotlin.utils.Validator
import com.capitalnowapp.mobile.models.FileUploadResponse
import com.capitalnowapp.mobile.models.GetCibilReq
import com.capitalnowapp.mobile.models.GetCibilResponse
import com.capitalnowapp.mobile.models.SkipPanData
import com.capitalnowapp.mobile.retrofit.GenericAPIService
import com.capitalnowapp.mobile.util.RealPathUtil
import com.capitalnowapp.mobile.util.TrackingUtil
import com.capitalnowapp.mobile.util.Utility
import com.google.android.material.textfield.TextInputEditText
import com.google.gson.Gson
import com.theartofdev.edmodo.cropper.CropImage
import org.json.JSONException
import org.json.JSONObject
import java.io.*
import java.text.SimpleDateFormat
import java.util.*


open class PanVerificationActivity : BaseActivity() {
    companion object {
        private const val CAMERA_PERMISSION_CODE = 100
    }
    private var tempFile: File? = null
    private var dialog: AlertDialog? = null
    private var selectedFile: String = ""
    private lateinit var ivPan: ImageView
    private lateinit var tvStatus: TextView
    private lateinit var tvBack: TextView
    private lateinit var ivPanImg: ImageView
    private lateinit var ivCancel: ImageView
    private lateinit var ivFrame: FrameLayout
    private lateinit var etPan: TextInputEditText
    private lateinit var tvValidate: TextView
    private lateinit var tvSkip: TextView
    private var activity: AppCompatActivity? = null
    private val REQUEST_IMAGE_CAPTURE = 102
    private var currentPath = ""
    private var cropGooglePhotosUri: Boolean? = false
    private var cropFileName: String? = null
    private var panImage: Uri? = null
    private var panNumber: String? = ""
    private var isPanUploaded = false
    private var isPanNumFailed = false
    private var isPanImgFailed = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pan_verification)

        val obj = JSONObject()
        try {
            obj.put("cnid",userDetails.qcId)
        } catch (e: JSONException) {
            throw RuntimeException(e)
        }
        TrackingUtil.pushEvent(obj, getString(R.string.pan_verification_page_landed))

        activity = this
        userId = userDetails.userId
        etPan = findViewById(R.id.etPan)
        etPan.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                if (s?.length == 10 && Validator.isValidPanNumber(s.toString())) {
                    panNumber = s.toString()

                    val obj = JSONObject()
                    try {
                        obj.put("cnid",userDetails.qcId)
                    } catch (e: JSONException) {
                        throw RuntimeException(e)
                    }
                    TrackingUtil.pushEvent(obj, getString(R.string.pan_card_number_entered))

                    uploadPan()

                } else if (s?.length!! > 0) {
                    panNumber = ""
                    ivPan.isEnabled = false
                } else {
                    ivPan.isEnabled = true
                    panNumber = ""
                }
                etPan.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
            }
        })
        tvSkip = findViewById(R.id.tvSkip)
        tvSkip.setOnClickListener {
            skipPanData()
        }

        ivPan = findViewById(R.id.ivPan)
        ivPanImg = findViewById(R.id.ivPanImg)
        ivCancel = findViewById(R.id.ivCancel)
        ivFrame = findViewById(R.id.ivFrame)
        tvStatus = findViewById(R.id.tvStatus)
        tvBack = findViewById(R.id.tvBack)
        ivPan.setOnClickListener {

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

            etPan.isEnabled = false
        }

        tvBack.setOnClickListener {
            val intent = Intent(this, DashboardActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or
                    Intent.FLAG_ACTIVITY_CLEAR_TASK or
                    Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
        }

        ivCancel.setOnClickListener {
            ivPan.visibility = VISIBLE
            ivFrame.visibility = GONE
            panImage = null
            etPan.isEnabled = true
        }
        tvValidate = findViewById(R.id.tvValidate)
        val cbAgreeTerms: CheckBox = findViewById(R.id.cbAgreeTerms)
        tvValidate.setOnClickListener {
            val obj = JSONObject()
            try {
                obj.put("cnid",userDetails.qcId)
                obj.put(getString(R.string.interaction_type),"SUBMIT Button Clicked")
            } catch (e: JSONException) {
                throw RuntimeException(e)
            }
            TrackingUtil.pushEvent(obj, getString(R.string.pan_verification_page_interacted))

            if ((isPanUploaded && cbAgreeTerms.isChecked) || (etPan.length() == 10 && Validator.isValidPanNumber(panNumber.toString()) && cbAgreeTerms.isChecked)) {
                getCibilScore()
            } else {
                if (etPan.length() < 10 || !Validator.isValidPanNumber(panNumber.toString())) {
                    displayToast("Enter Valid PAN Card Number")
                } else {
                    displayToast("Please Check the consent")
                }
            }
        }
    }

    private fun checkPermission(permission: String, requestCode: Int) {
        if (ContextCompat.checkSelfPermission(this@PanVerificationActivity, permission) == PackageManager.PERMISSION_DENIED) {

            // Requesting the permission
            ActivityCompat.requestPermissions(this@PanVerificationActivity, arrayOf(permission), requestCode)
        } else {
        }
    }

    // This function is called when the user accepts or decline the permission.
    // Request Code is used to check which permission called this function.
    // This request code is provided when the user is prompt for permission.
    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>,
                                            grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            } else {

            }
        }
    }

    private fun getCibilScore() {
        PanProgressDialog.showProgressDialog(this, Constants.LOADING_MESSAGE)
        val genericAPIService = GenericAPIService(this,0)
        val getCibilReq = GetCibilReq()
        getCibilReq.userId = userId
        val token = userToken
        genericAPIService.getCibilScore(getCibilReq, token)
        genericAPIService.setOnDataListener { responseBody ->
            PanProgressDialog.hideProgressDialog()
            val getCibilResponse = Gson().fromJson(
                    responseBody,
                    GetCibilResponse::class.java
            )
            if (getCibilResponse != null && getCibilResponse.status == true) {
                val obj = JSONObject()
                try {
                    obj.put("cnid",userDetails.qcId)
                    obj.put("isSuccess","true")
                } catch (e: JSONException) {
                    throw RuntimeException(e)
                }
                TrackingUtil.pushEvent(obj, getString(R.string.pan_approval_server_event))
                if (getCibilResponse.showPopup == 1) {
                    if (getCibilResponse.userStatusId == 12) {
                        tvValidate.isEnabled = false
                        showSuccess(true, getCibilResponse)
                    } else {
                        //showSuccess(true, getCibilResponse)
                        //getProfile(getCibilResponse.userId)
                        showSuccessLogout(getCibilResponse.message!!)
                    }
                } else {
                    showSuccess(true, getCibilResponse)
                }
                if(tempFile != null) {
                    tempFile?.delete()
                }
            } else {
                CNAlertDialog.showAlertDialog(
                    activity,
                    resources.getString(R.string.title_alert),
                    getCibilResponse.message
                )
                tvSkip.visibility = View.GONE
            }
        }
        genericAPIService.setOnErrorListener {
            fun errorData(throwable: Throwable?) {
                showSuccess(false, GetCibilResponse())
                PanProgressDialog.hideProgressDialog()
            }
        }
    }

    private fun showSuccess(success: Boolean, genericResponse: GetCibilResponse) {

        if (success) {
            CNAlertDialog.showStatusWithCallback(
                    this,
                    resources.getString(R.string.pan_success_alert),
                    getString(R.string.success_continue),
                    R.drawable.success_new, R.color.Primary2
            )
            CNAlertDialog.setRequestCode(1)

        } else {
            CNAlertDialog.showStatusWithCallback(
                    this,
                    genericResponse.message,
                    "OK",
                    R.drawable.failure_new, R.color.cb_errorRed
            )
            CNAlertDialog.setRequestCode(1)
        }

        CNAlertDialog.setListener(object : AlertDialogSelectionListener {
            override fun alertDialogCallback() {

            }

            override fun alertDialogCallback(buttonType: Constants.ButtonType, requestCode: Int) {
                if (buttonType == Constants.ButtonType.POSITIVE) {
                    if (success) {
                        startActivity(Intent(this@PanVerificationActivity, DashboardActivity::class.java))
                        CNAlertDialog.dismiss()
                        /*val intent = Intent (this, UploadBankDetailsActivity::class.java)
                         startActivity(intent)*/
                        finishAffinity()
                    } else {
                        CNAlertDialog.dismiss()
                    }
                }
            }
        })
    }

    private fun showSuccessLogout(message: String) {
        CNAlertDialog.showAlertDialogWithCallback(
                activity,
                "",
                message, false, "", "")

        CNAlertDialog.setRequestCode(1)

        CNAlertDialog.setListener(object : AlertDialogSelectionListener {
            override fun alertDialogCallback() {

            }

            override fun alertDialogCallback(buttonType: Constants.ButtonType, requestCode: Int) {
                if (buttonType == Constants.ButtonType.POSITIVE) {
                   // logout()
                    CNAlertDialog.dismiss()

                    startActivity(Intent(this@PanVerificationActivity, DashboardActivity::class.java))
                    CNAlertDialog.dismiss()
                    /*val intent = Intent (this, UploadBankDetailsActivity::class.java)
                     startActivity(intent)*/
                    finishAffinity()
                }
            }
        })
    }

    private fun skipPanData() {
        val genericAPIService = GenericAPIService(this,0)
        val skipPanData = SkipPanData()
        skipPanData.panSkip = "Yes"
        skipPanData.userId = userId
        val token = userToken
        genericAPIService.skipPanData(skipPanData, token)
        genericAPIService.setOnDataListener { responseBody ->
            val getCibilResponse = Gson().fromJson(
                    responseBody,
                    GetCibilResponse::class.java
            )
            if (getCibilResponse != null && getCibilResponse.status == true) {
                if (getCibilResponse.showPopup == 1) {
                    if (getCibilResponse.userStatusId == 12) {
                        val intent = Intent(this, DashboardActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or
                                Intent.FLAG_ACTIVITY_CLEAR_TASK or
                                Intent.FLAG_ACTIVITY_NEW_TASK
                        startActivity(intent)
                    } else {
                        //showSuccess(true, getCibilResponse)
                        //getProfile(getCibilResponse.userId)
                        showSuccessLogout(getCibilResponse.message!!)
                    }
                } else {
                    showSuccess(true, getCibilResponse)
                }
            } else {
                displayToast(getCibilResponse.message)
            }
        }
        genericAPIService.setOnErrorListener {
            fun errorData(throwable: Throwable?) {
            }
        }
    }

    private fun selectImage() {
        try {
            val alertDialog = Dialog(activityContext)
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
                val obj = JSONObject()
                try {
                    obj.put("cnid",userDetails.qcId)
                    obj.put(getString(R.string.interaction_type),"Open Camera Clicked")
                } catch (e: JSONException) {
                    throw RuntimeException(e)
                }
                TrackingUtil.pushEvent(obj, getString(R.string.pan_upload_clicked))

                val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                if (takePictureIntent.resolveActivity((activity as PanVerificationActivity).packageManager) != null) {
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
                                    (activity as PanVerificationActivity),
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
                val obj = JSONObject()
                try {
                    obj.put("cnid",userDetails.qcId)
                    obj.put(getString(R.string.interaction_type),"Open Gallery Clicked")
                } catch (e: JSONException) {
                    throw RuntimeException(e)
                }
                TrackingUtil.pushEvent(obj, getString(R.string.pan_upload_clicked))
                chooseFile()
                alertDialog.dismiss()
            }
            cancel.setOnClickListener {
                alertDialog.dismiss()
                etPan.isEnabled = true
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

    private fun chooseFile() {
        openDocumentPicker()
    }

    private fun openDocumentPicker() {
        val mimeTypes = arrayOf("image/*", "application/pdf")
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        //intent.setType(mimeTypes.length == 1 ? mimeTypes[0] : "*/*");
        intent.type = "image/*|application/pdf"
        if (mimeTypes.isNotEmpty()) {
            intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
        }
        startActivityForResult(intent, Constants.OPEN_DOCUMENT_REQUEST_CODE)
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
                displayToast(resources.getString(R.string.no_support_for_storage))
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

    private fun isGoogleDriveUri(uri: Uri): Boolean {
        return "com.google.android.apps.docs.storage" == uri.authority || "com.google.android.apps.docs.storage.legacy" == uri.authority
    }


    @RequiresApi(Build.VERSION_CODES.O)
    fun createFileFromContentUri(fileUri : Uri) : File{

        var fileName : String = ""

        fileUri.let { returnUri ->
            this.contentResolver.query(returnUri,null,null,null)
        }?.use { cursor ->
            val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            cursor.moveToFirst()
            fileName = cursor.getString(nameIndex)
        }

        //  For extract file mimeType
        val fileType: String? = fileUri.let { returnUri ->
            this.contentResolver.getType(returnUri)
        }

        val iStream : InputStream = this.contentResolver.openInputStream(fileUri)!!
        val outputDir : File = this.cacheDir!!
        val outputFile : File = File(outputDir,fileName)
        copyStreamToFile(iStream, outputFile)
        iStream.close()
        return  outputFile
    }

    fun copyStreamToFile(inputStream: InputStream, outputFile: File) {
        inputStream.use { input ->
            val outputStream = FileOutputStream(outputFile)
            outputStream.use { output ->
                val buffer = ByteArray(4 * 1024) // buffer size
                while (true) {
                    val byteCount = input.read(buffer)
                    if (byteCount < 0) break
                    output.write(buffer, 0, byteCount)
                }
                output.flush()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        //retrieve scan result
        super.onActivityResult(requestCode, resultCode, intent)
        try {
            if (resultCode == RESULT_OK && requestCode == Constants.OPEN_DOCUMENT_REQUEST_CODE) {
                try {
                    intent?.data?.also { documentUri ->
                        (activity as BaseActivity).contentResolver.takePersistableUriPermission(
                            documentUri,
                            Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                        )
                        //openDocument(documentUri)
                        tempFile = createFileFromContentUri(documentUri)
                        val filePath = tempFile!!.path
                        val fileUri = tempFile!!.toUri()
                        val selectedDocumentUri: Uri = fileUri
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
                                        activity?.contentResolver?.openInputStream(
                                            selectedDocumentUri
                                        )
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
                                        (activity as PanVerificationActivity)
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
                                displayToast(
                                    String.format(
                                        "Uploading file size must be less than %d MB.",
                                        Constants.FILE_UPLOAD_LIMIT
                                    )
                                )
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
                            displayToast(getString(R.string.upload_docs_format_validation_msg))
                        }
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
                        ivPanImg.setImageURI(panImage)
                        ivPan.visibility = GONE
                        ivFrame.visibility = VISIBLE
                        uploadPan()
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
                        displayToast(
                                String.format(
                                        "Uploading file size must be less than %d MB.",
                                        Constants.FILE_UPLOAD_LIMIT
                                )
                        )
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

    private fun uploadPan() {
        try {
            CNProgressDialog.showProgressDialog(this, Constants.LOADING_MESSAGE)
            val genericAPIService = GenericAPIService(this,0)
            genericAPIService.uploadPanImage(selectedFile, userId, panNumber, userToken)
            genericAPIService.setOnDataListener { responseBody ->
                CNProgressDialog.hideProgressDialog()
                val fileUploadResponse =
                        Gson().fromJson(responseBody, FileUploadResponse::class.java)
                //fileUploadResponse.status = false     //Fail senario
                if (fileUploadResponse != null && fileUploadResponse.status) {
                    isPanUploaded = true
                    if (panNumber?.length!! < 10) {
                        etPan.isEnabled = false
                        ivPanImg.isEnabled = false
                        ivPan.isEnabled = false
                        ivFrame.isEnabled = false
                        ivCancel.isEnabled = false
                        tvStatus.visibility = VISIBLE
                        tvStatus.text = getString(R.string.verify)
                        tvStatus.setCompoundDrawablesWithIntrinsicBounds(
                                null,
                                null,
                                ContextCompat.getDrawable(this, R.drawable.ic_success),
                                null
                        )
                    } else {
                        etPan.setCompoundDrawablesWithIntrinsicBounds(
                                null,
                                null,
                                ContextCompat.getDrawable(this, R.drawable.ic_success),
                                null
                        )
                    }
                } else {
                    isPanUploaded = false
                    if (panNumber?.length!! <= 10) {
                        isPanImgFailed = true
                        etPan.isEnabled = true
                        ivPan.isEnabled = true
                        ivFrame.visibility = GONE
                        tvStatus.visibility = VISIBLE
                        tvStatus.text = getString(R.string.failed)
                        ivPan.visibility = VISIBLE
                        tvSkip.visibility = GONE
                        tvStatus.setCompoundDrawablesWithIntrinsicBounds(
                                null,
                                null,
                                ContextCompat.getDrawable(this, R.drawable.ic_failure),
                                null
                        )



                    } else {
                        isPanNumFailed = true
                        etPan.setCompoundDrawablesWithIntrinsicBounds(
                                null,
                                null,
                                ContextCompat.getDrawable(this, R.drawable.ic_failure),
                                null
                        )

                    }
                    if (isPanImgFailed && isPanNumFailed) {
                        tvSkip.visibility = GONE
                    }
                }
            }
            genericAPIService.setOnErrorListener {
                isPanUploaded = false
                CNProgressDialog.hideProgressDialog()
                tvSkip.visibility = GONE
                tvStatus.visibility = GONE
                tvStatus.text = getString(R.string.failed)
                etPan.setCompoundDrawables(
                        null,
                        null,
                        ContextCompat.getDrawable(this, R.drawable.ic_failure),
                        null
                )
                displayToast(it.message.toString())

                if (panNumber?.length!! < 10) {
                    isPanImgFailed = true
                } else {
                    isPanNumFailed = true
                }

                if (isPanImgFailed && isPanNumFailed) {
                    tvSkip.visibility = GONE
                }

            }
        } catch (e: java.lang.Exception) {
            isPanUploaded = false
            e.printStackTrace()
        }
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

    override fun onBackPressed() {
        val intent = Intent(this, DashboardActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or
                Intent.FLAG_ACTIVITY_CLEAR_TASK or
                Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
    }

}