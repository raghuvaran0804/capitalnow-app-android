package com.capitalnowapp.mobile.kotlin.fragments

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.OpenableColumns
import android.provider.Settings
import android.text.Editable
import android.text.InputFilter
import android.text.InputFilter.AllCaps
import android.text.TextWatcher
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.AppCompatCheckBox
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.capitalnowapp.mobile.BuildConfig
import com.capitalnowapp.mobile.R
import com.capitalnowapp.mobile.activities.BaseActivity
import com.capitalnowapp.mobile.beans.RequestBankChangeData
import com.capitalnowapp.mobile.constants.Constants
import com.capitalnowapp.mobile.customviews.CNProgressDialog
import com.capitalnowapp.mobile.kotlin.activities.DashboardActivity
import com.capitalnowapp.mobile.kotlin.adapters.BankNameAdapter
import com.capitalnowapp.mobile.kotlin.utils.FileUtils
import com.capitalnowapp.mobile.models.CNModel
import com.capitalnowapp.mobile.models.FileUploadResponse
import com.capitalnowapp.mobile.retrofit.GenericAPIService
import com.capitalnowapp.mobile.util.CNSharedPreferences
import com.capitalnowapp.mobile.util.RealPathUtil
import com.capitalnowapp.mobile.util.Utility
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.gson.Gson
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.InputStream
import java.util.Locale


class RequestBankChangeFragment : Fragment() {
    private var tempFile: File? = null
    private var dialog: AlertDialog? = null
    var til_bank_name: TextInputLayout? = null
    var til_upload_doc: TextInputLayout? = null
    var et_account_holder_name: TextInputEditText? = null
    var et_branch_name: TextInputEditText? = null
    var et_account_number: TextInputEditText? = null
    var et_re_enter_account_number: TextInputEditText? = null
    var et_ifsc_code: TextInputEditText? = null
    var tx_et_bank_name: TextInputEditText? = null
    var et_upload_doc: TextInputEditText? = null
    var et_enter_password: TextInputEditText? = null
    var cb_first_terms: CheckBox? = null
    var cb_second_terms: CheckBox? = null
    var bt_submit: TextView? = null
    var cbAgreeTerms: AppCompatCheckBox? = null
    private val CP_PROOF_OF_BANK_STATEMENT = 1
    private var current_operation = 1
    private var isGooglePhotosUri = false
    private var selectedFilePath: String? = null
    private var filename: String? = null
    private var bankStatementURL: String? = null
    private var requestBankChangeData = RequestBankChangeData()
    var userId: String? = null
    protected var CNModel: CNModel? = null
    private var currentActivity: Activity? = null
    private var adapter: BankNameAdapter? = null
    var validationMsg = ""
    var sharedPreferences: CNSharedPreferences? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        val view: View = inflater.inflate(R.layout.fragment_request_bank_change, container, false)
        try {
            currentActivity = activity
            til_bank_name = view.findViewById(R.id.til_bank_name)
            til_upload_doc = view.findViewById(R.id.til_upload_doc)
            et_account_holder_name = view.findViewById(R.id.et_account_holder_name)
            et_branch_name = view.findViewById(R.id.et_branch_name)
            et_account_number = view.findViewById(R.id.et_account_number)
            et_re_enter_account_number = view.findViewById(R.id.et_re_enter_account_number)
            et_ifsc_code = view.findViewById(R.id.et_ifsc_code)
            tx_et_bank_name = view.findViewById(R.id.tx_et_bank_name)
            et_enter_password = view.findViewById(R.id.et_enter_password)
            et_upload_doc = view.findViewById(R.id.et_upload_doc)
            cb_first_terms = view.findViewById(R.id.cb_first_terms)
            cb_second_terms = view.findViewById(R.id.cb_second_terms)
            cbAgreeTerms = view.findViewById(R.id.cbAgreeTerms)
            bt_submit = view.findViewById(R.id.bt_submit)

            userId = (currentActivity as BaseActivity).userDetails.userId
            CNModel = CNModel(context, activity, Constants.RequestFrom.REQUEST_BANK_CHANGE)

            et_ifsc_code?.filters = arrayOf<InputFilter>(AllCaps())

            initView()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return view
    }

    private fun initView() {
        try {
            disableAgreeButton()
            et_upload_doc?.setOnClickListener(object : View.OnClickListener {
                override fun onClick(v: View?) {
                    //Your code here
                    chooseFile(CP_PROOF_OF_BANK_STATEMENT)
                }
            })

            cbAgreeTerms?.setOnClickListener(View.OnClickListener {
                if (cbAgreeTerms!!.isChecked) {
                    enableAgreeButton()
                } else
                    disableAgreeButton()
            })

            bt_submit?.setOnClickListener { saveRequestBankData() }
            tx_et_bank_name?.setOnClickListener { showBankNameDialog() }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun showBankNameDialog() {
        val Lines = resources.getStringArray(R.array.arrayBanks).toList()
        val builder = AlertDialog.Builder(context)
        val view = layoutInflater.inflate(R.layout.filter_dialog, null)

        val rvData = view.findViewById<RecyclerView>(R.id.rvData)
        val etSearchBank = view.findViewById<EditText>(R.id.etSearch)
        etSearchBank.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {

                if (s.isNotEmpty()) {
                    filterBank(s)
                } else {
                    adapter?.updateData(Lines as java.util.ArrayList<String>)
                }
            }
        })
        rvData?.layoutManager = LinearLayoutManager(context)
        adapter = BankNameAdapter(Lines, this)
        rvData.adapter = adapter
        builder.setView(view)
        builder.setCancelable(true)
        dialog = builder.create()
        dialog?.show()

        val displayMetrics = DisplayMetrics()
        currentActivity?.windowManager?.defaultDisplay?.getMetrics(displayMetrics)
        val displayWidth: Int = displayMetrics.widthPixels
        val displayHeight: Int = displayMetrics.heightPixels
        val layoutParams: WindowManager.LayoutParams = WindowManager.LayoutParams()
        layoutParams.copyFrom(dialog?.window!!.attributes)
        val dialogWindowWidth = (displayWidth * 0.8f).toInt()
        val dialogWindowHeight = (displayHeight * 0.6f).toInt()
        layoutParams.width = dialogWindowWidth
        layoutParams.height = dialogWindowHeight
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.window!!.attributes = layoutParams
    }

    fun onBankSelected(s: String) {
        if (dialog != null && dialog?.isShowing!!)
            dialog?.dismiss()

        tx_et_bank_name?.setText(s)
    }

    private fun filterBank(s: Editable) {
        var banks: List<String> = ArrayList()

        val filterBankList: ArrayList<String> = ArrayList()
        banks = resources.getStringArray(R.array.arrayBanks).toList()
        for (i in banks.indices) {
            val item: String = banks[i]

            if (item.toLowerCase(Locale.ROOT).contains(s)) {
                filterBankList.add(item)
            }
        }

        if (filterBankList.size > 0) {
            adapter?.updateData(filterBankList)
        }
    }

    private fun saveRequestBankData() {
        try {
            val accountHolderName = et_account_holder_name!!.text.toString().trim { it <= ' ' }
            val branchName = et_branch_name!!.text.toString().trim { it <= ' ' }
            val accountNumber = et_account_number!!.text.toString().trim { it <= ' ' }
            val reAccountNumber = et_re_enter_account_number!!.text.toString().trim { it <= ' ' }
            val ifscCode = et_ifsc_code!!.text.toString().trim { it <= ' ' }
            val bankName = tx_et_bank_name!!.text.toString().trim { it <= ' ' }
            val uploadDoc = et_upload_doc!!.text.toString().trim { it <= ' ' }
            val docPassword = et_enter_password!!.text.toString().trim { it <= ' ' }
            var focusView: View? = null
            var count = 0

            if (bankName.isEmpty()) {
                validationMsg = "Please select Bank"
                count++
            } else if (accountHolderName.isEmpty()) {
                validationMsg = "Account Holder Name is required and can't be empty"
                count++
            } else if (accountNumber.isEmpty()) {
                validationMsg = "Account Number is required and can't be empty"
                count++
            } else if (reAccountNumber.isEmpty()) {
                validationMsg = "Re-Enter Account Number is required and can't be empty"
                count++
            } else if (!accountNumber.equals(
                    et_re_enter_account_number!!.text.toString().trim { it <= ' ' })
            ) {
                validationMsg = "Account Number & Re-Enter Account Number not matching"
                count++
            } else if (ifscCode.isEmpty() || !isValidIfscCode(ifscCode.trim { it <= ' ' }, false)) {
                validationMsg = "Please enter valid IFSC Code"
                count++
            } else if (branchName.isEmpty()) {
                validationMsg = "Branch Name is required and can't be empty"
                count++
            } else if (uploadDoc.isEmpty()) {
                validationMsg = "Please upload Bank Statement"
                count++
            }
            if (count > 0) {
                displayToast(validationMsg)
            } else {
                if (focusView != null && focusView is TextInputEditText) focusView.clearFocus()
                requestBankChangeData.accountHolderName = accountHolderName
                requestBankChangeData.accountNumber = accountNumber
                requestBankChangeData.ifscCode = ifscCode
                requestBankChangeData.bankName = bankName
                requestBankChangeData.branchName = branchName
                requestBankChangeData.bankAccountPassword = docPassword
                if (bankStatementURL != null && bankStatementURL!!.isNotEmpty()) {
                    submitBankChangeRequestData(requestBankChangeData)
                } else {
                    uploadFile(selectedFilePath!!, filename!!, isGooglePhotosUri)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun disableAgreeButton() {
        bt_submit?.isEnabled = false
        bt_submit?.setBackgroundColor(ContextCompat.getColor(bt_submit!!.context, R.color.dark_gray))
        //Toast.makeText(context, "For availing better chances of getting your loan approved, please Agree and continue", Toast.LENGTH_SHORT).show()
    }

    private fun enableAgreeButton() {
        bt_submit?.isEnabled = true
        bt_submit?.setBackgroundColor(ContextCompat.getColor(bt_submit!!.context, R.color.Primary2))
    }

    private fun submitBankChangeRequestData(requestBankChangeData: RequestBankChangeData?) {
        CNProgressDialog.showProgressDialog(currentActivity, Constants.LOADING_MESSAGE)
        val token = (activity as BaseActivity).userToken

        CNModel!!.saveBankChangeRequestData(userId, requestBankChangeData,token)
    }

    private fun isValidIfscCode(ifscCode: String, isOfficeNo: Boolean): Boolean {
        return if (ifscCode.isNotEmpty()) {
            ifscCode.length == 11
        } else {
            false
        }
    }

    fun chooseFile(type: Int) {
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

    private fun openGallery() {
        val intent: Intent = this.getFileChooserIntent()!!
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

    private fun getFileChooserIntent(): Intent? {
        val mimeTypes = arrayOf("application/pdf")
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //intent.setType(mimeTypes.length == 1 ? mimeTypes[0] : "*/*");
            intent.type = "application/pdf"
            if (mimeTypes.size > 0) {
                intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
            }
        } else {
            var mimeTypesStr = ""
            for (mimeType in mimeTypes) {
                mimeTypesStr += "$mimeType|"
            }
            intent.type = mimeTypesStr.substring(0, mimeTypesStr.length - 1)
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
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
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

                    //if (mimeType.contains("jpeg") || mimeType.contains("png") || mimeType.contains("pdf")) {
                    if (fileType.equals("JPG", ignoreCase = true) || fileType.equals(
                            "JPEG",
                            ignoreCase = true
                        ) || fileType.equals("PNG", ignoreCase = true) || fileType.equals(
                            "PDF",
                            ignoreCase = true
                        )
                    ) {
                        if (selectedDocumentUri?.let { isGoogleDriveUri(it) }!!) {
                            try {
                                val inputStream =
                                    context?.contentResolver?.openInputStream(selectedDocumentUri)
                                if (inputStream != null) {
                                    selectedFilePath =
                                        RealPathUtil.getPathFromUri(context, selectedDocumentUri)
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
                        updateSelectedFilePath(selectedFilePath, fileType, isGooglePhotosUri)
                    } else {
                        displayToast("Please select JPG/JPEG, PNG OR PDF format file only.")
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun isGoogleDriveUri(uri: Uri): Boolean {
        return "com.google.android.apps.docs.storage" == uri.authority || "com.google.android.apps.docs.storage.legacy" == uri.authority
    }

    fun updateSelectedFilePath(
        selectedFilePath: String?,
        fileType: String?,
        isExternalDoc: Boolean
    ) {
        var selectedFilePath = selectedFilePath
        try {
            var uriFromPath: Uri? = null
            val textView: TextView? = null
            if (selectedFilePath != null && selectedFilePath !== "") {
                val selectedFile = File(selectedFilePath)
                uriFromPath = Uri.fromFile(selectedFile)
                filename = selectedFilePath.substring(selectedFilePath.lastIndexOf("/") + 1)
                val selectedFileSizeInMB = selectedFile.length() / (1024 * 1024)
                if (selectedFileSizeInMB > Constants.FILE_UPLOAD_LIMIT) {
                    displayToast(
                        String.format(
                            "Uploading file size must be less than %d MB.",
                            Constants.FILE_UPLOAD_LIMIT
                        )
                    )
                    return
                }
            } else {
                displayToast("An error occurred while selecting document to upload.")
                selectedFilePath = ""
                return
            }
            if (current_operation == CP_PROOF_OF_BANK_STATEMENT) {
                uploadFile(selectedFilePath, filename!!, isExternalDoc)
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    private fun uploadFile(selectedFilePath: String, fileName: String, isExternalDoc: Boolean) {
        CNProgressDialog.showProgressDialog(currentActivity, Constants.LOADING_MESSAGE)
        val genericAPIService = GenericAPIService(context)
        val token = (currentActivity as BaseActivity).userToken
        genericAPIService.uploadFileToServer(selectedFilePath, userId, token)
        genericAPIService.setOnDataListener { responseBody ->
            CNProgressDialog.hideProgressDialog()
            val fileUploadResponse = Gson().fromJson(responseBody, FileUploadResponse::class.java)
            if (fileUploadResponse != null && fileUploadResponse.status) {
                bankStatementURL = fileUploadResponse.fileUrl
                val textView: TextInputEditText? = et_upload_doc
                requestBankChangeData?.bankStatement = fileUploadResponse.fileUrl
                requestBankChangeData?.isIdProofExt = isExternalDoc
                textView?.setText(fileName)

                if (tempFile != null) {
                    tempFile?.delete()
                }
            } else {
                bankStatementURL = null
            }
        }
        genericAPIService.setOnErrorListener {
            CNProgressDialog.hideProgressDialog()
            bankStatementURL = null
        }
    }

    fun displayToast(message: String?) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == Constants.REQUEST_CODE_READ_EXTERNAL_STORAGE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openGallery()
            } else {
                if (!ActivityCompat.shouldShowRequestPermissionRationale(
                        activity as BaseActivity,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    )
                ) {
                    startActivity(
                        Intent(
                            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                            Uri.parse("package:" + BuildConfig.APPLICATION_ID)
                        )
                    )
                }
            }
        }
    }
}
