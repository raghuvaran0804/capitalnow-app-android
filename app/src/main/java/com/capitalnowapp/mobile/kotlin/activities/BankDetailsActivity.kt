package com.capitalnowapp.mobile.kotlin.activities

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
import android.provider.OpenableColumns
import android.util.Log
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.Window
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import com.android.volley.VolleyError
import com.capitalnowapp.mobile.BuildConfig
import com.capitalnowapp.mobile.R
import com.capitalnowapp.mobile.activities.BaseActivity
import com.capitalnowapp.mobile.beans.UploadDocuments
import com.capitalnowapp.mobile.constants.Constants
import com.capitalnowapp.mobile.customviews.CNAlertDialog
import com.capitalnowapp.mobile.customviews.CNButton
import com.capitalnowapp.mobile.customviews.CNProgressDialog
import com.capitalnowapp.mobile.customviews.CNTextView
import com.capitalnowapp.mobile.databinding.ActivityBankDetailsBinding
import com.capitalnowapp.mobile.interfaces.AlertDialogSelectionListener
import com.capitalnowapp.mobile.kotlin.fragments.ReferencesNewFragment
import com.capitalnowapp.mobile.kotlin.utils.FileUtils
import com.capitalnowapp.mobile.models.AnalyseCapabilityReq
import com.capitalnowapp.mobile.models.FileUploadAjaxRequest
import com.capitalnowapp.mobile.models.FileUploadResponse
import com.capitalnowapp.mobile.models.GenericResponse
import com.capitalnowapp.mobile.models.SubmitInitialDocsReq
import com.capitalnowapp.mobile.models.login.RegisterDeviceResponse
import com.capitalnowapp.mobile.models.userdetails.RegisterUserReq
import com.capitalnowapp.mobile.models.userdetails.UserDetails
import com.capitalnowapp.mobile.retrofit.GenericAPIService
import com.capitalnowapp.mobile.retrofit.ProgressAPIService
import com.capitalnowapp.mobile.util.CNSharedPreferences
import com.capitalnowapp.mobile.util.RealPathUtil
import com.capitalnowapp.mobile.util.Utility
import com.google.gson.Gson
import com.theartofdev.edmodo.cropper.CropImage
import kotlinx.android.synthetic.main.activity_bank_details.etBankSlipPassword
import kotlinx.android.synthetic.main.activity_bank_details.ivBank
import kotlinx.android.synthetic.main.activity_bank_details.ivFrameBank
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
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


class BankDetailsActivity : BaseActivity() {
    companion object {
        private const val CAMERA_PERMISSION_CODE = 100
    }

    private var tempFile: File? = null
    private val REQUEST_IMAGE_CAPTURE = 102
    private lateinit var binding: ActivityBankDetailsBinding
    private var isBank = false
    var canRedirect: Boolean = false
    private var selectedBankPath: String = ""
    private var selectedSlipPath: String = ""
    private var bankUri: Uri? = null
    private var slipUri: Uri? = null
    private var isBankSelected: Boolean = false
    private var isSlipSelected: Boolean = false
    private var cropFileName: String? = null
    private var cropGooglePhotosUri: Boolean? = false
    private var fileUploadAjaxRequest: FileUploadAjaxRequest? = null
    private var uploadDocuments: UploadDocuments? = null
    private var statementsUrl: String? = null
    private var activity: AppCompatActivity? = null
    private val userId: String? = null
    private var dynamicText = ""
    private var latestDocs = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBankDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        activity = this
        userId = userDetails.userId
        initView()
    }

    private fun initView() {
        if (intent.extras != null) {
            dynamicText = intent.getStringExtra("bank_statement_upload_text")!!
            binding.tvThreeMonths.text = dynamicText
            if (intent.hasExtra("latest_docs")) {
                binding.tvValidate.visibility = GONE
                binding.tvContinue.visibility = VISIBLE
            }
        }


        uploadDocuments = UploadDocuments((activity as BaseActivity).userId)

        binding.ivBank.setOnClickListener {

            checkPermission(
                Manifest.permission.CAMERA, CAMERA_PERMISSION_CODE
            )
            selectImage(true)
        }/*binding.ivSlip.setOnClickListener {
            checkPermission(Manifest.permission.CAMERA,
                CAMERA_PERMISSION_CODE
            )
            selectImage(false)
        }*/
        binding.ivCancelBank.setOnClickListener {
            isBankSelected = false
            ivFrameBank.visibility = GONE
            ivBank.visibility = VISIBLE
        }/*binding.ivCancelSlip.setOnClickListener {
            isSlipSelected = false
            ivFrameSlip.visibility = GONE
            ivSlip.visibility = VISIBLE
        }*/

        binding.tvValidate.setOnClickListener {
            if (isBankSelected) {
                validateUploadPendingDocuments()
            } else {
                displayToast("Please upload required documents to proceed")
            }
        }
        binding.tvContinue.setOnClickListener {
            if (isBankSelected) {
                validateDocuments()
            } else {
                displayToast("Please upload required documents to proceed")
            }
        }


    }

    private fun validateDocuments() {
        val bankStatementPwd: String = etBankSlipPassword?.text.toString()
        //uploadDocuments!!.bankStmt = statementsUrl
        uploadDocuments!!.bankStmtPassword = bankStatementPwd
        uploadDocuments!!.userId = (activity as BaseActivity).userDetails.userId
        uploadLatestDocs(uploadDocuments!!, true)
    }


    private fun validateUploadPendingDocuments() {
        //val salSlipPwd: String = etSlipPassword?.text.toString()
        val bankStatementPwd: String = etBankSlipPassword?.text.toString()

        //uploadDocuments!!.salSlipPassword = salSlipPwd
        uploadDocuments!!.bankStmtPassword = bankStatementPwd
        submitInitialDocs()
    }

    private fun checkPermission(permission: String, requestCode: Int) {
        if (ContextCompat.checkSelfPermission(
                this@BankDetailsActivity, permission
            ) == PackageManager.PERMISSION_DENIED
        ) {

            // Requesting the permission
            ActivityCompat.requestPermissions(
                this@BankDetailsActivity, arrayOf(permission), requestCode
            )
        } else {
        }
    }

    // This function is called when the user accepts or decline the permission.
    // Request Code is used to check which permission called this function.
    // This request code is provided when the user is prompt for permission.
    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            } else {
            }
        }
    }

    private fun submitInitialDocs() {
        try {
            CNProgressDialog.showProgressDialog(this, Constants.LOADING_MESSAGE)
            val genericAPIService = GenericAPIService(this)
            val submitInitialDocsReq = SubmitInitialDocsReq()
            submitInitialDocsReq.setUserId(userDetails.userId)
            submitInitialDocsReq.setBankStatementsPassword(uploadDocuments!!.bankStmtPassword)
            //submitInitialDocsReq.setSalSlipUrl(uploadDocuments!!.salSlip)
            //submitInitialDocsReq.salSlipPassword = uploadDocuments!!.salSlipPassword
            submitInitialDocsReq.deviceUniqueId = Utility.getInstance().getDeviceUniqueId(activity)
            submitInitialDocsReq.setBankStatementsUrl(statementsUrl)
            Log.d("docs_req", Gson().toJson(submitInitialDocsReq))
            val token = userToken
            genericAPIService.submitInitialDocs(submitInitialDocsReq, token)
            genericAPIService.setOnDataListener { responseBody ->
                CNProgressDialog.hideProgressDialog()
                val genericResponse = Gson().fromJson(responseBody, GenericResponse::class.java)
                if (genericResponse != null && genericResponse.status) {
                    if (genericResponse.statusRedirect == 3) {
                        val intent = Intent(this, DashboardActivity::class.java)
                        intent.putExtra("from", "manualbankdetails")
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or
                                Intent.FLAG_ACTIVITY_CLEAR_TASK or
                                Intent.FLAG_ACTIVITY_NEW_TASK
                        startActivity(intent)
                    } else {
                        val intent = Intent(this, DashboardActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or
                                Intent.FLAG_ACTIVITY_CLEAR_TASK or
                                Intent.FLAG_ACTIVITY_NEW_TASK
                        startActivity(intent)
                    }

                } else {
                    CNAlertDialog.showAlertDialog(
                        this,
                        resources.getString(com.capitalnowapp.mobile.R.string.title_alert),
                        genericResponse.message
                    )
                }
                if (genericResponse.statusCode == Constants.STATUS_CODE_UNAUTHORISED) {
                    (activity as BaseActivity).logout()

                } else {
                    assert(genericResponse != null)
                    //Toast.makeText(this, genericResponse!!.message, Toast.LENGTH_SHORT).show()
                }
            }
            genericAPIService.setOnErrorListener {
                CNProgressDialog.hideProgressDialog()
                Toast.makeText(
                    this,
                    resources.getString(com.capitalnowapp.mobile.R.string.error_failure),
                    Toast.LENGTH_SHORT
                ).show()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            CNProgressDialog.hideProgressDialog()

        }
    }

    private fun uploadLatestDocs(uploadDocuments: UploadDocuments, flag: Boolean) {
        try {
            if (flag) {
                CNProgressDialog.showProgressDialog(activity, Constants.LOADING_MESSAGE)
            }
            val genericAPIService = GenericAPIService(activity)
            val bankStatements = ArrayList<String>()

            if (uploadDocuments.bankStmt != null && uploadDocuments.bankStmt != "") {
                bankStatements.add(tempFile.toString())
            }
            val token = (activity as BaseActivity).userToken

            genericAPIService.uploadLatestDocsBank(
                uploadDocuments.salSlip,
                bankStatements,
                uploadDocuments.salSlipPassword,
                uploadDocuments.bankStmtPassword,
                (activity as BaseActivity).userDetails.userId,
                this,
                Utility.getInstance().getDeviceUniqueId(activity),
                token
            )
            genericAPIService.setOnDataListener { responseBody ->
                CNProgressDialog.hideProgressDialog()
                updateFileUploadStatus(responseBody)
                val intent = Intent(this, DashboardActivity::class.java)
                intent.putExtra("from", "manualbankdetails")
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or
                        Intent.FLAG_ACTIVITY_CLEAR_TASK or
                        Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
                //getApplyLoanData(true)
            }
            genericAPIService.setOnErrorListener {
                fun errorData(throwable: Throwable?) {
                    CNProgressDialog.hideProgressDialog()
                }

            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    fun updateFileUploadStatus(response: String) {
        CNProgressDialog.hideProgressDialog()
        var message = ""
        try {
            if (response.isNotEmpty()) {
                if (response == Constants.STATUS_FAILURE) {
                    message =
                        resources.getString(com.capitalnowapp.mobile.R.string.documents_uploading_failed)
                    showAlertDialog(message)
                } else {
                    val jsonResponseObject = JSONObject(response)
                    val status = jsonResponseObject.getString("status")
                    if (jsonResponseObject.has("message")) message =
                        jsonResponseObject.getString("message")
                    if (status == Constants.STATUS_SUCCESS || status == Constants.LIMIT_EXHAUSTED) {
                        /* val logInIntent = Intent(context, DashboardActivity::class.java)
                         logInIntent.flags = Intent.FLAG_ACTIVITY_NO_ANIMATION or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                         startActivity(logInIntent)*/
                        //adgydeCounting(getString(R.string.upload_document_existing_user))
                        if (jsonResponseObject.has("reference_redirection") && jsonResponseObject.getBoolean(
                                "reference_redirection"
                            )
                        ) {
                            (activity as DashboardActivity).getApplyLoanData(true)
                            val addReferencesFragment = ReferencesNewFragment()
                            if (jsonResponseObject.has("reference_notice")) {
                                val msg = jsonResponseObject.getString("reference_notice")
                                val bundle = Bundle()
                                bundle.putString("msg", msg)
                                addReferencesFragment.arguments = bundle
                            }
                            (activity as DashboardActivity).addReferencesFragment =
                                addReferencesFragment
                            (activity as DashboardActivity).replaceFrag(
                                addReferencesFragment,
                                (activity as DashboardActivity).getString(com.capitalnowapp.mobile.R.string.add_references),
                                null
                            )
                            (activity as DashboardActivity).isFromApply = false
                            (activity as DashboardActivity).selectedTab =
                                (activity as DashboardActivity).getString(com.capitalnowapp.mobile.R.string.add_references)
                            (activity as DashboardActivity).navMenuAdapter.setSelectedTab((activity as DashboardActivity).selectedTab!!)
                        } else {
                            (activity as DashboardActivity).selectedTab =
                                (activity as DashboardActivity).getString(com.capitalnowapp.mobile.R.string.home)
                            (activity as DashboardActivity).navMenuAdapter.setSelectedTab((activity as DashboardActivity).selectedTab!!)
                            (activity as DashboardActivity).isFromApply = false
                            (activity as DashboardActivity).getApplyLoanData(true)
                        }
                    } else {
                        showAlertDialog(message)
                    }
                }
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }


    private fun analyseCapability() {
        val genericAPIService = GenericAPIService(activity as BaseActivity)
        val analyseCapabilityReq = AnalyseCapabilityReq()
        analyseCapabilityReq.userId = (activity as BaseActivity).userDetails.userId
        analyseCapabilityReq.devicetype = "Android"
        val token = userToken
        genericAPIService.analyseCapability(analyseCapabilityReq, token)
        genericAPIService.setOnDataListener { responseBody ->
            val genericResponse = Gson().fromJson(
                responseBody, RegisterDeviceResponse::class.java
            )
            if (genericResponse.status == Constants.STATUS_SUCCESS) {
                //Success
                val token = userToken
                val userId = (activity as BaseActivity).userDetails.userId
                val req = RegisterUserReq()
                cnModel.saveStep2Registration(userId, req, token)
            } else {
                //Error
            }
        }
        genericAPIService.setOnErrorListener {
            fun errorData(throwable: Throwable?) {
            }
        }
    }

    private fun selectImage(isBank: Boolean) {
        try {
            this.isBank = isBank
            val alertDialog = Dialog(activity as BaseActivity)
            alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            alertDialog.setContentView(com.capitalnowapp.mobile.R.layout.selectimage_dialog)
            alertDialog.window!!.setLayout(
                FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT
            )
            alertDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            alertDialog.setCanceledOnTouchOutside(true)
            val takePhoto =
                alertDialog.findViewById<CNButton>(com.capitalnowapp.mobile.R.id.btnTakePhoto)
            val gallery = alertDialog.findViewById<CNButton>(com.capitalnowapp.mobile.R.id.btnGallery)
            val cancel = alertDialog.findViewById<ImageView>(com.capitalnowapp.mobile.R.id.ivCancel)

            if (isBank) {
                takePhoto.visibility = GONE
            }

            takePhoto.setOnClickListener {
                val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                if (takePictureIntent.resolveActivity((activity as BaseActivity).packageManager) != null) {
                    // Create the File where the photo should go
                    var photoFile: File? = null
                    try {
                        photoFile = createImageFile()
                        if (isBank) {
                            bankUri = Uri.fromFile(photoFile)
                        } else {
                            slipUri = Uri.fromFile(photoFile)
                        }
                    } catch (ex: IOException) {
                        // Error occurred while creating the File
                    }
                    try {
                        // Continue only if the File was successfully created
                        if (photoFile != null) {
                            val photoURI: Uri = FileProvider.getUriForFile(
                                this, BuildConfig.APPLICATION_ID + ".fileprovider", photoFile
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
                if (isBank) {
                    chooseFile()
                } else {
                    chooseFile()
                }
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
        val intent = getImageChooserIntent()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            startActivityForResult(intent, Constants.REQUEST_CODE_CHOOSE_DOCUMENT_TO_UPLOAD)
        } else {
            if (intent.resolveActivity(activity?.packageManager!!) != null) {
                startActivityForResult(intent, Constants.REQUEST_CODE_CHOOSE_DOCUMENT_TO_UPLOAD)
            } else {
                (activity as BaseActivity).displayToast(resources.getString(com.capitalnowapp.mobile.R.string.no_support_for_storage))
            }
        }
    }

    private fun getImageChooserIntent(): Intent {
        val mimeTypes = arrayOf("application/pdf")
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION)
        //intent.setType(mimeTypes.length == 1 ? mimeTypes[0] : "*/*");
        intent.type = "application/pdf"
        if (mimeTypes.isNotEmpty()) {
            intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
        }
        return intent
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun createFileFromContentUri(fileUri: Uri): File {

        var fileName: String = ""

        fileUri.let { returnUri ->
            activity!!.contentResolver.query(returnUri, null, null, null)
        }?.use { cursor ->
            val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            cursor.moveToFirst()
            fileName = cursor.getString(nameIndex)
        }

        //  For extract file mimeType
        val fileType: String? = fileUri.let { returnUri ->
            this.contentResolver.getType(returnUri)
        }

        val iStream: InputStream = this.contentResolver.openInputStream(fileUri)!!
        val outputDir: File = this.cacheDir!!
        val outputFile: File = File(outputDir, fileName)
        copyStreamToFile(iStream, outputFile)
        iStream.close()
        return outputFile
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
            if (resultCode == Activity.RESULT_OK && requestCode == Constants.OPEN_DOCUMENT_REQUEST_CODE) {
                try {
                    intent?.data?.also { documentUri ->
                        (activity as BaseActivity).contentResolver.takePersistableUriPermission(
                            documentUri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                        )
                        //openDocument(documentUri)
                        tempFile = createFileFromContentUri(documentUri)
                        val filePath = tempFile!!.path
                        val fileUri = tempFile!!.toUri()
                        val selectedDocumentUri: Uri = fileUri
                        selectedDocumentUri.let { activity?.contentResolver?.getType(it) }
                        val fileType = Utility.getMimeType(this, selectedDocumentUri)

                        if ( fileType.equals(
                                "PDF", ignoreCase = true
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
                                        selectedFilePath = RealPathUtil.getPathFromUri(
                                            activity, selectedDocumentUri
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
                                        selectedDocumentUri, this
                                    )
                                }/*selectedFilePath = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
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
                                    uploadFileWithProgress(
                                        it,
                                        isGooglePhotosUri,
                                        selectedFilePath.substring(selectedFilePath.lastIndexOf("/") + 1)
                                    )
                                }
                            }


                        } else {
                            (activity as BaseActivity).displayToast("Please select PDF format file only.")
                        }
                    }
                } catch (e: java.lang.Exception) {
                    e.printStackTrace()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            } else if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
                if (bankUri != null || slipUri != null) {
                    var selectedFile = File("")
                    if (isBank) {
                        selectedFile = File(bankUri!!.path.toString())
                    } else {
                        selectedFile = File(slipUri!!.path.toString())
                    }
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
                            uploadFileWithProgress(
                                it,
                                false,
                                selectedFilePath.substring(selectedFilePath.lastIndexOf("/") + 1)
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
        selectedFilePath: String, isExternalDoc: Boolean, fileName: String
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
        val userId: RequestBody =
            (activity as BaseActivity).userDetails.userId!!.toRequestBody("text/plain".toMediaTypeOrNull())

        val call: retrofit2.Call<FileUploadResponse?>? =
            fileBody.apiService.uploadFile(fileToUpload, apiKey, userId)
        call?.enqueue(object : Callback<FileUploadResponse?> {
            override fun onResponse(
                call: retrofit2.Call<FileUploadResponse?>, response: Response<FileUploadResponse?>
            ) {
                if (response.body() != null && response.body()!!.status) {
                    val fileUploadResponse = response.body()
                    if (fileUploadResponse != null && fileUploadResponse.status) {
                        updateSelectedFilePath(
                            fileUploadResponse.fileUrl, isExternalDoc, fileName, selectedFilePath
                        )
                    }
                }
                CNProgressDialog.hideProgressDialog()
            }

            override fun onFailure(call: retrofit2.Call<FileUploadResponse?>, t: Throwable) {
                Log.e("imageupload", t.toString())
            }
        })
    }

    private fun updateSelectedFilePath(
        fileUrl: String, isExternalDoc: Boolean, fileName: String?, selectedFilePath: String
    ) {
        try {
            val textView: CNTextView? = null
            fileUploadAjaxRequest = FileUploadAjaxRequest()
            fileUploadAjaxRequest!!.userId = (activity as BaseActivity).userId
            var fileUrls: String? = ""
            fileUrls = fileUrl

            if (isBank) {
                uploadDocuments!!.bankStmt = fileUrl
                uploadDocuments!!.isBankStmtExt = isExternalDoc
                fileUploadAjaxRequest!!.urlsFor = Constants.FileUploadAjaxCallKeys.BANK_STATEMENTS
                if (uploadDocuments!!.bankStmt2 != null && uploadDocuments!!.bankStmt2 != "") fileUrls =
                    fileUrls + "," + uploadDocuments!!.bankStmt2
                if (uploadDocuments!!.bankStmt3 != null && uploadDocuments!!.bankStmt3 != "") fileUrls =
                    fileUrls + "," + uploadDocuments!!.bankStmt3

                statementsUrl = fileUrls
                isBankSelected = true
                binding.ivFrameBank.visibility = VISIBLE
                ivBank.visibility = GONE
                binding.ivBankImg.setImageResource(com.capitalnowapp.mobile.R.drawable.ic_upload_doc_pdf_new)
            } else {/* uploadDocuments!!.salSlip = fileUrl
                 uploadDocuments!!.isSalSlipExt = isExternalDoc
                 fileUploadAjaxRequest!!.urlsFor = Constants.FileUploadAjaxCallKeys.SAL_SLIP

                 isSlipSelected = true
                 binding.ivFrameSlip.visibility = VISIBLE
                 binding.ivSlip.visibility = GONE
                 ivSlipImg.setImageURI(Uri.parse(selectedFilePath))*/
            }
            fileUploadAjaxRequest!!.fileUrls = fileUrls
            updateFileAjaxCall()

            var strName = ""
            strName = if (fileName?.length!! > 20) {
                fileName.substring(0, 10) + "..."
            } else {
                fileName
            }
            if (textView != null) {
                textView.text = strName
                textView.setCompoundDrawablesRelativeWithIntrinsicBounds(
                    0, 0, com.capitalnowapp.mobile.R.drawable.ic_remove_image, 0
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

    private fun showCrop(
        selectedFile: String, googlePhotosUri: Boolean, fileName: String, selectedDocumentUri: Uri
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
            while (o.outWidth / scale / 2 >= REQUIRED_SIZE && o.outHeight / scale / 2 >= REQUIRED_SIZE) {
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
    override fun onVolleyErrorResponse(error: VolleyError?) {
        if (CNProgressDialog.isProgressDialogShown) CNProgressDialog.hideProgressDialog()
        CNAlertDialog.showAlertDialog(
            activityContext,
            resources.getString(R.string.title_error),
            resources.getString(R.string.error_failure)
        )
    }

    open fun logoutUser(user_id: String?, message: String?) {

        CNProgressDialog.hideProgressDialog()
        CNAlertDialog()
        CNAlertDialog.setRequestCode(Constants.ALERT_DIALOG_REQUEST_CODE_COMMON)
        CNAlertDialog.showMaterialAlertDialog(
            this,
            "",
            message,
            R.drawable.loan_hold_icon,
            false,
            R.color.pop_up_color
        )

        CNAlertDialog.setListener(object : AlertDialogSelectionListener {
            override fun alertDialogCallback() {
                val intent = Intent(activityContext, DashboardActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                overridePendingTransition(R.anim.right_in, R.anim.left_out)
                finish()
            }

            override fun alertDialogCallback(buttonType: Constants.ButtonType, requestCode: Int) {}
        })

        sharedPreferences = CNSharedPreferences(this)

        val ud = Gson().fromJson(
            sharedPreferences.getString(Constants.USER_DETAILS_DATA),
            UserDetails::class.java
        )

        if (ud != null) {
            ud.userId = user_id
            ud.userStatusId = "23"
        }

        sharedPreferences.putString(Constants.USER_DETAILS_DATA, Gson().toJson(ud))
        userDetails = ud;
    }
}