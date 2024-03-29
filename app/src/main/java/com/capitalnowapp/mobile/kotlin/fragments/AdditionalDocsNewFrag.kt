package com.capitalnowapp.mobile.kotlin.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import com.capitalnowapp.mobile.customviews.CNProgressDialog
import com.capitalnowapp.mobile.customviews.CNTextView
import com.capitalnowapp.mobile.databinding.FragmentAdditionalDocsNewBinding
import com.capitalnowapp.mobile.kotlin.activities.DashboardActivity
import com.capitalnowapp.mobile.kotlin.utils.FileUtils
import com.capitalnowapp.mobile.models.FileUploadAjaxRequest
import com.capitalnowapp.mobile.models.FileUploadResponse
import com.capitalnowapp.mobile.models.GetAdditionalDocReq
import com.capitalnowapp.mobile.models.GetAdditionalDocResponse
import com.capitalnowapp.mobile.models.UploadAdditionalDocReq
import com.capitalnowapp.mobile.models.UploadAdditionalDocResponse
import com.capitalnowapp.mobile.retrofit.GenericAPIService
import com.capitalnowapp.mobile.retrofit.ProgressAPIService
import com.capitalnowapp.mobile.util.RealPathUtil
import com.capitalnowapp.mobile.util.TrackingUtil
import com.capitalnowapp.mobile.util.Utility
import com.google.gson.Gson
import com.theartofdev.edmodo.cropper.CropImage
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.json.JSONException
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

class AdditionalDocsNewFrag : Fragment() {

    companion object {
        private const val CAMERA_PERMISSION_CODE = 100
    }
    private var tempFile: File? = null
    private var proof1Url: String = ""
    private var proof2Url: String = ""
    private var current_operation: Int = 1
    private var mMakePhotoUri: Uri? = null
    private val REQUEST_IMAGE_CAPTURE = 102
    private var currenntPath = ""
    private var selectedPAPPath1: String? = ""
    private var selectedPAPPath2: String? = ""
    private var selectedSalSlipPath: String? = ""
    private var isSelectedPAPPath1: Boolean = false
    private var isSelectedPAPPath2: Boolean = false
    private var isSelectedSalSlipPath: Boolean = false
    private val PRESENT_ADDRESS_PROOF1_IMG = 1
    private val PRESENT_ADDRESS_PROOF2_IMG = 2
    private val LATEST_SALARY_SLIP_IMG = 3
    private var uploadAdditionalDocReq = UploadAdditionalDocReq()
    private var fileUploadAjaxRequest: FileUploadAjaxRequest? = null
    private var binding: FragmentAdditionalDocsNewBinding? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }
    @SuppressLint("NotConstructor")
    fun AdditionalDocsNewFrag(){
        // Required empty public constructor
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentAdditionalDocsNewBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    private fun initView() {
        try {
            val obj = JSONObject()
            try {
                obj.put("cnid", (activity as BaseActivity).userDetails.qcId)
            } catch (e: JSONException) {
                throw RuntimeException(e)
            }
            TrackingUtil.pushEvent(obj, getString(R.string.additional_documents_page_landed))

            getAdditionalDocuments()
            binding?.ivBack?.setOnClickListener {
                val intent = Intent(context, DashboardActivity::class.java)
                intent.flags =
                    Intent.FLAG_ACTIVITY_NO_ANIMATION or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            }
            binding?.ivFirst?.setOnClickListener {
                try {
                    checkPermission(
                        Manifest.permission.CAMERA, CAMERA_PERMISSION_CODE
                    )
                    checkPermission(
                        Manifest.permission.WRITE_EXTERNAL_STORAGE, CAMERA_PERMISSION_CODE
                    )
                    if (isSelectedPAPPath1) {
                        isSelectedPAPPath1 = false
                        binding?.ivFirst?.setImageDrawable(
                            ContextCompat.getDrawable(
                                requireContext(),
                                R.drawable.ic_upload_doc_up_new
                            )
                        )
                        proof1Url = ""
                    } else {
                        selectImage(PRESENT_ADDRESS_PROOF1_IMG)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            binding?.ivSecond?.setOnClickListener {
                try {
                    checkPermission(
                        Manifest.permission.CAMERA, CAMERA_PERMISSION_CODE
                    )
                    checkPermission(
                        Manifest.permission.WRITE_EXTERNAL_STORAGE, CAMERA_PERMISSION_CODE
                    )
                    if (isSelectedPAPPath2) {
                        isSelectedPAPPath2 = false
                        binding?.ivSecond?.setImageDrawable(
                            ContextCompat.getDrawable(
                                requireContext(),
                                R.drawable.ic_upload_doc_up_new
                            )
                        )
                        proof2Url = ""
                    } else {
                        selectImage(PRESENT_ADDRESS_PROOF2_IMG)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            binding?.ivSalSlip?.setOnClickListener {
                try {
                    checkPermission(
                        Manifest.permission.CAMERA, CAMERA_PERMISSION_CODE
                    )
                    checkPermission(
                        Manifest.permission.WRITE_EXTERNAL_STORAGE, CAMERA_PERMISSION_CODE
                    )
                    if (isSelectedSalSlipPath) {
                        isSelectedSalSlipPath = false
                        binding?.ivSalSlip?.setImageDrawable(
                            ContextCompat.getDrawable(
                                requireContext(),
                                R.drawable.ic_upload_doc_up_new
                            )
                        )
                        uploadAdditionalDocReq.latSalSlip = ""
                    } else {
                        selectImage(LATEST_SALARY_SLIP_IMG)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            binding?.tvUploadAdditionalDocs?.setOnClickListener {

                val obj = JSONObject()
                try {
                    obj.put("cnid", (activity as BaseActivity).userDetails.qcId)
                    obj.put(getString(R.string.interaction_type), "Upload Button Clicked")
                } catch (e: JSONException) {
                    throw RuntimeException(e)
                }
                TrackingUtil.pushEvent(obj, getString(R.string.additional_documents_page_interacted))

                if (validateAdditionalDocs()) {
                    uploadAdditionalDocs()
                } else {
                    //(activity as BaseActivity).displayToast(resources.getString(R.string.upload_docs_validation_msg_1))
                    Toast.makeText(
                        context,
                        resources.getString(R.string.upload_docs_validation_msg_1),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun validateAdditionalDocs(): Boolean {
        try {
            return proof1Url.trim().isNotEmpty() || proof2Url.trim()
                .isNotEmpty() || uploadAdditionalDocReq.latSalSlip?.trim()?.isNotEmpty()!!
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return false
    }

    private fun uploadAdditionalDocs() {
        try {
            val genericAPIService = GenericAPIService(activity, 0)
            val token = (activity as BaseActivity).userToken
            uploadAdditionalDocReq.latSalPass = binding?.etSlipPwd?.text.toString().trim()
            var url = ""
            if (proof1Url.isNotEmpty() && proof2Url.isNotEmpty()) {
                url = "$proof1Url,$proof2Url"
            } else if (proof1Url.isNotEmpty()) {
                url = proof1Url
            } else if (proof2Url.isNotEmpty()) {
                url = proof2Url
            }
            uploadAdditionalDocReq.preAddress = url
            genericAPIService.uploadAdditionalDocuments(uploadAdditionalDocReq, token)
            genericAPIService.setOnDataListener { responseBody ->
                CNProgressDialog.hideProgressDialog()
                val uploadAdditionalDocResponse =
                    Gson().fromJson(responseBody, UploadAdditionalDocResponse::class.java)
                if (uploadAdditionalDocResponse != null && uploadAdditionalDocResponse.status == true) {
                    //(activity as DashboardActivity).getApplyLoanData(true)
                    val intent = Intent(activity, DashboardActivity::class.java)
                    intent.flags =
                        Intent.FLAG_ACTIVITY_NO_ANIMATION or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                } else {
                    Toast.makeText(requireContext(), uploadAdditionalDocResponse.message, Toast.LENGTH_SHORT)
                        .show()
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
                requireActivity(),
                permission
            ) == PackageManager.PERMISSION_DENIED
        ) {

            // Requesting the permission
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(permission), requestCode)
        } else {
        }
    }

    private fun selectImage(flag: Int) {
        current_operation = flag
        val options = arrayOf<CharSequence>(
            resources.getString(R.string.take_photo),
            resources.getString(R.string.chooseFromGallery),
            resources.getString(R.string.cancel)
        )
        val builder = android.app.AlertDialog.Builder(activity)
        builder.setItems(options) { dialog, item ->
            if (options[item] == resources.getString(R.string.take_photo)) {
                val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                // Ensure that there's a camera activity to handle the intent
                if (takePictureIntent.resolveActivity((activity as BaseActivity).packageManager) != null) {
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
            currenntPath = image.absolutePath
        }
        // Save a file: path for use with ACTION_VIEW intents
        return image
    }

    private fun chooseFile(type: Int) {
        current_operation = type
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
        val intent = getFileChooserIntent()
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
        try {
            if (resultCode == Activity.RESULT_OK && requestCode == Constants.OPEN_DOCUMENT_REQUEST_CODE) {
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
                                    when (current_operation) {
                                        PRESENT_ADDRESS_PROOF1_IMG -> {
                                            selectedPAPPath1 = selectedFilePath
                                            isSelectedPAPPath1 = true
                                            if (isSelectedPAPPath1) {
                                                binding?.ivFirst?.setImageDrawable(
                                                    ContextCompat.getDrawable(
                                                        requireContext(),
                                                        R.drawable.ic_upload_doc_image_new
                                                    )
                                                )
                                            }
                                            uploadFileWithProgress(
                                                it,
                                                false,
                                                selectedPAPPath1!!.substring(
                                                    selectedPAPPath1!!.lastIndexOf(
                                                        "/"
                                                    ) + 1
                                                )
                                            )

                                        }

                                        PRESENT_ADDRESS_PROOF2_IMG -> {
                                            selectedPAPPath2 = selectedFilePath
                                            isSelectedPAPPath2 = true
                                            if (isSelectedPAPPath2) {
                                                binding?.ivSecond?.setImageDrawable(
                                                    ContextCompat.getDrawable(
                                                        requireContext(),
                                                        R.drawable.ic_upload_doc_image_new
                                                    )
                                                )
                                            }
                                            uploadFileWithProgress(
                                                it,
                                                false,
                                                selectedPAPPath2!!.substring(
                                                    selectedPAPPath2!!.lastIndexOf(
                                                        "/"
                                                    ) + 1
                                                )
                                            )
                                        }

                                        LATEST_SALARY_SLIP_IMG -> {
                                            selectedSalSlipPath = selectedFilePath
                                            isSelectedSalSlipPath = true
                                            if (isSelectedSalSlipPath) {
                                                binding?.ivSalSlip?.setImageDrawable(
                                                    ContextCompat.getDrawable(
                                                        requireContext(),
                                                        R.drawable.ic_upload_doc_image_new
                                                    )
                                                )
                                            }
                                            uploadFileWithProgress(
                                                it,
                                                false,
                                                selectedSalSlipPath!!.substring(
                                                    selectedSalSlipPath!!.lastIndexOf(
                                                        "/"
                                                    ) + 1
                                                )
                                            )
                                        }

                                    }
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
                    val result = data?.getStringExtra("crop")
                    if (resultCode == Activity.RESULT_OK) {
                        when (current_operation) {
                            PRESENT_ADDRESS_PROOF1_IMG -> {
                                selectedPAPPath1 = result
                                isSelectedPAPPath1 = true
                                if (isSelectedPAPPath1) {
                                    binding?.ivFirst?.setImageDrawable(
                                        ContextCompat.getDrawable(
                                            requireContext(),
                                            R.drawable.ic_upload_doc_image_new
                                        )
                                    )
                                }
                            }

                            PRESENT_ADDRESS_PROOF2_IMG -> {
                                selectedPAPPath2 = result
                                isSelectedPAPPath2 = true
                                if (isSelectedPAPPath2) {
                                    binding?.ivSecond?.setImageDrawable(
                                        ContextCompat.getDrawable(
                                            requireContext(),
                                            R.drawable.ic_upload_doc_image_new
                                        )
                                    )
                                }
                            }

                            LATEST_SALARY_SLIP_IMG -> {
                                selectedSalSlipPath = result
                                isSelectedSalSlipPath = true
                                if (isSelectedSalSlipPath) {
                                    binding?.ivSalSlip?.setImageDrawable(
                                        ContextCompat.getDrawable(
                                            requireContext(),
                                            R.drawable.ic_upload_doc_image_new
                                        )
                                    )
                                }
                            }


                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            } else if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
                if (mMakePhotoUri != null) {
                    val selectedFile = File(mMakePhotoUri!!.path.toString())
                    val reducedFile = saveBitmapToFile(selectedFile)
                    val selectedFilePath = reducedFile!!.path
                    val selectedFileSizeInMB = reducedFile.length() / (1024 * 1024)
                    if (selectedFileSizeInMB > Constants.TWOMB_FILE_UPLOAD_LIMIT) {
                        (activity as BaseActivity).displayToast(
                            String.format(
                                "Uploading file size must be less than %d MB.",
                                Constants.TWOMB_FILE_UPLOAD_LIMIT
                            )
                        )
                        return
                    } else {
                        selectedFilePath.let {
                            when (current_operation) {
                                PRESENT_ADDRESS_PROOF1_IMG -> {
                                    selectedPAPPath1 = selectedFilePath
                                    isSelectedPAPPath1 = true
                                    if (isSelectedPAPPath1) {
                                        binding?.ivFirst?.setImageDrawable(
                                            ContextCompat.getDrawable(
                                                requireContext(),
                                                R.drawable.ic_upload_doc_image_new
                                            )
                                        )
                                    }
                                    uploadFileWithProgress(
                                        it,
                                        false,
                                        selectedPAPPath1!!.substring(
                                            selectedPAPPath1!!.lastIndexOf(
                                                "/"
                                            ) + 1
                                        )
                                    )

                                }

                                PRESENT_ADDRESS_PROOF2_IMG -> {
                                    selectedPAPPath2 = selectedFilePath
                                    isSelectedPAPPath2 = true
                                    if (isSelectedPAPPath2) {
                                        binding?.ivSecond?.setImageDrawable(
                                            ContextCompat.getDrawable(
                                                requireContext(),
                                                R.drawable.ic_upload_doc_image_new
                                            )
                                        )
                                    }
                                    uploadFileWithProgress(
                                        it,
                                        false,
                                        selectedPAPPath2!!.substring(
                                            selectedPAPPath2!!.lastIndexOf(
                                                "/"
                                            ) + 1
                                        )
                                    )
                                }

                                LATEST_SALARY_SLIP_IMG -> {
                                    selectedSalSlipPath = selectedFilePath
                                    isSelectedSalSlipPath = true
                                    if (isSelectedSalSlipPath) {
                                        binding?.ivSalSlip?.setImageDrawable(
                                            ContextCompat.getDrawable(
                                                requireContext(),
                                                R.drawable.ic_upload_doc_image_new
                                            )
                                        )
                                    }
                                    uploadFileWithProgress(
                                        it,
                                        false,
                                        selectedSalSlipPath!!.substring(
                                            selectedSalSlipPath!!.lastIndexOf(
                                                "/"
                                            ) + 1
                                        )
                                    )
                                }
                            }
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
        val token = (activity as BaseActivity).userToken
        val apiKey = RequestBody.create("text/plain".toMediaTypeOrNull(), token)
        val userId: RequestBody = RequestBody.create(
            "text/plain".toMediaTypeOrNull(), (activity as DashboardActivity).userDetails.userId!!
        )

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
                        updateSelectedFilePath(fileUploadResponse.fileUrl, isExternalDoc, fileName)
                    }
                }
                if(tempFile != null) {
                    tempFile?.delete()
                }
                CNProgressDialog.hideProgressDialog()
            }

            override fun onFailure(call: retrofit2.Call<FileUploadResponse?>, t: Throwable) {
                Log.e("imageupload", t.toString())
            }
        })
    }

    private fun updateSelectedFilePath(fileUrl: String, isExternalDoc: Boolean, fileName: String?) {
        try {
            val textView: CNTextView? = null
            fileUploadAjaxRequest = FileUploadAjaxRequest()
            fileUploadAjaxRequest!!.userId = (activity as BaseActivity).userId
            var fileUrls: String? = ""
            fileUrls = fileUrl

            when (current_operation) {
                PRESENT_ADDRESS_PROOF1_IMG -> {
                    proof1Url = fileUrls;
                    if (proof2Url.isNotEmpty()) {
                        fileUrls = "$fileUrls,$proof2Url"
                    }
                    fileUploadAjaxRequest!!.urlsFor = Constants.FileUploadAjaxCallKeys.PRE_ADDRESS
                    isSelectedPAPPath1 = true
                }

                PRESENT_ADDRESS_PROOF2_IMG -> {
                    proof2Url = fileUrls;
                    if (proof1Url.isNotEmpty()) {
                        fileUrls = "$proof1Url,$fileUrls"
                    }
                    fileUploadAjaxRequest!!.urlsFor = Constants.FileUploadAjaxCallKeys.PRE_ADDRESS
                    isSelectedPAPPath2 = true

                }

                LATEST_SALARY_SLIP_IMG -> {
                    isSelectedSalSlipPath = true
                    fileUploadAjaxRequest!!.urlsFor =
                        Constants.FileUploadAjaxCallKeys.LATEST_SAL_SLIP
                    uploadAdditionalDocReq.latSalSlip = fileUrls
                }

            }

            fileUploadAjaxRequest!!.fileUrls = fileUrls
            //updateFileAjaxCall()

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
        val genericAPIService = GenericAPIService(context)
        fileUploadAjaxRequest?.deviceUniqueId = Utility.getInstance().getDeviceUniqueId(activity)
        Log.d("file ajax", Gson().toJson(fileUploadAjaxRequest))
        val token = (activity as BaseActivity).userToken
        genericAPIService.uploadFileToServer(fileUploadAjaxRequest, token)
    }

    private fun getAdditionalDocuments() {
        try {
            val genericAPIService = GenericAPIService(activity, 0)
            val getAdditionalDocReq = GetAdditionalDocReq()
            val token = (activity as BaseActivity).userToken
            genericAPIService.getAdditionalDocuments(getAdditionalDocReq, token)
            genericAPIService.setOnDataListener { responseBody ->
                CNProgressDialog.hideProgressDialog()
                val getAdditionalDocResponse =
                    Gson().fromJson(responseBody, GetAdditionalDocResponse::class.java)
                if (getAdditionalDocResponse != null && getAdditionalDocResponse.status == true) {
                    setAdditionalDocData(getAdditionalDocResponse)
                } else {
                    Toast.makeText(context, getAdditionalDocResponse.message, Toast.LENGTH_SHORT)
                        .show()
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

    private fun setAdditionalDocData(additionalDocResponse: GetAdditionalDocResponse?) {
        try {
            binding?.tvTitle?.text = additionalDocResponse?.data?.title
            val requiredDocs = additionalDocResponse?.data?.requiredDocuments
            for (additionalDocs in requiredDocs!!.withIndex()) {
                if (additionalDocs.value.key.equals("pre_address")) {
                    binding?.llPresentAddress?.visibility = View.VISIBLE
                    binding?.tvPresentAddressTitle?.text = additionalDocs.value.label
                    binding?.tvPresentAddressText?.text = additionalDocs.value.subLabel.toString()
                }
                if (additionalDocs.value.key.equals("lat_sal_slip")) {
                    binding?.llLatestSalarySlip?.visibility = View.VISIBLE
                    binding?.tvLatestSalarySlip?.text = additionalDocs.value.label
                }
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}