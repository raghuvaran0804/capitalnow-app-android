package com.capitalnowapp.mobile.kotlin.fragments


import android.Manifest
import android.annotation.SuppressLint
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
import android.os.*
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import com.capitalnowapp.mobile.BuildConfig
import com.capitalnowapp.mobile.R
import com.capitalnowapp.mobile.R.layout
import com.capitalnowapp.mobile.activities.BaseActivity
import com.capitalnowapp.mobile.beans.UploadDocuments
import com.capitalnowapp.mobile.beans.UserTermsData
import com.capitalnowapp.mobile.constants.Constants
import com.capitalnowapp.mobile.customviews.CNButton
import com.capitalnowapp.mobile.customviews.CNProgressDialog
import com.capitalnowapp.mobile.customviews.CNTextView
import com.capitalnowapp.mobile.databinding.ActivitySignature2Binding
import com.capitalnowapp.mobile.kotlin.activities.CropActivity
import com.capitalnowapp.mobile.kotlin.activities.DashboardActivity
import com.capitalnowapp.mobile.kotlin.utils.FileUtils
import com.capitalnowapp.mobile.models.FileUploadAjaxRequest
import com.capitalnowapp.mobile.models.FileUploadResponse
import com.capitalnowapp.mobile.models.GenericResponse
import com.capitalnowapp.mobile.models.SubmitInitialDocsReq
import com.capitalnowapp.mobile.retrofit.GenericAPIService
import com.capitalnowapp.mobile.retrofit.ProgressAPIService
import com.capitalnowapp.mobile.util.RealPathUtil
import com.capitalnowapp.mobile.util.TrackingUtil
import com.capitalnowapp.mobile.util.Utility
import com.google.gson.Gson
import com.theartofdev.edmodo.cropper.CropImage
import com.williamww.silkysignature.views.SignaturePad.*
import kotlinx.android.synthetic.main.activity_signature2.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Callback
import retrofit2.Response
import java.io.*
import java.lang.ref.WeakReference
import java.text.SimpleDateFormat
import java.util.*


class SignatureConsentFragment : Fragment() {
    companion object {
        private const val CAMERA_PERMISSION_CODE = 100
    }

    private var tempFile: File? = null
    private val REQUEST_IMAGE_CAPTURE: Int = 102
    private var signUri: Uri? = null
    private lateinit var binding: ActivitySignature2Binding
    private var cropFileName: String? = null
    private var cropGooglePhotosUri: Boolean? = false
    private var isSignSelected: Boolean = false
    private var isSignPadSelected: Boolean = false
    private var isSignUploaded: Boolean = false
    private var uploadDocuments: UploadDocuments? = null
    private var fileUploadAjaxRequest: FileUploadAjaxRequest? = null
    private var userTermsData: UserTermsData? = null
    private var fromMissingBankDetails: String? = ""

    @SuppressLint("NotConstructor")
    fun SignatureConsentFragment() {
        // empty constructor
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = ActivitySignature2Binding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val obj = JSONObject()
        try {
            obj.put("cnid", (activity as BaseActivity).userDetails.qcId)
        } catch (e: JSONException) {
            throw RuntimeException(e)
        }
        TrackingUtil.pushEvent(obj, getString(R.string.add_signature_page_landed))

        super.onViewCreated(view, savedInstanceState)

        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q) {
            binding.llSignPad.visibility = GONE
        } else {
            binding.llSignPad.visibility = VISIBLE
        }

        if (arguments != null) {
            userTermsData = requireArguments().getSerializable("userTermsData") as UserTermsData
            fromMissingBankDetails = requireArguments().getString("fromMissingBankDetails")
        }


        binding.tvBack.setOnClickListener {
            /*val intent = Intent(context, DashboardActivity::class.java)
                intent.flags =
                    Intent.FLAG_ACTIVITY_NO_ANIMATION or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)*/
            if ((activity as BaseActivity).sharedPreferences.getBoolean("fromDocs")) {
                (activity as BaseActivity).sharedPreferences.putBoolean("fromDocs", false)
                (activity as DashboardActivity).onBackPressed()
            } else {
                val intent = Intent(context, DashboardActivity::class.java)
                intent.flags =
                    Intent.FLAG_ACTIVITY_NO_ANIMATION or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            }
        }
        uploadDocuments = UploadDocuments((activity as BaseActivity).userId)
        signature_pad.setOnSignedListener(object : OnSignedListener {
            override fun onStartSigning() {}
            override fun onSigned() {

                binding.tvClear.isEnabled = true
                binding.tvPreview.isEnabled = true
                binding.tvValidate.isEnabled = true
                binding.ivSign.isEnabled = false
                isSignPadSelected = true
                binding.tvSave.setBackgroundColor(tvSave.context.resources.getColor(R.color.Primary2))
                binding.tvSave.setTextColor(tvSave.context.resources.getColor(R.color.color_primary))
            }

            override fun onClear() {
                binding.tvClear.isEnabled = false
                binding.tvPreview.isEnabled = false
                binding.tvValidate.isEnabled = false
                isSignPadSelected = false
                isSignUploaded = false
            }
        })
        binding.tvClear.setOnClickListener {

            val obj = JSONObject()
            try {
                obj.put("cnid", (activity as BaseActivity).userDetails.qcId)
                obj.put(getString(R.string.interaction_type), "Clear Signature Clicked")
            } catch (e: JSONException) {
                throw RuntimeException(e)
            }
            TrackingUtil.pushEvent(obj, getString(R.string.add_signature_page_interacted))

            signature_pad.clear()
            binding.tvSave.setBackgroundResource(R.drawable.button_border_5dp)
            binding.tvSave.setTextColor(tvSave.context.resources.getColor(R.color.black))

            ivSign.isEnabled = true
        }
        binding.tvPreview.setOnClickListener {

            val obj = JSONObject()
            try {
                obj.put("cnid", (activity as BaseActivity).userDetails.qcId)
                obj.put(getString(R.string.interaction_type), "Preview Clicked")
            } catch (e: JSONException) {
                throw RuntimeException(e)
            }
            TrackingUtil.pushEvent(obj, getString(R.string.add_signature_page_interacted))

            if (signature_pad.signatureBitmap != null) {
                previewImage()
            }
        }
        binding.tvSave.setOnClickListener {

            val obj = JSONObject()
            try {
                obj.put("cnid", (activity as BaseActivity).userDetails.qcId)
                obj.put(getString(R.string.interaction_type), "Save Clicked")
            } catch (e: JSONException) {
                throw RuntimeException(e)
            }
            TrackingUtil.pushEvent(obj, getString(R.string.add_signature_page_interacted))

            if (signature_pad.signatureBitmap != null) {
                isSignPadSelected
                saveImage()

            }
        }
        binding.ivSign.setOnClickListener {

            val obj = JSONObject()
            try {
                obj.put("cnid", (activity as BaseActivity).userDetails.qcId)
                obj.put(getString(R.string.interaction_type), "Upload Signature Clicked")
            } catch (e: JSONException) {
                throw RuntimeException(e)
            }
            TrackingUtil.pushEvent(obj, getString(R.string.add_signature_page_interacted))

            checkPermission(
                Manifest.permission.CAMERA,
                CAMERA_PERMISSION_CODE
            )
            selectImage()
        }

        binding.ivCancelSign.setOnClickListener {
            binding.ivSign.visibility = VISIBLE
            binding.ivFrameSign.visibility = GONE
            signature_pad.isEnabled = true
            isSignSelected = false
            isSignUploaded = false
        }

        binding.tvValidate.setOnClickListener {

            val obj = JSONObject()
            try {
                obj.put("cnid", (activity as BaseActivity).userDetails.qcId)
                obj.put(getString(R.string.interaction_type), "Submit Signature Clicked")
            } catch (e: JSONException) {
                throw RuntimeException(e)
            }
            TrackingUtil.pushEvent(obj, getString(R.string.add_signature_page_interacted))

            if ((isSignSelected || isSignPadSelected) && binding.cbAgreeTerms.isChecked) {
                submitInitialDocs()
            } else {
                if (!isSignSelected && !isSignPadSelected) {
                    Toast.makeText(context, "Please save Signature", Toast.LENGTH_SHORT)
                        .show()
                } else if (!binding.cbAgreeTerms.isChecked) {
                    Toast.makeText(context, "Please Check the Consent", Toast.LENGTH_SHORT).show()
                }
            }
        }
        var startIndex: Int
        var endIndex: Int
        val ss: SpannableString?
        if (userTermsData?.message != null) {
            val words: List<String> = userTermsData?.findText!!.split("||")
            val links: List<String> = userTermsData?.replaceLinks!!.split("||")
            ss = SpannableString(userTermsData?.message)
            for (w in words.withIndex()) {
                val termsAndCondition: ClickableSpan = object : ClickableSpan() {
                    override fun onClick(textView: View) {
                        showTermsPolicyDialog(w.value, links[w.index])
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

                val scale = resources.displayMetrics.density
                binding.tvSignatureConfirm?.text = ss
                binding.tvSignatureConfirm?.movementMethod = LinkMovementMethod.getInstance()
            }
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

    private fun showTermsPolicyDialog(title: String, link: String) {
        val alert = AlertDialog.Builder(
            (activity as BaseActivity).activityContext,
            R.style.RulesAlertDialogStyle
        )
        val inflater: LayoutInflater = layoutInflater
        val dialogView: View = inflater.inflate(R.layout.dialog_terms_conditions, null)
        alert.setView(dialogView)
        val tvTitle: CNTextView = dialogView.findViewById(R.id.et_title)
        val tvBack: CNTextView = dialogView.findViewById(R.id.tvBack)
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
        tvBack.setOnClickListener { dialog.dismiss() }
        dialog.show()
    }

    private fun saveImage() {
        val photo = File(
            getAlbumStorageDir("SignaturePad"),
            String.format("Signature_%d.jpg", System.currentTimeMillis())
        )
        saveBitmapToJPG(binding.signaturePad.signatureBitmap, photo)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun submitInitialDocs() {
        try {
            CNProgressDialog.showProgressDialog(context, Constants.LOADING_MESSAGE)
            val genericAPIService = GenericAPIService(context)
            val submitInitialDocsReq = SubmitInitialDocsReq()
            submitInitialDocsReq.setUserId((activity as BaseActivity).userId)
            submitInitialDocsReq.signature_consent = "1"
            submitInitialDocsReq.customersignature = uploadDocuments!!.customersignature
            submitInitialDocsReq.deviceUniqueId = Utility.getInstance().getDeviceUniqueId(activity)
            Log.d("docs_req", Gson().toJson(submitInitialDocsReq))
            val token = (activity as BaseActivity).userToken
            genericAPIService.submitInitialDocs(submitInitialDocsReq, token)
            genericAPIService.setOnDataListener { responseBody ->
                val genericResponse = Gson().fromJson(responseBody, GenericResponse::class.java)
                if (genericResponse != null && genericResponse.status) {
                    CNProgressDialog.hideProgressDialog()
                    if ((activity as BaseActivity).sharedPreferences != null && (activity as BaseActivity).sharedPreferences.getBoolean("fromDocs")) {
                        (activity as BaseActivity).sharedPreferences.putBoolean("fromDocs", false)
                        (activity as DashboardActivity).onBackPressed()
                    } else {
                        (activity as DashboardActivity).getApplyLoanData(true)
                        if (genericResponse.isReferenceRedirection) {

                                CNProgressDialog.hideProgressDialog()
                                (activity as DashboardActivity).isFromApply = false
                                (activity as DashboardActivity).selectedTab =
                                    (activity as DashboardActivity).getString(R.string.add_references)
                                (activity as DashboardActivity).navMenuAdapter.setSelectedTab((activity as DashboardActivity).selectedTab!!)
                            //adgydeCounting((activity as DashboardActivity).getString(R.string.upload_document_first_time_user))
                        } else {
                            CNProgressDialog.hideProgressDialog()
                            (activity as DashboardActivity).selectedTab =
                                (activity as DashboardActivity).getString(R.string.home)
                            (activity as DashboardActivity).navMenuAdapter.setSelectedTab((activity as DashboardActivity).selectedTab!!)
                            (activity as DashboardActivity).isFromApply = false
                        }
                    }
                } else {
                    CNProgressDialog.hideProgressDialog()
                    assert(genericResponse != null)
                    Toast.makeText(context, genericResponse!!.message, Toast.LENGTH_SHORT).show()
                }
            }
            genericAPIService.setOnErrorListener {
                CNProgressDialog.hideProgressDialog()
                Toast.makeText(
                    context,
                    context?.getString(R.string.error_failure),
                    Toast.LENGTH_SHORT
                ).show()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @Throws(IOException::class)
    fun saveBitmapToJPG(bitmap: Bitmap, file: File?) {

        val resized = Bitmap.createScaledBitmap(bitmap, 300, 150, true)
        val stream: OutputStream = FileOutputStream(file)
        resized.compress(Bitmap.CompressFormat.PNG, 80, stream)
        stream.close()

        isSignPadSelected = true
        isSignSelected = false
        uploadFileWithProgress(file!!.absolutePath, false, file.name)
    }

    fun getAlbumStorageDir(albumName: String?): File? {
        // Get the directory for the user's public pictures directory.
        val file = File(
            Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES
            ), albumName
        )
        if (!file.mkdirs()) {
            Log.e("SignaturePad", "Directory not created")
        }
        return file
    }

    private fun selectImage() {
        try {
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
                        signUri = Uri.fromFile(photoFile)
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
    fun createFileFromContentUri(fileUri: Uri): File {

        var fileName: String = ""

        fileUri.let { returnUri ->
            requireActivity().contentResolver.query(returnUri, null, null, null)
        }?.use { cursor ->
            val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            cursor.moveToFirst()
            fileName = cursor.getString(nameIndex)
        }

        //  For extract file mimeType
        val fileType: String? = fileUri.let { returnUri ->
            requireActivity().contentResolver.getType(returnUri)
        }

        val iStream: InputStream = requireActivity().contentResolver.openInputStream(fileUri)!!
        val outputDir: File = context?.cacheDir!!
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
                            }
                            val selectedFile = File(selectedFilePath)

                            val selectedFileSizeInMB = selectedFile.length() / (1024 * 1024)
                            if (selectedFileSizeInMB > Constants.FILE_UPLOAD_LIMIT) {
                                (activity as BaseActivity).displayToast(
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
                            (activity as BaseActivity).displayToast(getString(R.string.upload_docs_format_validation_msg))
                        }
                    }
                } catch (e: java.lang.Exception) {
                    e.printStackTrace()
                }
            } else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
                try {
                    val result = intent?.getStringExtra("crop")
                    if (resultCode == Activity.RESULT_OK) {
                        isSignPadSelected = false
                        isSignSelected = true
                        uploadFileWithProgress(result!!, cropGooglePhotosUri!!, cropFileName!!)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            } else if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
                if (signUri != null) {
                    val selectedFile = File(signUri!!.path.toString())
                    val reducedFile = saveBitmapToFile(selectedFile)
                    val selectedFilePath = reducedFile!!.path
                    val selectedFileSizeInMB = reducedFile.length() / (1024 * 1024)
                    if (selectedFileSizeInMB > Constants.FILE_UPLOAD_LIMIT) {
                        (activity as DashboardActivity).displayToast(
                            String.format(
                                "Uploading file size must be less than %d MB.",
                                Constants.FILE_UPLOAD_LIMIT
                            )
                        )
                        return
                    } else {
                        selectedFilePath.let {
                            showCrop(it, false, selectedFilePath, signUri!!)
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
        Log.d("selectedFilePath", selectedFilePath);
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
        val token = (activity as BaseActivity).userToken
        val apiKey = RequestBody.create("text/plain".toMediaTypeOrNull(), token)
        val userId: RequestBody = RequestBody.create(
            "text/plain".toMediaTypeOrNull(), (activity as DashboardActivity).userDetails.userId!!
        )

        val call: retrofit2.Call<FileUploadResponse?>? =
            fileBody.apiService.uploadFile(fileToUpload, apiKey, userId)
        call?.enqueue(object : Callback<FileUploadResponse?> {
            @RequiresApi(Build.VERSION_CODES.M)
            override fun onResponse(
                call: retrofit2.Call<FileUploadResponse?>,
                response: Response<FileUploadResponse?>
            ) {
                if (response.body() != null && response.body()!!.status) {
                    val fileUploadResponse = response.body()
                    if (fileUploadResponse != null && fileUploadResponse.status) {
                        updateSelectedFilePath(
                            fileUploadResponse.fileUrl,
                            isExternalDoc,
                            fileName,
                            selectedFilePath
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

    @RequiresApi(Build.VERSION_CODES.M)
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

            uploadDocuments!!.customersignature = fileUrl
            //uploadDocuments!!.isAddressProofExt = isExternalDoc
            fileUploadAjaxRequest!!.urlsFor = Constants.FileUploadAjaxCallKeys.SIGNATURE

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
                    0,
                    0,
                    R.drawable.ic_remove_image,
                    0
                )
            }
            isSignUploaded = true
            if (isSignSelected) {
                val uri = Uri.parse(File(selectedFilePath).absolutePath)
                ivImgSign.setImageURI(uri)
                ivFrameSign.visibility = VISIBLE
                ivSign.visibility = GONE
                signature_pad.isEnabled = false
            }

        } catch (e: java.lang.Exception) {
            isSignUploaded = false
            e.printStackTrace()
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun updateFileAjaxCall() {
        val genericAPIService = GenericAPIService(context)
        fileUploadAjaxRequest?.deviceUniqueId = Utility.getInstance().getDeviceUniqueId(activity)
        Log.d("file ajax", Gson().toJson(fileUploadAjaxRequest))
        val token = (activity as BaseActivity).userToken
        genericAPIService.uploadFileToServer(fileUploadAjaxRequest, token)
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


    private fun previewImage() {
        val alertCustomDialog =
            LayoutInflater.from(activity as DashboardActivity).inflate(layout.image_alert, null)
        val alert = android.app.AlertDialog.Builder(activity as DashboardActivity)
        alert.setView(alertCustomDialog)
        val dialog = alert.create()
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val ivSignatureImage = alertCustomDialog.findViewById<ImageView>(R.id.ivSignatureImage)
        ivSignatureImage.setImageBitmap(signature_pad.signatureBitmap)
        dialog.setCanceledOnTouchOutside(true)
        dialog.show()
    }
}


