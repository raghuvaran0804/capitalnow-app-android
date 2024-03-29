package com.capitalnowapp.mobile.kotlin.fragments


//import com.appsflyer.AppsFlyerLib
//import io.branch.referral.util.BranchEvent
import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Rect
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
import android.view.View.INVISIBLE
import android.view.ViewGroup
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
import com.capitalnowapp.mobile.beans.UploadDocuments
import com.capitalnowapp.mobile.constants.Constants
import com.capitalnowapp.mobile.customviews.CNAlertDialog
import com.capitalnowapp.mobile.customviews.CNProgressDialog
import com.capitalnowapp.mobile.customviews.CNTextView
import com.capitalnowapp.mobile.databinding.ActivityLatestDocumentsBinding
import com.capitalnowapp.mobile.kotlin.activities.DashboardActivity
import com.capitalnowapp.mobile.kotlin.activities.DocsGuidelinesActivity
import com.capitalnowapp.mobile.kotlin.activities.UploadBankDetailsActivity
import com.capitalnowapp.mobile.kotlin.utils.FileUtils
import com.capitalnowapp.mobile.models.CNModel
import com.capitalnowapp.mobile.models.FileUploadAjaxRequest
import com.capitalnowapp.mobile.retrofit.GenericAPIService
import com.capitalnowapp.mobile.util.RealPathUtil
import com.capitalnowapp.mobile.util.Utility
import kotlinx.android.synthetic.main.activity_latest_documents.*
import org.json.JSONObject
import java.io.*
import java.text.SimpleDateFormat
import java.util.*


class LatestDocumentsFrag : Fragment(), View.OnClickListener {

    companion object {
        private const val CAMERA_PERMISSION_CODE = 100
    }
    private var tempFile: File? = null
    private var textChanged: Boolean = false
    private var isBankUploaded: Int = 100
    private var isSlipUploaded: Int = 100
    private var binding: ActivityLatestDocumentsBinding? = null
    private var utility: Utility? = null
    private var uploadDocuments: UploadDocuments? = null

    private var fileUploadAjaxRequest: FileUploadAjaxRequest? = null

    private var current_operation: Int = 1
    private var isSlipSelected: Boolean = false
    private var isBank1Selected: Boolean = false
    private var isBank2Selected: Boolean = false
    private var isBank3Selected: Boolean = false
    private var isSignSelected: Boolean = false

    private var statementsUrl: String? = null

    private val CP_SAL_SLIP = 3
    private val CP_BANK_STMT = 4
    private val CP_BANK_STMT_2 = 6
    private val CP_BANK_STMT_3 = 7

    private var isStatementTwoAdded: Boolean = false
    private var isStatementThreeAdded: Boolean = false
    private var mMakePhotoUri: Uri? = null
    private val REQUEST_IMAGE_CAPTURE = 102
    private var currentPath = ""
    private var loansResponse = JSONObject()
    private var manualUpload = -1

    @SuppressLint("NotConstructor")
    fun LatestDocumentsFrag() {
        // Required empty public constructor
    }


    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        binding = ActivityLatestDocumentsBinding.inflate(inflater, container, false)
        return binding!!.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView(binding)
    }

    private fun initView(binding: ActivityLatestDocumentsBinding?) {
        (activity as BaseActivity).userId = (activity as BaseActivity).userDetails.userId
        (activity as BaseActivity).cnModel =
                CNModel(context, activity, Constants.RequestFrom.APPLY_LOAN)
        utility = Utility.getInstance()
        uploadDocuments = UploadDocuments((activity as BaseActivity).userId)

        if (arguments != null) {
            loansResponse = JSONObject(requireArguments().getString("loansResponse")!!)
            if (loansResponse.has("manual_upload")) {
                manualUpload = requireArguments().getInt("manual_upload")
            }

            if (loansResponse.has("bank_statement")) {
                isBankUploaded = loansResponse.getInt("bank_statement")
                Log.d("bank_statement value...", isBankUploaded.toString())
                llBankStatement.isEnabled = isBankUploaded != 0

                if (isBankUploaded == 0) {
                    llBankStatement.alpha = 0.4f
                    tvSelectBank.visibility = View.GONE
                }
            }

            if (loansResponse.has("sal_slip")) {
                isSlipUploaded = loansResponse.getInt("sal_slip")
                Log.d("sal_slip value...", isSlipUploaded.toString())
                llSalarySlip.isEnabled = isSlipUploaded != 0

                if (isSlipUploaded == 0) {
                    llSalarySlip.alpha = 0.4f
                    tvSelectSlip.visibility = View.GONE
                }
            }
        }

        llSalarySlip.setOnClickListener(this)
        ivSlip.setOnClickListener(this)
        llBankStatement.setOnClickListener(this)
        ivBank1.setOnClickListener(this)
        ivBank2.setOnClickListener(this)
        ivBank3.setOnClickListener(this)
        ivSubmit.setOnClickListener(this)

        etGuidelines.setOnClickListener(this)


        binding?.etSlipPwd?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun afterTextChanged(editable: Editable) {
                textChanged = true
            }
        })

        binding!!.etSlipPwd.viewTreeObserver.addOnGlobalLayoutListener {
            try {
                if (textChanged) {
                    val r = Rect()
                    binding.etSlipPwd.getWindowVisibleDisplayFrame(r)
                    if (binding.etSlipPwd.rootView.height - (r.bottom - r.top) > 500) { // if more than 100 pixels, its probably a keyboard...
                    } else {
                        if (binding.etSlipPwd.text.toString().trim().isNotEmpty()) {
                            uploadDocuments!!.salSlipPassword = binding.etSlipPwd.text.toString().toString()
                            uploadLatestDocs(uploadDocuments!!, false)
                        }
                    }
                    textChanged = false
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        binding?.toolbar?.tvToolbarTitle?.text = "Latest Documents"
        binding?.toolbar?.tvAction?.visibility = INVISIBLE
    }

    fun showAlertDialog(message: String?) {
        if (CNProgressDialog.isProgressDialogShown) CNProgressDialog.hideProgressDialog()
        CNAlertDialog.showAlertDialog(context, resources.getString(R.string.title_alert), message)
    }

    fun displayToast(message: String?) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    override fun onClick(view: View?) {
        try {
            when (view?.id) {
                R.id.etGuidelines -> {
                    val intent = Intent(activity, DocsGuidelinesActivity::class.java)
                    intent.putExtra(
                            "url",
                            "https://s3.ap-south-1.amazonaws.com/cdn.cn/guidlines_new_docs.png"
                    )
                    startActivity(intent)
                    activity?.overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up)
                }
                R.id.ivSlip -> {
                    if (isSlipSelected) {
                        isSlipSelected = false
                        ivSlip.setImageDrawable(
                                ContextCompat.getDrawable(
                                        requireContext(),
                                        R.drawable.ic_upload_doc_up
                                )
                        )
                    } else {
                        checkPermission(
                                Manifest.permission.CAMERA,
                                CAMERA_PERMISSION_CODE
                        )
                        selectImage(CP_SAL_SLIP)
                    }
                }
                R.id.llSalarySlip -> {
                    if (llSlipAttachments.visibility == View.VISIBLE) {
                        llSlipAttachments.visibility = View.GONE
                        setExpand(tvSelectSlip, true)
                    } else {
                        llSlipAttachments.visibility = View.VISIBLE
                        setExpand(tvSelectSlip, false)
                    }
                }
                R.id.llBankStatement -> {
                    if (manualUpload == 0) {
                        val intent = Intent(activity, UploadBankDetailsActivity::class.java)
                        intent.putExtra("referrer", Constants.FIN_BIT_REFERRER.Latest_Docs)
                        startActivity(intent)
                    } else {
                        if (llBankAttachments.visibility == View.VISIBLE) {
                            llBankAttachments.visibility = View.GONE
                            setExpand(tvSelectBank, true)
                        } else {
                            llBankAttachments.visibility = View.VISIBLE
                            setExpand(tvSelectBank, false)
                        }
                    }
                }
                R.id.ivBank1 -> {
                    if (isBank1Selected) {
                        isBank1Selected = false
                        ivBank1.setImageDrawable(
                                ContextCompat.getDrawable(
                                        requireContext(),
                                        R.drawable.ic_upload_doc_up
                                )
                        )
                    } else {
                        chooseFile(CP_BANK_STMT)
                    }
                }
                R.id.ivBank2 -> {
                    if (isBank2Selected) {
                        isBank2Selected = false
                        ivBank2.setImageDrawable(
                                ContextCompat.getDrawable(
                                        requireContext(),
                                        R.drawable.ic_upload_doc_up
                                )
                        )
                    } else {
                        chooseFile(CP_BANK_STMT_2)
                    }
                }
                R.id.ivBank3 -> {
                    if (isBank3Selected) {
                        isBank3Selected = false
                        ivBank3.setImageDrawable(
                                ContextCompat.getDrawable(
                                        requireContext(),
                                        R.drawable.ic_upload_doc_up
                                )
                        )
                    } else {
                        chooseFile(CP_BANK_STMT_3)
                    }
                }
                R.id.ivSubmit -> validateUploadPendingDocuments()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setExpand(image: ImageView?, b: Boolean) {
        if (b) {
            image?.setImageResource(R.drawable.ic_docs_expand)
        } else {
            image?.setImageResource(R.drawable.ic_docs_collapse)
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

    private fun selectImage(flag: Int) {
        current_operation = flag
        val options = arrayOf<CharSequence>(
                resources.getString(R.string.chooseFromGallery),
                resources.getString(R.string.cancel)
        )
        val builder = android.app.AlertDialog.Builder(activity)
        builder.setItems(options) { dialog, item ->
            if (options[item] == resources.getString(R.string.take_photo)) {
                val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                // Ensure that there's a camera activity to handle the intent
                if (takePictureIntent.resolveActivity((activity as DashboardActivity).packageManager) != null) {
                    // Create the File where the photo should go
                    var photoFile: File? = null
                    try {
                        photoFile = createImageFile()
                        mMakePhotoUri = Uri.fromFile(photoFile)
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
                            /*val bitmap = BitmapFactory.decodeFile(photoFile.path)
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 2, FileOutputStream(photoFile))
                            val uri = getImageUri((activity as DashboardActivity), bitmap)*/
                            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
                        }
                    } catch (e: java.lang.Exception) {
                        e.printStackTrace()
                    }
                }
            } else if (options[item] == resources.getString(R.string.chooseFromGallery)) {
                chooseFile(flag)
            } else if (options[item] == resources.getString(R.string.cancel)) {
                dialog.dismiss()
            }
        }

        val dialog = builder.create()
        dialog.show()
    }

    @Throws(IOException::class)

    open fun createImageFile(): File? {
        // Create an image file name
        val timeStamp: String =
                SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val imageFileName = "JPEG_temp$timeStamp"
        val storageDir =
                (activity as DashboardActivity).getExternalFilesDir(Environment.DIRECTORY_PICTURES)
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

    fun chooseFile(type: Int) {
        current_operation = type
        //openGallery(current_operation)
        openDocumentPicker(current_operation)
    }

    private fun openDocumentPicker(current_operation: Int) {
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

    private fun openGallery(current_operation: Int) {
        val intent =
                if (current_operation == CP_BANK_STMT || current_operation == CP_BANK_STMT_2 || current_operation == CP_BANK_STMT_3) {
                    getPDFChooserIntent()
                } else {
                    getFileChooserIntent()
                }

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

    private fun getFileChooserIntent(): Intent {
        val mimeTypes = arrayOf("image/*", "application/pdf")
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
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
                if (mMakePhotoUri != null) {
                    val selectedFile = File(mMakePhotoUri!!.path.toString())
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
                            updateSelectedFilePath(
                                    selectedFilePath,
                                    false,
                                    selectedFile.name
                            )
                        }
                    }
                }
            } else {
                try {
                    data?.data?.also { documentUri ->
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

                        //if (mimeType.contains("jpeg") || mimeType.contains("png") || mimeType.contains("pdf")) {
                        if ( fileType.equals(
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
                                                context,
                                                selectedDocumentUri
                                            )
                                        isGooglePhotosUri = true
                                    }
                                } catch (e: FileNotFoundException) {
                                    e.printStackTrace()
                                }
                            } else {
                                selectedFilePath =
                                    RealPathUtil.getPathFromUri(context, selectedDocumentUri)
                                if (selectedFilePath == null || selectedFilePath == "" || (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R)) {
                                    selectedFilePath = FileUtils.makeFileCopyInCacheDir(
                                        selectedDocumentUri,
                                        (activity as DashboardActivity)
                                    )
                                }
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
                                //selectedFilePath?.let { uploadFile(it, isGooglePhotosUri, selectedFilePath.substring(selectedFilePath.lastIndexOf("/") + 1)) }
                                updateSelectedFilePath(
                                    selectedFilePath!!,
                                    isGooglePhotosUri,
                                    selectedFile.name
                                )
                            }
                        } else {
                            displayToast("Please select PDF format file only.")
                        }
                    }
                } catch (e: java.lang.Exception) {
                    e.printStackTrace()
                }
            }
        }

    }

    private fun validateUploadPendingDocuments() {

        val salSlipPwd: String = binding?.etSlipPwd?.text.toString()
        val bankStatementPwd: String = binding?.etBankSlipPassword?.text.toString()

        uploadDocuments!!.salSlipPassword = salSlipPwd
        uploadDocuments!!.bankStmtPassword = bankStatementPwd


        if (isBankUploaded > 0 && isSlipUploaded > 0) {
            if (uploadDocuments!!.salSlip != null && uploadDocuments!!.salSlip != "") {
                if ((uploadDocuments!!.bankStmt != null && uploadDocuments!!.bankStmt != "")
                        || (uploadDocuments!!.bankStmt2 != null && uploadDocuments!!.bankStmt2 != "")
                        || (uploadDocuments!!.bankStmt3 != null && uploadDocuments!!.bankStmt3 != "")
                ) {
                    uploadLatestDocs(uploadDocuments!!, true)
                } else {
                    displayToast(getString(R.string.unique_validation_msg))
                }
            } else {
                displayToast(getString(R.string.select_salary_slip))
            }
        } else if (isBankUploaded > 0) {
            if ((uploadDocuments!!.bankStmt != null && uploadDocuments!!.bankStmt != "")
                    || (uploadDocuments!!.bankStmt2 != null && uploadDocuments!!.bankStmt2 != "")
                    || (uploadDocuments!!.bankStmt3 != null && uploadDocuments!!.bankStmt3 != "")
            ) {
                uploadLatestDocs(uploadDocuments!!, true)
            } else {
                displayToast(getString(R.string.unique_validation_msg))
            }
        } else if (isSlipUploaded > 0) {
            if (uploadDocuments!!.salSlip != null && uploadDocuments!!.salSlip != "") {
                uploadLatestDocs(uploadDocuments!!, true)
            } else {
                displayToast(getString(R.string.select_salary_slip))
            }
        }
    }

    private fun uploadLatestDocs(uploadDocuments: UploadDocuments, flag: Boolean) {
        if (flag) {
            CNProgressDialog.showProgressDialog(activity, Constants.LOADING_MESSAGE)
        }
        val genericAPIService = GenericAPIService(activity)
        val bankStatements = ArrayList<String>()

        if (uploadDocuments.bankStmt != null && uploadDocuments.bankStmt != "") {
            bankStatements.add(uploadDocuments.bankStmt)
        }
        if (uploadDocuments.bankStmt2 != null && uploadDocuments.bankStmt2 != "") {
            bankStatements.add(uploadDocuments.bankStmt2)
        }
        if (uploadDocuments.bankStmt3 != null && uploadDocuments.bankStmt3 != "") {
            bankStatements.add(uploadDocuments.bankStmt3)
        }
        val token = (activity as BaseActivity).userToken
        if (flag) {
            genericAPIService.uploadLatestDocs(
                    uploadDocuments.salSlip,
                    bankStatements,
                    uploadDocuments.salSlipPassword,
                    uploadDocuments.bankStmtPassword,
                    (activity as DashboardActivity).userDetails.userId,
                this,
                    Utility.getInstance().getDeviceUniqueId(activity),
                    token
            )
        } else {
            genericAPIService.saveLatestPayslip(
                    uploadDocuments.salSlip,
                    uploadDocuments.salSlipPassword,
                    (activity as DashboardActivity).userDetails.userId,
                    this,
                    Utility.getInstance().getDeviceUniqueId(activity),
                    token
            )
        }
        genericAPIService.setOnDataListener { responseBody ->
            CNProgressDialog.hideProgressDialog()
            if (flag) {
                updateFileUploadStatus(responseBody)
            }
        }
        genericAPIService.setOnErrorListener { CNProgressDialog.hideProgressDialog() }
    }

    private fun updateSelectedFilePath(fileUrl: String, isExternalDoc: Boolean, name: String) {
        try {
            var textView: CNTextView? = null
            fileUploadAjaxRequest = FileUploadAjaxRequest()
            fileUploadAjaxRequest!!.userId = (activity as DashboardActivity).userId
            var fileUrls: String? = ""
            fileUrls = fileUrl

            if (current_operation == CP_BANK_STMT) {
                uploadDocuments!!.bankStmt = fileUrl
                uploadDocuments!!.isBankStmtExt = isExternalDoc
                fileUploadAjaxRequest!!.urlsFor = Constants.FileUploadAjaxCallKeys.BANK_STATEMENTS
                if (uploadDocuments!!.bankStmt2 != null && uploadDocuments!!.bankStmt2 != "") fileUrls =
                        fileUrls + "," + uploadDocuments!!.bankStmt2
                if (uploadDocuments!!.bankStmt3 != null && uploadDocuments!!.bankStmt3 != "") fileUrls =
                        fileUrls + "," + uploadDocuments!!.bankStmt3
                statementsUrl = fileUrls
                isStatementTwoAdded = false
                isBank1Selected = true
                checkIsPdf(fileUrl, ivBank1)
            } else if (current_operation == CP_BANK_STMT_2) {
                uploadDocuments!!.bankStmt2 = fileUrl
                fileUploadAjaxRequest!!.urlsFor = Constants.FileUploadAjaxCallKeys.BANK_STATEMENTS
                if (uploadDocuments!!.bankStmt != null && uploadDocuments!!.bankStmt != "") fileUrls =
                        fileUrls + "," + uploadDocuments!!.bankStmt
                if (uploadDocuments!!.bankStmt3 != null && uploadDocuments!!.bankStmt3 != "") fileUrls =
                        fileUrls + "," + uploadDocuments!!.bankStmt3
                statementsUrl = fileUrls
                isStatementTwoAdded = true
                isBank2Selected = true
                checkIsPdf(fileUrl, ivBank2)
            } else if (current_operation == CP_BANK_STMT_3) {
                uploadDocuments!!.bankStmt3 = fileUrl
                fileUploadAjaxRequest!!.urlsFor = Constants.FileUploadAjaxCallKeys.BANK_STATEMENTS
                if (uploadDocuments!!.bankStmt2 != null && uploadDocuments!!.bankStmt2 != "") fileUrls =
                        fileUrls + "," + uploadDocuments!!.bankStmt2
                if (uploadDocuments!!.bankStmt != null && uploadDocuments!!.bankStmt != "") fileUrls =
                        fileUrls + "," + uploadDocuments!!.bankStmt
                statementsUrl = fileUrls
                isStatementThreeAdded = true
                isBank3Selected = true
                checkIsPdf(fileUrl, ivBank3)
            } else if (current_operation == CP_SAL_SLIP) {
                textView = binding?.tvSalarySlip
                uploadDocuments!!.salSlip = fileUrl
                uploadDocuments!!.isSalSlipExt = isExternalDoc
                fileUploadAjaxRequest!!.urlsFor = Constants.FileUploadAjaxCallKeys.SAL_SLIP
                isSlipSelected = true
                checkIsPdf(fileUrl, ivSlip)
            }
            fileUploadAjaxRequest!!.fileUrls = fileUrls

            if (current_operation == CP_SAL_SLIP) {
                uploadLatestDocs(uploadDocuments!!, false)
            }

            // textView!!.text = name
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    private fun checkIsPdf(fileUrl: String, image: ImageView?) {
        if (checkIsPdf(fileUrl)) {
            image?.setImageDrawable(
                    ContextCompat.getDrawable(
                            requireContext(),
                            R.drawable.ic_upload_doc_pdf
                    )
            )
        } else {
            image?.setImageDrawable(
                    ContextCompat.getDrawable(
                            requireContext(),
                            R.drawable.ic_upload_doc_image
                    )
            )
        }
    }

    private fun checkIsPdf(fileUrl: String): Boolean {
        if (fileUrl.uppercase(Locale.ROOT).contains(".PDF")) {
            return true
        }
        return false
    }

     @SuppressLint("SuspiciousIndentation")
     fun updateFileUploadStatus(response: String) {
        CNProgressDialog.hideProgressDialog()
        binding?.ivSubmit?.isEnabled = true
        binding?.ivSubmit?.isClickable = true
        var message = ""
        try {
            if (response.isNotEmpty()) {
                if (response == Constants.STATUS_FAILURE) {
                    message = resources.getString(R.string.documents_uploading_failed)
                    showAlertDialog(message)
                } else {
                    val jsonResponseObject = JSONObject(response)
                    val status = jsonResponseObject.getString("status")
                    if (jsonResponseObject.has("message")) message = jsonResponseObject.getString("message")
                    if (status == Constants.STATUS_SUCCESS || status == Constants.LIMIT_EXHAUSTED) {
                        /* val logInIntent = Intent(context, DashboardActivity::class.java)
                         logInIntent.flags = Intent.FLAG_ACTIVITY_NO_ANIMATION or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                         startActivity(logInIntent)*/
                        //adgydeCounting(getString(R.string.upload_document_existing_user))
                        if (jsonResponseObject.has("reference_redirection") && jsonResponseObject.getBoolean("reference_redirection")) {

                            val addReferencesFragment = ReferencesNewFragment()
                            if (jsonResponseObject.has("reference_notice")) {
                                val msg = jsonResponseObject.getString("reference_notice")
                                val bundle = Bundle()
                                bundle.putString("msg", msg)
                                addReferencesFragment.arguments = bundle
                            }
                            (activity as DashboardActivity).addReferencesFragment = addReferencesFragment
                                (activity as DashboardActivity).replaceFrag(
                                        addReferencesFragment,
                                        (activity as DashboardActivity).getString(R.string.add_references),
                                        null
                                )
                            (activity as DashboardActivity).isFromApply = false
                            (activity as DashboardActivity).selectedTab =
                                    (activity as DashboardActivity).getString(R.string.add_references)
                            (activity as DashboardActivity).navMenuAdapter.setSelectedTab((activity as DashboardActivity).selectedTab!!)
                        } else {
                            (activity as DashboardActivity).selectedTab =
                                    (activity as DashboardActivity).getString(R.string.home)
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

    /*open fun adgydeCounting(value: String) {
        val params = HashMap<String, Any>()
        val key = getString(R.string.upload_document_key)
        params[key] = value //patrametre name,value change to event
        AppsFlyerLib.getInstance().logEvent(context, key, params)

        val logger = AppEventsLogger.newLogger(this.context)
        val bundle = Bundle()
        bundle.putString(key, value)
        logger.logEvent(getString(R.string.upload_document_key), bundle)

        BranchEvent("UploadedLatestDocuments")
                .addCustomDataProperty("UploadedLatestDocuments", "Uploaded_Latest_Documents")
                .setCustomerEventAlias("Uploaded_Latest_Documents")
                .logEvent(activity as DashboardActivity)
    }*/
}