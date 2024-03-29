package com.capitalnowapp.mobile.kotlin.fragments

import android.Manifest
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
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
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
import com.capitalnowapp.mobile.databinding.FragmentContactUsNewBinding
import com.capitalnowapp.mobile.kotlin.activities.DashboardActivity
import com.capitalnowapp.mobile.kotlin.utils.FileUtils
import com.capitalnowapp.mobile.models.ContactUsQuery
import com.capitalnowapp.mobile.models.Contactus
import com.capitalnowapp.mobile.models.FileUploadAjaxRequest
import com.capitalnowapp.mobile.models.FileUploadResponse
import com.capitalnowapp.mobile.models.SubmitContactUsReq
import com.capitalnowapp.mobile.models.SubmitContactUsResponse
import com.capitalnowapp.mobile.retrofit.GenericAPIService
import com.capitalnowapp.mobile.util.RealPathUtil
import com.capitalnowapp.mobile.util.TrackingUtil
import com.capitalnowapp.mobile.util.Utility
import com.google.gson.Gson
import com.theartofdev.edmodo.cropper.CropImage
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

class ContactUsNewFragment : Fragment() {

    companion object {
        private const val CAMERA_PERMISSION_CODE = 100
    }
    private var tempFile: File? = null
    private var selectedFilePath3: String? = ""
    private var selectedFilePath2: String? = ""
    private var selectedFilePath1: String? = ""
    private var isSelectedFilePath1: Boolean = false
    private var isSelectedFilePath2: Boolean = false
    private var isSelectedFilePath3: Boolean = false
    private var selectedQuery: ContactUsQuery? = null
    private var selectedSubQuery: String? = null
    private var queryAdapter: ArrayAdapter<ContactUsQuery>? = null
    private var subQueryAdapter: ArrayAdapter<String>? = null
    private var binding: FragmentContactUsNewBinding? = null
    private var activity: Activity? = null
    private var contactUsData: Contactus? = null
    private var contactUsMessage: String? = ""
    private var current_operation: Int = 1
    private var currenntPath = ""
    private var mMakePhotoUri: Uri? = null
    private val REQUEST_IMAGE_CAPTURE = 102
    private val FRIST_IMG = 1
    private val SECOND_IMG = 2
    private val THIRD_IMG = 3
    private var cropGooglePhotosUri: Boolean? = false
    private var fileUploadAjaxRequest: FileUploadAjaxRequest? = null
    private var cropFileName: String? = null
    private var contactRequestCount: String? = null
    private var validationMsg = ""

    private val contactUsJson = "{\n" +
            "\t\"query\": [{\n" +
            "\t\t\t\"id\": 1,\n" +
            "\t\t\t\"title\": \"Payment Related\",\n" +
            "\t\t\t\"sub_title\": [\"I need an extension\",\n" +
            "\t\t\t\t\"I want to change my auto payment date\",\n" +
            "\t\t\t\t\"Getting too many repayment SMS/Email\",\n" +
            "\t\t\t\t\"Report fraud calls/sms\",\n" +
            "\t\t\t\t\"I need a settlement\",\n" +
            "\t\t\t\t\"Payment done but not reflecting in app\",\n" +
            "\t\t\t\t\"Netbanking not working in app\",\n" +
            "\t\t\t\t\"UPI not working in app\",\n" +
            "\t\t\t\t\"Unable to pay through app\",\n" +
            "\t\t\t\t\"Double payment done manual payment and auto debit\"\n" +
            "\t\t\t]\n" +
            "\t\t},\n" +
            "\t\t{\n" +
            "\t\t\t\"id\": 2,\n" +
            "\t\t\t\"title\": \"Profile Related\",\n" +
            "\t\t\t\"sub_title\": [\"Need to update bank account details\",\n" +
            "\t\t\t\t\"Need to update Registered mobile number/ E-mail ID\",\n" +
            "\t\t\t\t\"Need to update Salary / Salary slip upload\",\n" +
            "\t\t\t\t\"Why my profile is on hold\",\n" +
            "\t\t\t\t\"Account deactivation request\",\n" +
            "\t\t\t\t\"I have account activation request\",\n" +
            "\t\t\t\t\"I Need NOC against my loan closure\"\n" +
            "\t\t\t]\n" +
            "\t\t},\n" +
            "\t\t{\n" +
            "\t\t\t\"id\": 3,\n" +
            "\t\t\t\"title\": \"Techical issues\",\n" +
            "\t\t\t\"sub_title\": [\"Unable to login with OTP\",\n" +
            "\t\t\t\t\"Unable to upload PAN Card\",\n" +
            "\t\t\t\t\"Unable to upload Aadhar Card\",\n" +
            "\t\t\t\t\"Unable to repay my loan\",\n" +
            "\t\t\t\t\"Unable to complete Enach\",\n" +
            "\t\t\t\t\"App not responding\"\n" +
            "\t\t\t]\n" +
            "\t\t},\n" +
            "\t\t{\n" +
            "\t\t\t\"id\": 4,\n" +
            "\t\t\t\"title\": \"General Queries\",\n" +
            "\t\t\t\"sub_title\": [\"When will my loan be disbursed\",\n" +
            "\t\t\t\t\"Unsubscribe alerts/mails\",\n" +
            "\t\t\t\t\"How do i get access code\",\n" +
            "\t\t\t\t\"I have a limit increase request\",\n" +
            "\t\t\t\t\"Why should i sign Enach\",\n" +
            "\t\t\t\t\"I have sent my documents for loan processing\"\n" +
            "\t\t\t]\n" +
            "\t\t},\n" +
            "\t\t{\n" +
            "\t\t\t\"id\": 5,\n" +
            "\t\t\t\"title\": \"Cibil/Bureau status\",\n" +
            "\t\t\t\"sub_title\": [\"Enquiry on loan status updated at Credit Bureau\",\n" +
            "\t\t\t\t\"Incorrect loan account linked in CapitalNow\",\n" +
            "\t\t\t\t\"Need to change the loan status reported to Credit Bureau\",\n" +
            "\t\t\t\t\"Payment made but still showing as defaulter\"\n" +
            "\t\t\t]\n" +
            "\t\t}\n" +
            "\t]\n" +
            "}"


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentContactUsNewBinding.inflate(inflater, container, false)
        activity = getActivity()
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    private fun initView() {


        val obj = JSONObject()
        try {
            obj.put("cnid", (activity as BaseActivity).userDetails.qcId)
        } catch (e: JSONException) {
            throw RuntimeException(e)
        }
        TrackingUtil.pushEvent(obj, getString(R.string.contact_us_page_landed))


        //val listType: Type = object : TypeToken<Contactus>() {}.type
        contactUsData = Gson().fromJson(contactUsJson, Contactus::class.java)

        setToDefault()

        binding?.spinnerQuery?.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {


                    val qIndex = position - 1
                    if (qIndex >= 0) {
                        selectedQuery = contactUsData?.query?.get(qIndex)
                        setSubQueries(selectedQuery)
                    } else {
                        selectedQuery = null
                        setToDefault()
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {

                }

            }


        binding?.tvSubmit?.setOnClickListener {

            val obj = JSONObject()
            try {
                obj.put("cnid", (activity as BaseActivity).userDetails.qcId)
                obj.put(getString(R.string.interaction_type), "SUBMIT Button Clicked")
            } catch (e: JSONException) {
                throw RuntimeException(e)
            }
            TrackingUtil.pushEvent(obj, getString(R.string.contact_us_query_submitted))

            //SubmitContactUs()
            validateContactUs()

        }
        binding?.iv1?.setOnClickListener {
            try {
                checkPermission(
                    Manifest.permission.CAMERA, CAMERA_PERMISSION_CODE
                )
                checkPermission(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE, CAMERA_PERMISSION_CODE
                )
                if (isSelectedFilePath1) {
                    isSelectedFilePath1 = false
                    binding?.iv1?.setImageDrawable(
                        ContextCompat.getDrawable(
                            requireContext(),
                            R.drawable.ic_upload_doc_up
                        )
                    )
                } else {
                    selectImage(FRIST_IMG)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        binding?.iv2?.setOnClickListener {
            try {
                checkPermission(
                    Manifest.permission.CAMERA, CAMERA_PERMISSION_CODE
                )
                checkPermission(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE, CAMERA_PERMISSION_CODE
                )
                if (isSelectedFilePath2) {
                    isSelectedFilePath2 = false
                    binding?.iv2?.setImageDrawable(
                        ContextCompat.getDrawable(
                            requireContext(),
                            R.drawable.ic_upload_doc_up
                        )
                    )
                } else {
                    selectImage(SECOND_IMG)
                }

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        binding?.iv3?.setOnClickListener {
            try {
                checkPermission(
                    Manifest.permission.CAMERA, CAMERA_PERMISSION_CODE
                )
                checkPermission(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE, CAMERA_PERMISSION_CODE
                )

                if (isSelectedFilePath3) {
                    isSelectedFilePath3 = false
                    binding?.iv3?.setImageDrawable(
                        ContextCompat.getDrawable(
                            requireContext(),
                            R.drawable.ic_upload_doc_up
                        )
                    )
                } else {
                    selectImage(THIRD_IMG)
                }

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        binding?.etMessage?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                if (s?.length!! >= 50) {
                    contactUsMessage = s.toString().trim()
                }
            }

        })

        binding?.spinnerSubQuery?.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {


                    val qIndex = position - 1
                    if (qIndex >= 0) {
                        selectedSubQuery = selectedQuery?.subTitle?.get(qIndex)

                    } else {
                        selectedSubQuery = ""
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {

                }

            }

        setData()
    }

    private fun validateContactUs() {
        try {
            var count = 0
            if (binding?.etName?.text?.equals("")!!) {
                validationMsg = "Please Provide your name"
                count++
            }
            if (binding?.etMobile?.text?.equals("")!!) {
                validationMsg = "Please Provide your Mobile Number"
                count++
            }
            if (binding?.etEmail?.text?.equals("")!!) {
                validationMsg = "Please Provide your Email"
                count++
            }
            if (binding?.spinnerQuery?.selectedItem?.equals("")!! || binding?.spinnerQuery?.selectedItem?.equals("Select Query")!!) {
                validationMsg = "Please Select Query"
                count++
            }
            if (binding?.spinnerSubQuery?.selectedItem?.equals("")!! || binding?.spinnerSubQuery?.selectedItem?.equals("Select Sub Query")!!) {
                validationMsg = "Please Select Sub Query"
                count++
            }
            if (binding?.etMessage?.text?.length!! <= 50) {
                validationMsg = "Message Characters length should be more than 50"
                count++
            }
            if(count == 0){
                submitContactUsWeb()
            }
            else if (count > 1) {
                Toast.makeText(context, "Please fill all the fields to move forward", Toast.LENGTH_SHORT).show()
                count = 0
            } else {
                Toast.makeText(context, validationMsg, Toast.LENGTH_SHORT).show()
                count = 0
            }

        } catch (e: Exception) {
            e.printStackTrace()
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

    private fun chooseFile(type: Int) {
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

    private fun setData() {
        if (binding?.etName != null && !binding?.etName!!.equals("")) {
            binding?.etName?.setText((activity as BaseActivity).userDetails.fullName)
        } else {
            Toast.makeText(activity, "Please provide Full Name", Toast.LENGTH_SHORT).show()
        }
        if (binding?.etMobile != null && !binding?.etMobile!!.equals("")) {
            binding?.etMobile?.setText((activity as BaseActivity).userDetails.userMobile)
        } else {
            Toast.makeText(activity, "Please provide Mobile Number", Toast.LENGTH_SHORT).show()
        }
        if (binding?.etEmail != null && !binding?.etEmail!!.equals("")) {
            binding?.etEmail?.setText((activity as BaseActivity).userDetails.email)
        } else {
            Toast.makeText(activity, "Please provide Email", Toast.LENGTH_SHORT).show()
        }

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
                                        FRIST_IMG -> {
                                            selectedFilePath1 = selectedFilePath
                                            isSelectedFilePath1 = true
                                            if (isSelectedFilePath1) {
                                                binding?.iv1?.setImageDrawable(
                                                    ContextCompat.getDrawable(
                                                        requireContext(),
                                                        R.drawable.ic_upload_doc_image
                                                    )
                                                )
                                            }

                                        }

                                        SECOND_IMG -> {
                                            selectedFilePath2 = selectedFilePath
                                            isSelectedFilePath2 = true
                                            if (isSelectedFilePath2) {
                                                binding?.iv2?.setImageDrawable(
                                                    ContextCompat.getDrawable(
                                                        requireContext(),
                                                        R.drawable.ic_upload_doc_image
                                                    )
                                                )
                                            }
                                        }

                                        THIRD_IMG -> {
                                            selectedFilePath3 = selectedFilePath
                                            isSelectedFilePath3 = true
                                            if (isSelectedFilePath3) {
                                                binding?.iv3?.setImageDrawable(
                                                    ContextCompat.getDrawable(
                                                        requireContext(),
                                                        R.drawable.ic_upload_doc_image
                                                    )
                                                )
                                            }
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
                            FRIST_IMG -> {
                                selectedFilePath1 = result
                                isSelectedFilePath1 = true
                                if (isSelectedFilePath1) {
                                    binding?.iv1?.setImageDrawable(
                                        ContextCompat.getDrawable(
                                            requireContext(),
                                            R.drawable.ic_upload_doc_image
                                        )
                                    )
                                }
                            }

                            SECOND_IMG -> {
                                selectedFilePath2 = result
                                isSelectedFilePath2 = true
                                if (isSelectedFilePath2) {
                                    binding?.iv2?.setImageDrawable(
                                        ContextCompat.getDrawable(
                                            requireContext(),
                                            R.drawable.ic_upload_doc_image
                                        )
                                    )
                                }
                            }

                            THIRD_IMG -> {
                                selectedFilePath3 = result
                                isSelectedFilePath3 = true
                                if (isSelectedFilePath3) {
                                    binding?.iv3?.setImageDrawable(
                                        ContextCompat.getDrawable(
                                            requireContext(),
                                            R.drawable.ic_upload_doc_image
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
                                FRIST_IMG -> {
                                    selectedFilePath1 = selectedFilePath
                                    isSelectedFilePath1 = true
                                    if (isSelectedFilePath1) {
                                        binding?.iv1?.setImageDrawable(
                                            ContextCompat.getDrawable(
                                                requireContext(),
                                                R.drawable.ic_upload_doc_image
                                            )
                                        )
                                    }

                                }

                                SECOND_IMG -> {
                                    selectedFilePath2 = selectedFilePath
                                    isSelectedFilePath2 = true
                                    if (isSelectedFilePath2) {
                                        binding?.iv2?.setImageDrawable(
                                            ContextCompat.getDrawable(
                                                requireContext(),
                                                R.drawable.ic_upload_doc_image
                                            )
                                        )
                                    }
                                }

                                THIRD_IMG -> {
                                    selectedFilePath3 = selectedFilePath
                                    isSelectedFilePath3 = true
                                    if (isSelectedFilePath3) {
                                        binding?.iv3?.setImageDrawable(
                                            ContextCompat.getDrawable(
                                                requireContext(),
                                                R.drawable.ic_upload_doc_image
                                            )
                                        )
                                    }
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

    private fun SubmitContactUs() {
        try {
            CNProgressDialog.showProgressDialog(activity, Constants.LOADING_MESSAGE)
            val genericAPIService = GenericAPIService(activity, 0)
            val submitContactUsReq = SubmitContactUsReq()
            submitContactUsReq.webCntExistingCustomer = "Yes"
            submitContactUsReq.webCntName = (activity as BaseActivity).userDetails.fullName
            submitContactUsReq.webCntMobileNumber =
                (activity as BaseActivity).userDetails.userMobile
            submitContactUsReq.webCntEmail = (activity as BaseActivity).userDetails.email
            submitContactUsReq.platform = "Android"
            submitContactUsReq.webCntQuery = selectedQuery?.title
            submitContactUsReq.webCntSubQuery = selectedSubQuery
            submitContactUsReq.webCntMessage = contactUsMessage
            val token = (activity as BaseActivity).userToken
            genericAPIService.submitContactUs(submitContactUsReq, token)
            genericAPIService.setOnDataListener { responseBody ->
                CNProgressDialog.hideProgressDialog()
                val submitContactUsResponse =
                    Gson().fromJson(responseBody, SubmitContactUsResponse::class.java)
                if (submitContactUsResponse != null && submitContactUsResponse.status == true) {
                    when {
                        binding!!.etName == null && binding!!.etName.equals("") -> {
                            Toast.makeText(activity, "Please Provide your name", Toast.LENGTH_SHORT)
                                .show()
                        }

                        binding!!.etMobile == null && binding!!.etMobile.equals("") -> {
                            Toast.makeText(
                                activity,
                                "Please Provide your Mobile Number",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                        binding!!.etEmail == null && binding!!.etEmail.equals("") -> {
                            Toast.makeText(
                                activity,
                                "Please Provide your Email",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                        selectedQuery!!.equals("") -> {
                            Toast.makeText(activity, "Please Select Query", Toast.LENGTH_SHORT)
                                .show()
                        }

                        selectedQuery!!.subTitle!!.equals("") -> {
                            Toast.makeText(activity, "Please Select Query", Toast.LENGTH_SHORT)
                                .show()
                        }

                        binding!!.etMessage.length() <= 50 -> {
                            Toast.makeText(
                                activity,
                                "Message Characters length should be more than 50",
                                Toast.LENGTH_SHORT
                            )
                                .show()
                        }

                        else -> {
                            Toast.makeText(
                                activity,
                                submitContactUsResponse.message,
                                Toast.LENGTH_SHORT
                            )
                                .show()
                            (activity as BaseActivity).sharedPreferences.putBoolean(
                                "shouldRefreshDashboardScreen",
                                true
                            )
                        }

                    }

                }

            }
            genericAPIService.setOnErrorListener {
                CNProgressDialog.hideProgressDialog()
                (activity as BaseActivity).displayToast(it.message.toString())
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun submitContactUsWeb() {
        try {
            CNProgressDialog.showProgressDialog(activity, Constants.LOADING_MESSAGE)
            val genericAPIService = GenericAPIService(activity, 0)
            val token = (activity as BaseActivity).userToken
            val webCntExistingCustomer = "Yes"
            val webCntName = binding?.etName?.text.toString().trim { it <= ' ' }
            val webCntMobileNumber = binding?.etMobile?.text.toString().trim { it <= ' ' }
            val webCntEmail = binding?.etEmail?.text.toString().trim { it <= ' ' }
            val platform = "android"
            val webCntQuery = selectedQuery?.title
            val webCntSubQuery = selectedSubQuery.toString()
            val webCntMessage = contactUsMessage
            genericAPIService.submitContactUsWeb(
                webCntExistingCustomer,
                selectedFilePath1,
                selectedFilePath2,
                selectedFilePath3,
                webCntName,
                webCntMobileNumber,
                webCntEmail,
                platform,
                webCntQuery,
                webCntSubQuery,
                webCntMessage,
                token
            )
            genericAPIService.setOnDataListener { responseBody ->
                CNProgressDialog.hideProgressDialog()
                val fileUploadResponse =
                    Gson().fromJson(responseBody, FileUploadResponse::class.java)
                if (fileUploadResponse != null && fileUploadResponse.status) {

                    Toast.makeText(
                        activity,
                        "Request has been submitted",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                    if(tempFile != null) {
                        tempFile?.delete()
                    }
                    val intent = Intent(context, DashboardActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)


                } else {
                    Toast.makeText(
                        activity,
                        fileUploadResponse.message,
                        Toast.LENGTH_SHORT
                    )
                        .show()
                }
                genericAPIService.setOnErrorListener {
                    CNProgressDialog.hideProgressDialog()
                    (activity as BaseActivity).displayToast(it.message.toString())
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setSubQueries(selectedQuery: ContactUsQuery?) {

        val subQueryZeroPos = "Select Sub Query"
        val subQueryList: ArrayList<String> = ArrayList()
        subQueryList.add(subQueryZeroPos)
        if (selectedQuery != null) {
            selectedQuery.subTitle?.let { subQueryList.addAll(it) }

        }

        subQueryAdapter = context?.let {
            ArrayAdapter(
                it,
                android.R.layout.simple_spinner_item, subQueryList
            )
        }
        subQueryAdapter?.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding!!.spinnerSubQuery.adapter = subQueryAdapter
    }

    private fun setToDefault() {

        val queryZeroPos = ContactUsQuery()
        queryZeroPos.id = 0
        queryZeroPos.title = "Select Query"

        val queryList: ArrayList<ContactUsQuery> = ArrayList()
        queryList.add(queryZeroPos)
        contactUsData?.query?.let { queryList.addAll(it) }

        queryAdapter = context?.let {
            ArrayAdapter(
                it,
                android.R.layout.simple_spinner_item, queryList
            )
        }
        queryAdapter?.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding!!.spinnerQuery.adapter = queryAdapter

        setSubQueries(null)
    }
}