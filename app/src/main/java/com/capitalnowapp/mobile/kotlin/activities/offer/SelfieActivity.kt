package com.capitalnowapp.mobile.kotlin.activities.offer

import android.Manifest
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
import com.capitalnowapp.mobile.databinding.ActivitySelfieBinding
import com.capitalnowapp.mobile.kotlin.activities.CropActivity
import com.capitalnowapp.mobile.kotlin.activities.DashboardActivity
import com.capitalnowapp.mobile.kotlin.utils.FileUtils
import com.capitalnowapp.mobile.models.ContactUsResponse
import com.capitalnowapp.mobile.models.offerModel.OfferUploadSelfieResponse
import com.capitalnowapp.mobile.models.offerModel.ProfileFormDataReq
import com.capitalnowapp.mobile.models.offerModel.ProfileFormDataResponse
import com.capitalnowapp.mobile.retrofit.GenericAPIService
import com.capitalnowapp.mobile.util.RealPathUtil
import com.capitalnowapp.mobile.util.Utility
import com.google.gson.Gson
import com.theartofdev.edmodo.cropper.CropImage
import java.io.*
import java.text.SimpleDateFormat
import java.util.*

class SelfieActivity : BaseActivity() {
    companion object {
        private const val CAMERA_PERMISSION_CODE = 100
    }

    private var activity: AppCompatActivity? = null
    private var currentPath = ""
    private var pageNumber: Int? = 22
    private var mMakePhotoUri: Uri? = null
    private val REQUEST_IMAGE_CAPTURE = 102
    private var selfieImage: Uri? = null
    private var selectedFile: String = ""
    private lateinit var ivSelfieImg: ImageView
    private var cropGooglePhotosUri: Boolean? = false
    private var cropFileName: String? = null
    private var binding: ActivitySelfieBinding? = null
    private var profileFormDataResponse = ProfileFormDataResponse()
    private var genericResponse: ContactUsResponse = ContactUsResponse()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySelfieBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        activity = this
        initView()
    }

    private fun initView() {
        profileFormData()
        binding?.ivCamera?.setOnClickListener {
            try {
                checkPermission(
                    Manifest.permission.CAMERA,
                    CAMERA_PERMISSION_CODE
                )
                selectImage()
                selfieImage = null
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        binding?.tvNext?.setOnClickListener {
            uploadSelfie()
        }
        binding?.ivHelp?.setOnClickListener {
            showHelpPopup()
        }
        binding?.ivBack?.setOnClickListener {
            val intent = Intent(this, DashboardActivity::class.java)
            startActivity(intent)
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

    private fun profileFormData() {
        try {
            CNProgressDialog.showProgressDialog(this, Constants.LOADING_MESSAGE)
            val genericAPIService = GenericAPIService(this, 0)
            val profileFormDataReq = ProfileFormDataReq()
            profileFormDataReq.pageNo = 22
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
        try{
            if(profileFormDataResponse.offerHelp?.icon !=null){
                Glide.with(this).load(profileFormDataResponse.offerHelp!!.icon)
                    .into(binding?.ivHelp!!)
            }
        }catch (e:Exception){
            e.printStackTrace()
        }
    }

    private fun checkPermission(permission: String, requestCode: Int) {
        if (ContextCompat.checkSelfPermission(
                this@SelfieActivity,
                permission
            ) == PackageManager.PERMISSION_DENIED
        ) {

            // Requesting the permission
            ActivityCompat.requestPermissions(
                this@SelfieActivity,
                arrayOf(permission),
                requestCode
            )
        } else {

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
                if (takePictureIntent.resolveActivity((activity as SelfieActivity).packageManager) != null) {
                    // Create the File where the photo should go
                    var photoFile: File? = null
                    try {
                        photoFile = createImageFile()
                        selfieImage = Uri.fromFile(photoFile)
                    } catch (ex: IOException) {
                        // Error occurred while creating the File
                    }
                    try {
                        // Continue only if the File was successfully created
                        if (photoFile != null) {
                            val photoURI: Uri = FileProvider.getUriForFile(
                                (activity as SelfieActivity),
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
                                    (activity as SelfieActivity)
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

                            Toast.makeText(
                                this,
                                "Uploading file size must be less than %d MB.",
                                Toast.LENGTH_LONG
                            ).show()

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

                        Toast.makeText(
                            this,
                            R.string.upload_docs_format_validation_msg,
                            Toast.LENGTH_LONG
                        ).show()

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
                        selfieImage = Uri.parse(File(result).absolutePath)
                        binding?.ivPreview?.setImageURI(selfieImage)
                        binding?.ivCamera?.visibility = View.GONE
                        binding?.ivPreview?.visibility = View.VISIBLE
                        //uploadCsPan()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            } else if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
                if (selfieImage != null) {
                    val selectedFile = File(selfieImage!!.path.toString())
                    val reducedFile = saveBitmapToFile(selectedFile)
                    val selectedFilePath = reducedFile!!.path
                    val selectedFileSizeInMB = reducedFile.length() / (1024 * 1024)
                    if (selectedFileSizeInMB > Constants.FILE_UPLOAD_LIMIT) {
                        Toast.makeText(
                            this,
                            "Uploading file size must be less than %d MB.",
                            Toast.LENGTH_LONG
                        ).show()

                        return
                    } else {
                        selectedFilePath.let {
                            showCrop(it, false, selectedFilePath, selfieImage!!)
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
            intent.putExtra("isProfilePic", true)
            startActivityForResult(intent, CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    private fun uploadSelfie() {
        try {
            CNProgressDialog.showProgressDialog(this, Constants.LOADING_MESSAGE)
            val genericAPIService = GenericAPIService(this, 0)
            val token = userToken
            genericAPIService.uploadCsSelfie(selectedFile, pageNumber, token)
            genericAPIService.setOnDataListener { responseBody ->
                CNProgressDialog.hideProgressDialog()
                val offerUploadSelfieResponse =
                    Gson().fromJson(responseBody, OfferUploadSelfieResponse::class.java)
                if (offerUploadSelfieResponse.status == true) {
                    //isPanUploaded = true
                    getApplyLoanDataBase(true)
                    finish()
                } else {
                    //fileUploadResponse.status = false
                }
            }
            genericAPIService.setOnErrorListener {
                fun errorData(throwable: Throwable?) {
                    //Failure
                    CNProgressDialog.hideProgressDialog()
                }
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

}