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
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.view.Window
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
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
import com.capitalnowapp.mobile.databinding.ActivitySalarySlipBinding
import com.capitalnowapp.mobile.kotlin.activities.DashboardActivity
import com.capitalnowapp.mobile.kotlin.utils.FileUtils
import com.capitalnowapp.mobile.models.ContactUsResponse
import com.capitalnowapp.mobile.models.FileUploadAjaxRequest
import com.capitalnowapp.mobile.models.FileUploadResponse
import com.capitalnowapp.mobile.models.offerModel.CSGenericResponse
import com.capitalnowapp.mobile.models.offerModel.OfferSalarySlipReq
import com.capitalnowapp.mobile.models.offerModel.ProfileFormDataReq
import com.capitalnowapp.mobile.models.offerModel.ProfileFormDataResponse
import com.capitalnowapp.mobile.retrofit.GenericAPIService
import com.capitalnowapp.mobile.retrofit.ProgressAPIService
import com.capitalnowapp.mobile.util.RealPathUtil
import com.capitalnowapp.mobile.util.Utility
import com.google.gson.Gson
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class SalarySlipActivity : BaseActivity() {
    companion object {
        private const val CAMERA_PERMISSION_CODE = 100
    }

    private val REQUEST_IMAGE_CAPTURE = 102
    private var salarySlipImage: Uri? = null
    private var activity: AppCompatActivity? = null
    private var slipUri: Uri? = null
    private var currentPath = ""
    private var selectedFile: String = ""
    private var fileUrl: String = ""
    private var pageNumber: Int? = 20
    private var isSlipSelected: Boolean = false
    private var fileUploadAjaxRequest: FileUploadAjaxRequest? = null
    private var profileFormDataResponse = ProfileFormDataResponse()
    private var genericResponse: ContactUsResponse = ContactUsResponse()
    private var offerSalarySlipReq = OfferSalarySlipReq()
    private var binding: ActivitySalarySlipBinding? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySalarySlipBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        activity = this
        initView()
    }

    private fun initView() {
        profileFormData()
        binding?.ivSalarySlip?.setOnClickListener {
            try {
                checkPermission(
                    Manifest.permission.CAMERA,
                    CAMERA_PERMISSION_CODE
                )
                selectImage()
                salarySlipImage = null
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        binding?.ivCancel?.setOnClickListener {
            binding?.ivFrame?.visibility = View.GONE
            binding?.ivSalarySlip?.visibility = View.VISIBLE
        }
        binding?.tvNext?.setOnClickListener {
            uploadCsSalarySlip()
        }
        binding?.ivBack?.setOnClickListener {
            val intent = Intent(this, DashboardActivity::class.java)
            startActivity(intent)
        }

        binding?.ivHelp?.setOnClickListener {
            showHelpPopup()
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
            val emailIntent = Intent(
                Intent.ACTION_SENDTO, Uri.fromParts(
                    "mailto", genericResponse.email, null
                )
            )
            startActivity(Intent.createChooser(emailIntent, "Choose to send email..."))
        } catch (e: Exception) {
            e.printStackTrace()
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
                if (takePictureIntent.resolveActivity((activity as BaseActivity).packageManager) != null) {
                    // Create the File where the photo should go
                    var photoFile: File? = null
                    try {
                        photoFile = createImageFile()

                        slipUri = Uri.fromFile(photoFile)

                    } catch (ex: IOException) {
                        // Error occurred while creating the File
                    }
                    try {
                        // Continue only if the File was successfully created
                        if (photoFile != null) {
                            val photoURI: Uri = FileProvider.getUriForFile(
                                this,
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
                val intent = getPDFChooserIntent()
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    startActivityForResult(
                        intent,
                        Constants.REQUEST_CODE_CHOOSE_DOCUMENT_TO_UPLOAD
                    )
                } else {
                    if (intent.resolveActivity(activity?.packageManager!!) != null) {
                        startActivityForResult(
                            intent,
                            Constants.REQUEST_CODE_CHOOSE_DOCUMENT_TO_UPLOAD
                        )
                    } else {
                        (activity as BaseActivity).displayToast(resources.getString(R.string.no_support_for_storage))
                    }
                }

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
        // Save a file: path for use with ACTION_VIEW intents
        return image
    }

    private fun getPDFChooserIntent(): Intent {
        val mimeTypes = arrayOf("application/pdf")
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        //retrieve scan result
        super.onActivityResult(requestCode, resultCode, intent)
        try {
            if (resultCode == Activity.RESULT_OK && requestCode == Constants.REQUEST_CODE_CHOOSE_DOCUMENT_TO_UPLOAD) {
                try {
                    val selectedDocumentUri: Uri = intent!!.data!!
                    selectedDocumentUri.let { activity?.contentResolver?.getType(it) }
                    val fileType = Utility.getMimeType(activity, selectedDocumentUri)

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
                                        RealPathUtil.getPathFromUri(activity, selectedDocumentUri)
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
                                    this
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
                            /*selectedFilePath?.let {
                                uploadFileWithProgress(
                                    it,
                                    isGooglePhotosUri,
                                    selectedFilePath.substring(selectedFilePath.lastIndexOf("/") + 1)
                                )
                            }*/

                            if (selectedFilePath != null) {
                                this.selectedFile = selectedFilePath
                                updateSelectedFilePath(
                                    selectedFilePath,
                                    isGooglePhotosUri,
                                    selectedFile.name, selectedFilePath
                                )
                            }
                        }

                    } else {
                        (activity as BaseActivity).displayToast(getString(R.string.upload_docs_format_validation_msg))
                    }
                } catch (e: java.lang.Exception) {
                    e.printStackTrace()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            } else if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
                if (slipUri != null) {
                    var selectedFile = File("")
                    selectedFile = File(slipUri!!.path.toString())
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
                        /*  selectedFilePath.let {
                              uploadFileWithProgress(
                                  it,
                                  false,
                                  selectedFilePath.substring(selectedFilePath.lastIndexOf("/") + 1)
                              )
                          }*/

                        if (selectedFilePath != null) {
                            this.selectedFile = selectedFilePath
                            updateSelectedFilePath(
                                selectedFilePath,
                                false,
                                selectedFile.name, selectedFilePath
                            )
                        }
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun uploadFileWithProgress(
        selectedFilePath: String,
        isExternalDoc: Boolean,
        fileName: String
    ) {
        val file = File(selectedFilePath)
        CNProgressDialog.showUploadProgressDialog(activity, Constants.LOADING_MESSAGE)
        val fileBody =
            ProgressAPIService(selectedFilePath, object : ProgressAPIService.UploadCallbacks {
                override fun onProgressUpdate(percentage: Int) {
                    Log.d("percentage", percentage.toString())
                }

                override fun onError() {
                    Log.e("imageupload", "error")
                    CNProgressDialog.hideProgressDialog()
                }

                override fun onFinish() {
                    Log.e("imageupload", "finish")
                    CNProgressDialog.hideProgressDialog()
                }

            })

        val fileToUpload: MultipartBody.Part =
            MultipartBody.Part.createFormData("file", file.name, fileBody)
        val token = userToken
        val apiKey = token.toRequestBody("text/plain".toMediaTypeOrNull())
        val userId: RequestBody = (activity as BaseActivity).userDetails.userId!!
            .toRequestBody("text/plain".toMediaTypeOrNull())

        val call: retrofit2.Call<FileUploadResponse?>? =
            fileBody.apiService.uploadFile(fileToUpload, apiKey, userId)
        call?.enqueue(object : Callback<FileUploadResponse?> {
            override fun onResponse(
                call: retrofit2.Call<FileUploadResponse?>,
                response: Response<FileUploadResponse?>
            ) {
                if (response.body() != null && response.body()!!.status) {
                    val fileUploadResponse = response.body()
                    if (fileUploadResponse != null && fileUploadResponse.status) {
                        /*updateSelectedFilePath(
                            fileUploadResponse.fileUrl,
                            isExternalDoc,
                            fileName,
                            selectedFilePath
                        )*/
                    }
                }
                CNProgressDialog.hideProgressDialog()
            }

            override fun onFailure(call: retrofit2.Call<FileUploadResponse?>, t: Throwable) {
                Log.e("imageupload", t.toString())
            }
        })
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

    private fun updateSelectedFilePath(
        fileUrl: String,
        isExternalDoc: Boolean,
        fileName: String?,
        selectedFilePath: String
    ) {
        try {
            val textView: CNTextView? = null
            fileUploadAjaxRequest = FileUploadAjaxRequest()
            fileUploadAjaxRequest!!.userId = (activity as BaseActivity).userId
            var fileUrls: String? = ""
            fileUrls = fileUrl

            offerSalarySlipReq.uploadSalarySlip = fileUrl
            offerSalarySlipReq.pageNo = 20
            isSlipSelected = true
            binding?.ivFrame?.visibility = View.VISIBLE
            binding?.ivSalarySlip?.visibility = View.GONE
            binding?.ivSalarySlipImg?.setImageURI(Uri.parse(selectedFilePath))
            fileUploadAjaxRequest!!.fileUrls = fileUrls

            var strName = ""
            strName = if (fileName?.length!! > 20) {
                fileName.substring(0, 10) + "..."
            } else {
                fileName
            }
            if (textView != null) {
                textView.text = strName
                textView.setCompoundDrawablesRelativeWithIntrinsicBounds(
                    0,
                    0,
                    R.drawable.ic_remove_image,
                    0
                )
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    private fun updateFileAjaxCall() {
        val genericAPIService = GenericAPIService(this)
        fileUploadAjaxRequest?.deviceUniqueId = Utility.getInstance().getDeviceUniqueId(activity)
        Log.d("file ajax", Gson().toJson(fileUploadAjaxRequest))
        val token = userToken
        genericAPIService.uploadFileToServer(fileUploadAjaxRequest, token)
    }

    private fun isGoogleDriveUri(uri: Uri): Boolean {
        return "com.google.android.apps.docs.storage" == uri.authority || "com.google.android.apps.docs.storage.legacy" == uri.authority
    }


    private fun profileFormData() {
        try {
            CNProgressDialog.showProgressDialog(this, Constants.LOADING_MESSAGE)
            val genericAPIService = GenericAPIService(this, 0)
            val profileFormDataReq = ProfileFormDataReq()
            profileFormDataReq.pageNo = 20
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

    private fun setData() {
        if(profileFormDataResponse.offerHelp?.icon !=null){
            Glide.with(this).load(profileFormDataResponse.offerHelp!!.icon)
                .into(binding?.ivHelp!!)
        }
    }

    private fun uploadCsSalarySlip() {
        try {
            CNProgressDialog.showProgressDialog(this, Constants.LOADING_MESSAGE)
            val genericAPIService = GenericAPIService(this, 0)
            val token = userToken
            genericAPIService.uploadCsSalarySlip(selectedFile, pageNumber, token)
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

    private fun checkPermission(permission: String, requestCode: Int) {
        if (ContextCompat.checkSelfPermission(
                this@SalarySlipActivity,
                permission
            ) == PackageManager.PERMISSION_DENIED
        ) {

            // Requesting the permission
            ActivityCompat.requestPermissions(
                this@SalarySlipActivity,
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

}