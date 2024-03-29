package com.capitalnowapp.mobile.kotlin.activities

import android.Manifest
import android.app.Activity.RESULT_OK
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
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.view.Window
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import com.capitalnowapp.mobile.BuildConfig
import com.capitalnowapp.mobile.R
import com.capitalnowapp.mobile.activities.BaseActivity
import com.capitalnowapp.mobile.constants.Constants
import com.capitalnowapp.mobile.customviews.CNAlertDialog
import com.capitalnowapp.mobile.customviews.CNButton
import com.capitalnowapp.mobile.customviews.CNProgressDialog
import com.capitalnowapp.mobile.databinding.ActivityAadharVerificationBinding
import com.capitalnowapp.mobile.kotlin.utils.FileUtils
import com.capitalnowapp.mobile.models.AadharOtpReq
import com.capitalnowapp.mobile.models.AadharOtpResponse
import com.capitalnowapp.mobile.models.FileUploadResponse
import com.capitalnowapp.mobile.models.GenericResponse
import com.capitalnowapp.mobile.models.GetCaptchaReq
import com.capitalnowapp.mobile.models.GetCaptchaResponse
import com.capitalnowapp.mobile.models.SkipAadharData
import com.capitalnowapp.mobile.retrofit.GenericAPIService
import com.capitalnowapp.mobile.util.RealPathUtil
import com.capitalnowapp.mobile.util.TrackingUtil
import com.capitalnowapp.mobile.util.Utility
import com.google.gson.Gson
import com.theartofdev.edmodo.cropper.CropImage
import kotlinx.android.synthetic.main.activity_aadhar_verification.etCaptcha
import org.json.JSONException
import org.json.JSONObject
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class AadharVerificationFragment : Fragment() {
    companion object {
        private const val CAMERA_PERMISSION_CODE = 100
    }

    private var getCaptchaResponse: GetCaptchaResponse? = null
    private var tempFile: File? = null
    private var selectedFileFront: String = ""
    private var selectedFileBack: String = ""
    private val REQUEST_IMAGE_CAPTURE = 102
    private var aadharNum: String = ""
    private var aadharFront: Uri? = null
    private var aadharBack: Uri? = null
    private var binding: ActivityAadharVerificationBinding? = null
    private var currentPath = ""
    private var mMakePhotoUri: Uri? = null
    private var cropFileName: String? = null
    private var cropGooglePhotosUri: Boolean? = false
    private var isFront: Boolean = false;
    private var isFrontSelected: Boolean = false;
    private var isBackSelected: Boolean = false;
    private var isAadharUploaded: Boolean = false;

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val obj = JSONObject()
        try {
            obj.put("cnid", (activity as BaseActivity).userDetails.qcId)
        } catch (e: JSONException) {
            throw RuntimeException(e)
        }
        TrackingUtil.pushEvent(obj, getString(R.string.aadhar_verification_page_landed))

        getCaptcha()

        binding!!.ivAadhaarFront.setOnClickListener {

            checkPermission(
                Manifest.permission.CAMERA,
                CAMERA_PERMISSION_CODE
            )
            checkPermission(
                Manifest.permission.WRITE_EXTERNAL_STORAGE, CAMERA_PERMISSION_CODE
            )
            selectImage(true)
        }
        binding!!.ivAadhaarBack.setOnClickListener {


            checkPermission(
                Manifest.permission.CAMERA,
                CAMERA_PERMISSION_CODE
            )
            selectImage(false)
        }

        binding!!.ivCancelFront.setOnClickListener {
            isFrontSelected = false

            binding!!.ivAadhaarFront.visibility = View.VISIBLE
            binding!!.ivFrameFront.visibility = View.GONE
            aadharFront = null

            if (!isBackSelected) {
                binding!!.etAadhar.isEnabled = true
            }
        }
        binding!!.ivCancelBack.setOnClickListener {
            isBackSelected = false

            binding!!.ivAadhaarBack.visibility = View.VISIBLE
            binding!!.ivFrameBack.visibility = View.GONE
            aadharBack = null

            if (!isFrontSelected) {
                binding!!.etAadhar.isEnabled = true
            }
        }

        binding?.ivRefresh?.setOnClickListener {
            getCaptcha()
        }

        binding!!.etAadhar.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                when {
                    s?.length == 12 -> {
                        aadharNum = s.toString()
                        uploadAadhar()
                    }

                    s?.length!! > 0 -> {
                        aadharNum = ""
                        binding!!.ivAadhaarFront.isEnabled = false
                        binding!!.ivAadhaarBack.isEnabled = false
                    }

                    else -> {
                        binding!!.ivFrameBack.isEnabled = true
                        binding!!.ivAadhaarFront.isEnabled = true
                        binding!!.ivAadhaarBack.isEnabled = true
                        binding!!.ivCancelFront.isEnabled = true
                        binding!!.ivCancelBack.isEnabled = true
                        binding!!.ivFrameFront.isEnabled = true
                        aadharNum = ""
                    }
                }
                binding!!.etAadhar.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            }
        })

        binding!!.tvSkip.setOnClickListener {
            skipAadhar()
        }

        binding!!.tvSendOtp.setOnClickListener {
            if (isAadharUploaded && binding!!.cbConfirmAadhaar.isChecked && binding?.etAadhar?.length() == 12) {
                    if (getCaptchaResponse == null || getCaptchaResponse!!.captchaRequired == null || getCaptchaResponse!!.captchaRequired == false || (getCaptchaResponse!!.captchaRequired == true && binding?.etCaptcha?.editableText!!.isNotEmpty())) {
                        sendAadharOtp()
                    } else {
                        Toast.makeText(context, "Please Enter Captcha", Toast.LENGTH_SHORT).show()
                    }
            } else {
                if (binding!!.etAadhar.length() < 12 || !isAadharUploaded) {
                    Toast.makeText(context, "Enter Valid Aadhaar Number", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "Please select the consent", Toast.LENGTH_SHORT).show()
                }
            }
        }
        binding?.tvUpload?.setOnClickListener {

            val obj = JSONObject()
            try {
                obj.put("cnid", (activity as BaseActivity).userDetails.qcId)
                obj.put(getString(R.string.interaction_type), "Upload Button Clicked")
            } catch (e: JSONException) {
                throw RuntimeException(e)
            }
            TrackingUtil.pushEvent(obj, getString(R.string.aadhar_manual_upload_page_interacted))
            if (isAadharUploaded && binding!!.cbConfirmAadhaar.isChecked) {
                updateAdharManualDocs()
            }else {
                if(!isAadharUploaded){
                    Toast.makeText(context, "Please Upload Aadhaar", Toast.LENGTH_SHORT).show()
                }else {
                    Toast.makeText(context, "Please select the consent", Toast.LENGTH_SHORT).show()
                }
            }
        }

        binding?.cbConfirmMobile?.setOnCheckedChangeListener { buttonView, isChecked ->

            val obj = JSONObject()
            try {
                obj.put("cnid", (activity as BaseActivity).userDetails.qcId)
                obj.put(getString(R.string.interaction_type), "No Mobile Number Check box ticked")
            } catch (e: JSONException) {
                throw RuntimeException(e)
            }
            TrackingUtil.pushEvent(obj, getString(R.string.aadhar_verification_page_interacted))

            if (binding?.cbConfirmMobile?.isChecked!!) {
                binding?.llUploadAadhar?.visibility = VISIBLE
                binding?.llEnterAadhar?.visibility = GONE
                binding?.tvUpload?.visibility = VISIBLE
                binding?.tvSendOtp?.visibility = GONE
                binding?.etAadhar?.text?.clear()
            } else {
                binding?.llEnterAadhar?.visibility = VISIBLE
                binding?.llUploadAadhar?.visibility = GONE
                binding?.tvUpload?.visibility = GONE
                binding?.tvSendOtp?.visibility = VISIBLE
            }
        }

    }

    private fun getCaptcha() {
        try{
            val genericAPIService = GenericAPIService(activity, 0)
            val getCaptchaReq = GetCaptchaReq()
            val token = (activity as BaseActivity).userToken
            genericAPIService.getCaptcha(getCaptchaReq, token)
            genericAPIService.setOnDataListener { responseBody ->
                getCaptchaResponse = Gson().fromJson(
                    responseBody,
                    GetCaptchaResponse::class.java
                )
                if (getCaptchaResponse != null && getCaptchaResponse!!.status == true) {
                    if(getCaptchaResponse!!.captchaRequired==true){
                        setCaptchaImage(getCaptchaResponse!!)
                    }else {
                        binding?.llCaptcha?.visibility = GONE
                    }

                } else {
                    Toast.makeText(context, getCaptchaResponse!!.message, Toast.LENGTH_SHORT)
                }

            }
            genericAPIService.setOnErrorListener {
                fun errorData(throwable: Throwable?) {
                    //Failure
                }
            }

        }catch (e: Exception){
            e.printStackTrace()
        }
    }

    private fun setCaptchaImage(getCaptchaResponse: GetCaptchaResponse) {
        try{
            binding?.llCaptcha?.visibility = VISIBLE
            val captchaImage = getCaptchaResponse.captchaImage
            val imageBytes = android.util.Base64.decode(captchaImage, android.util.Base64.DEFAULT);
            val decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
            binding?.ivCaptcha!!.setImageBitmap(decodedImage)

        }catch (e: Exception){
            e.printStackTrace()
        }
    }

    private fun updateAdharManualDocs() {
        try {
            CNProgressDialog.showProgressDialog(activity, Constants.LOADING_MESSAGE)
            val genericAPIService = GenericAPIService(activity as BaseActivity)
            val updateAdharManualDocsReq = SkipAadharData()
            val token = (activity as BaseActivity).userToken
            genericAPIService.updateAdharManualDocs(updateAdharManualDocsReq, token)
            genericAPIService.setOnDataListener { responseBody ->
                CNProgressDialog.hideProgressDialog()
                val genericResponse = Gson().fromJson(
                    responseBody,
                    GenericResponse::class.java
                )
                if (genericResponse != null && genericResponse.status) {
                    (activity as DashboardActivity).getApplyLoanData(true)
                }else {
                    Toast.makeText(context, "Aadhaar Upload Failed", Toast.LENGTH_SHORT).show()
                }
            }
            genericAPIService.setOnErrorListener {
                fun errorData(throwable: Throwable?) {
                    CNProgressDialog.hideProgressDialog()
                }
            }

        } catch (e : Exception){
            e.printStackTrace()
        }
    }

    private fun checkPermission(permission: String, requestCode: Int) {
        if (ContextCompat.checkSelfPermission(
                requireActivity(),
                permission
            ) == PackageManager.PERMISSION_DENIED
        ) {

            // Requesting the permission
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(permission), requestCode)
        } else {
        }
    }

    // This function is called when the user accepts or decline the permission.
    // Request Code is used to check which permission called this function.
    // This request code is provided when the user is prompt for permission.
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

    private fun skipAadhar() {
        val genericAPIService = GenericAPIService(activity as BaseActivity)
        val skipAadharData = SkipAadharData()
        skipAadharData.aadharSkip = "Yes"
        skipAadharData.userId = (activity as BaseActivity).userDetails.userId
        val token = (activity as BaseActivity).userToken
        genericAPIService.skipAadharData(skipAadharData, token)
        genericAPIService.setOnDataListener { responseBody ->
            val genericResponse = Gson().fromJson(
                responseBody,
                GenericResponse::class.java
            )
            if (genericResponse != null && genericResponse.status) {
                (activity as DashboardActivity).selectedTab =
                    (activity as DashboardActivity).getString(R.string.home)
                (activity as DashboardActivity).navMenuAdapter.setSelectedTab((activity as DashboardActivity).selectedTab!!)
                (activity as DashboardActivity).isFromApply = false
                (activity as DashboardActivity).getApplyLoanData(true)
            }
        }
        genericAPIService.setOnErrorListener {
            fun errorData(throwable: Throwable?) {
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = ActivityAadharVerificationBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    private fun sendAadharOtp() {
        try {
            CNProgressDialog.showProgressDialog(activity, Constants.LOADING_MESSAGE)
            val genericAPIService = GenericAPIService(activity, 0)
            val aadharOtpReq = AadharOtpReq()
            if ((activity as DashboardActivity).mCurrentLocation != null) {
                aadharOtpReq.long =
                    (activity as DashboardActivity).mCurrentLocation?.longitude.toString()
                aadharOtpReq.lat =
                    (activity as DashboardActivity).mCurrentLocation?.latitude.toString()
            }
            aadharOtpReq.userId = (activity as DashboardActivity).userDetails.userId
            aadharOtpReq.aadharno = aadharNum
            aadharOtpReq.sessionId = getCaptchaResponse!!.sessionId
            aadharOtpReq.aadharCaptcha = binding?.etCaptcha!!.text!!.trim().toString()
            val token = (activity as BaseActivity).userToken
            genericAPIService.sendAadharOtp(aadharOtpReq, token)
            Log.d("aadhaar req", Gson().toJson(aadharOtpReq))
            genericAPIService.setOnDataListener { responseBody ->
                CNProgressDialog.hideProgressDialog()
                val aadharOtpResponse = Gson().fromJson(
                    responseBody,
                    AadharOtpResponse::class.java
                )
                if (aadharOtpResponse != null && aadharOtpResponse.status == true) {
                    val bundle = Bundle()
                    bundle.putString("accessKey", aadharOtpResponse.accessKey.toString())
                    bundle.putString("aadharNum", aadharNum)
                    (activity as DashboardActivity).replaceAadharOtp(bundle)
                    if (tempFile != null) {
                        tempFile?.delete()
                    }
                } else {
                    Toast.makeText(context, aadharOtpResponse.message, Toast.LENGTH_SHORT)
                        .show()
                    etCaptcha.text!!.clear()
                    getCaptcha()
                    binding!!.tvSkip.visibility = GONE

                }

            }
            genericAPIService.setOnErrorListener {
                fun errorData(throwable: Throwable?) {
                    //Failure
                    CNProgressDialog.hideProgressDialog()
                }
            }
        }catch (e:Exception){
            e.printStackTrace()
        }
    }


    private fun uploadAadhar() {
        try {
            CNProgressDialog.showProgressDialog(activity, Constants.LOADING_MESSAGE)
            val genericAPIService = GenericAPIService(activity,0)
            val token = (activity as BaseActivity).userToken

            genericAPIService.uploadAadharImage(
                selectedFileFront,
                selectedFileBack,
                (activity as BaseActivity).userDetails.userId,
                aadharNum, token
            )
            genericAPIService.setOnDataListener { responseBody ->
                CNProgressDialog.hideProgressDialog()
                val fileUploadResponse =
                    Gson().fromJson(responseBody, FileUploadResponse::class.java)
                if (fileUploadResponse != null && fileUploadResponse.status) {
                    isAadharUploaded = true
                    if (aadharNum.length!! < 12) {
                        binding!!.etAadhar.isEnabled = false
                        binding!!.ivAadhaarFront.isEnabled = false
                        binding!!.ivAadhaarFront.setImageResource(R.drawable.uploadgrey)
                        binding!!.ivAadhaarBack.setImageResource(R.drawable.uploadgrey)
                        binding!!.ivAImgFront.isEnabled = false
                        binding!!.ivFrameFront.isEnabled = false
                        binding!!.ivCancelFront.isEnabled = false
                        binding!!.ivAadhaarBack.isEnabled = false
                        binding!!.ivAImgBack.isEnabled = false
                        binding!!.ivFrameBack.isEnabled = false
                        binding!!.ivCancelBack.isEnabled = false
                        binding!!.tvSuccess.visibility = View.VISIBLE
                        binding!!.tvfailure.visibility = GONE
                        //aadharNum = fileUploadResponse.aadharno
                    } else {
                        binding!!.etAadhar.setCompoundDrawablesWithIntrinsicBounds(
                            0,
                            0,
                            R.drawable.ic_success,
                            0
                        )
                    }
                } else {
                    isAadharUploaded = false
                    if (aadharNum.length < 12) {
                        binding!!.etAadhar.isEnabled = true
                        binding!!.tvfailure.visibility = VISIBLE
                        binding!!.ivAadhaarFront.visibility = VISIBLE
                        binding!!.ivFrameFront.visibility = GONE
                        binding!!.tvSuccess.visibility = GONE
                        binding!!.ivAadhaarBack.visibility = VISIBLE
                        binding!!.ivFrameBack.visibility = GONE
                        binding!!.ivAadhaarFront.isEnabled = true
                        binding!!.ivAadhaarBack.isEnabled = true
                        isFrontSelected = false
                        isBackSelected = false
                        //aadharNum = fileUploadResponse.aadharno
                        Toast.makeText(context, fileUploadResponse.message, Toast.LENGTH_SHORT).show()
                    } else {
                        binding!!.etAadhar.setCompoundDrawablesWithIntrinsicBounds(
                            0,
                            0,
                            R.drawable.ic_failure,
                            0
                        )
                    }
                }
            }
            genericAPIService.setOnErrorListener {
                binding!!.etAadhar.setCompoundDrawablesWithIntrinsicBounds(
                    0,
                    0,
                    R.drawable.ic_failure,
                    0
                );
                CNAlertDialog.showAlertDialog(
                    context,
                    resources.getString(R.string.title_alert),
                    "Aadhar Upload Failed"
                )

                isAadharUploaded = false
                CNProgressDialog.hideProgressDialog()
                (activity as BaseActivity).displayToast(it.message.toString())
            }
        } catch (e: java.lang.Exception) {
            isAadharUploaded = false
            e.printStackTrace()
        }
    }

    private fun selectImage(isFront: Boolean) {
        try {
            this.isFront = isFront
            val alertDialog = Dialog(activity as BaseActivity)
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
                if (takePictureIntent.resolveActivity((activity as DashboardActivity).packageManager) != null) {
                    // Create the File where the photo should go
                    var photoFile: File? = null
                    try {
                        photoFile = createImageFile()
                        if (isFront) {
                            aadharFront = Uri.fromFile(photoFile)
                        } else {
                            aadharBack = Uri.fromFile(photoFile)
                        }
                    } catch (ex: IOException) {
                        // Error occurred while creating the File
                    }
                    try {
                        // Continue only if the File was successfully created
                        if (photoFile != null) {
                            val photoURI: Uri = FileProvider.getUriForFile(
                                (activity as DashboardActivity),
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
                if (!isFrontSelected && !isBackSelected) {
                    binding!!.etAadhar.isEnabled = true
                }
            }
            alertDialog.show()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    open fun createImageFile(): File? {
        // Create an image file name
        val timeStamp: String =
            SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val imageFileName = "JPEG_temp$timeStamp"
        val storageDir = activity?.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
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
                (activity as BaseActivity).displayToast(resources.getString(R.string.no_support_for_storage))
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

    @RequiresApi(Build.VERSION_CODES.O)
    fun createFileFromContentUri(fileUri : Uri) : File{

        var fileName : String = ""

        fileUri.let { returnUri ->
            requireActivity().contentResolver.query(returnUri,null,null,null)
        }?.use { cursor ->
            val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            cursor.moveToFirst()
            fileName = cursor.getString(nameIndex)
        }

        //  For extract file mimeType
        val fileType: String? = fileUri.let { returnUri ->
            requireActivity().contentResolver.getType(returnUri)
        }

        val iStream : InputStream = requireActivity().contentResolver.openInputStream(fileUri)!!
        val outputDir : File = context?.cacheDir!!
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
                        val fileType = Utility.getMimeType(context, selectedDocumentUri)

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
                                            RealPathUtil.getPathFromUri(
                                                activity,
                                                selectedDocumentUri
                                            )
                                        isGooglePhotosUri = true
                                    }
                                } catch (e: FileNotFoundException) {
                                    e.printStackTrace()
                                }
                            } else {
                                selectedFilePath =
                                    RealPathUtil.getPathFromUri(activity, selectedDocumentUri)
                                if (selectedFilePath == null || selectedFilePath == "" || (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R)) {
                                    selectedFilePath = FileUtils.makeFileCopyInCacheDir(
                                        selectedDocumentUri,
                                        (activity as DashboardActivity)
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
                            if (selectedFileSizeInMB > Constants.TWOMB_FILE_UPLOAD_LIMIT) {
                                (activity as BaseActivity).displayToast(
                                    String.format(
                                        "Uploading file size must be less than %d MB.",
                                        Constants.TWOMB_FILE_UPLOAD_LIMIT
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
                            (activity as BaseActivity).displayToast(getString(R.string.upload_docs_format_validation_msg))
                        }
                    }
                } catch (e: java.lang.Exception) {
                    e.printStackTrace()
                }
            } else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
                try {
                    val result = intent?.getStringExtra("crop")
                    if (resultCode == RESULT_OK) {
                        if (isFront) {
                            selectedFileFront = File(result).absolutePath
                            aadharFront = Uri.parse(File(result).absolutePath)
                            binding!!.ivAImgFront.setImageURI(aadharFront)
                            binding!!.ivAadhaarFront.visibility = View.GONE
                            binding!!.ivFrameFront.visibility = View.VISIBLE
                            isFrontSelected = true
                            binding!!.etAadhar.isEnabled = false
                        } else {
                            selectedFileBack = File(result).absolutePath
                            aadharBack = Uri.parse(File(result).absolutePath)
                            binding!!.ivAImgBack.setImageURI(aadharBack)
                            binding!!.ivAadhaarBack.visibility = View.GONE
                            binding!!.ivFrameBack.visibility = View.VISIBLE
                            isBackSelected = true
                        }
                        if (isFrontSelected && isBackSelected) {
                            uploadAadhar()
                            isAadharUploaded = true
                        }
                        binding!!.etAadhar.isEnabled = false
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            } else if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
                if (aadharFront != null || aadharBack != null) {
                    var selectedFile = File("")
                    if (isFront) {
                        selectedFile = File(aadharFront!!.path.toString())
                    } else {
                        selectedFile = File(aadharBack!!.path.toString())
                    }
                    val reducedFile = saveBitmapToFile(selectedFile)
                    val selectedFilePath = reducedFile!!.path
                    val selectedFileSizeInMB = reducedFile.length() / (1024 * 1024)
                    if (selectedFileSizeInMB > Constants.TWOMB_FILE_UPLOAD_LIMIT) {
                        (activity as DashboardActivity).displayToast(
                            String.format(
                                "Uploading file size must be less than %d MB.",
                                Constants.TWOMB_FILE_UPLOAD_LIMIT
                            )
                        )
                        return
                    } else {
                        selectedFilePath.let {
                            if (isFront) {
                                showCrop(it, false, selectedFilePath, aadharFront!!)
                            } else {
                                showCrop(it, false, selectedFilePath, aadharBack!!)
                            }
                        }
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
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


}