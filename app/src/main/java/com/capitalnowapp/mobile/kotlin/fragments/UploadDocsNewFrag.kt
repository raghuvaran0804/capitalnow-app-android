package com.capitalnowapp.mobile.kotlin.fragments

//import com.appsflyer.AppsFlyerLib
//import io.branch.referral.util.BranchEvent
import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Activity.RESULT_OK
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.MailTo
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.provider.Settings
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.DisplayMetrics
import android.util.Log
import android.view.*
import android.view.View.GONE
import android.view.View.VISIBLE
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatCheckBox
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import com.capitalnowapp.mobile.BuildConfig
import com.capitalnowapp.mobile.R
import com.capitalnowapp.mobile.activities.BaseActivity
import com.capitalnowapp.mobile.beans.RequiredDocuments
import com.capitalnowapp.mobile.beans.UploadDocuments
import com.capitalnowapp.mobile.constants.Constants
import com.capitalnowapp.mobile.customviews.CNAlertDialog
import com.capitalnowapp.mobile.customviews.CNProgressDialog
import com.capitalnowapp.mobile.customviews.CNTextView
import com.capitalnowapp.mobile.databinding.FragDocsNewBinding
import com.capitalnowapp.mobile.kotlin.activities.CropActivity
import com.capitalnowapp.mobile.kotlin.activities.DashboardActivity
import com.capitalnowapp.mobile.kotlin.activities.DocsGuidelinesActivity
import com.capitalnowapp.mobile.kotlin.utils.FileUtils
import com.capitalnowapp.mobile.models.*
import com.capitalnowapp.mobile.retrofit.GenericAPIService
import com.capitalnowapp.mobile.retrofit.ProgressAPIService
import com.capitalnowapp.mobile.util.CNSharedPreferences
import com.capitalnowapp.mobile.util.RealPathUtil
import com.capitalnowapp.mobile.util.TrackingUtil
import com.capitalnowapp.mobile.util.Utility
import com.google.gson.Gson
import com.theartofdev.edmodo.cropper.CropImage
import kotlinx.android.synthetic.main.frag_docs_new.*
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


class UploadDocsNewFrag : Fragment(), View.OnClickListener, View.OnTouchListener {
    companion object {
        private const val CAMERA_PERMISSION_CODE = 100
    }
    private var tempFile: File? = null
    private var sharedPreferences: CNSharedPreferences? = null
    private var cropFileName: String? = null
    private var cropGooglePhotosUri: Boolean? = false
    private var isIdSelected: Boolean = false
    private var isSlipSelected: Boolean = false
    private var isSignSelected: Boolean = false
    private var isAdr1Selected: Boolean = false
    private var isAdr2Selected: Boolean = false
    private var isPrAdr1Selected: Boolean = false
    private var isPrAdr2Selected: Boolean = false
    private var isBank1Selected: Boolean = false
    private var isBank2Selected: Boolean = false
    private var isBank3Selected: Boolean = false
    private var currenntPath = ""
    private var mMakePhotoUri: Uri? = null
    private val REQUEST_IMAGE_CAPTURE = 102

    private var activity: AppCompatActivity? = null

    private var binding: FragDocsNewBinding? = null
    private var utility: Utility? = null
    private var uploadDocuments: UploadDocuments? = null
    private var requiredDocuments: RequiredDocuments? = null

    private var fileUploadAjaxRequest: FileUploadAjaxRequest? = null

    private var current_operation: Int = 1
    private var noOfRequiredDocs: Int = 0

    private var isIdProofPending: Boolean = false
    private var isAddProofPending: Boolean = false
    private var isSalSlipPending: Boolean = false
    private var isBankStmtPending: Boolean = false
    private var isSignaturePending: Boolean = false

    private var statementsUrl: String? = null
    private var addressUrl: String? = null
    private var presentAddressUrl: String? = null
    private var dynamicText = ""


    private val CP_PROOF_OF_ID = 1
    private val CP_PROOF_OF_ADDRESS = 2
    private val CP_SAL_SLIP = 3
    private val CP_BANK_STMT = 4
    private val CP_PROOF_OF_ADDRESS_2 = 5
    private val CP_BANK_STMT_2 = 6
    private val CP_BANK_STMT_3 = 7
    private val CP_SIGN = 8
    private val CP_PRESENT_ADDRESS_1 = 9
    private val CP_PRESENT_ADDRESS_2 = 10

    @SuppressLint("NotConstructor")
    fun UploadDocsNewFrag() {
        // empty constructor
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragDocsNewBinding.inflate(inflater, container, false)
        activity = (getActivity() as DashboardActivity)
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
                obj.put("cnid",(activity as BaseActivity).userDetails.qcId)
            } catch (e: JSONException) {
                throw RuntimeException(e)
            }
            TrackingUtil.pushEvent(obj, getString(R.string.upload_documents_page_landed))

            if ((activity as BaseActivity).userDetails.references == false) {
                llPercentage?.visibility = View.VISIBLE
                circularProgressBar?.progress = 20
                txtProgressCircular?.text = "20%"
            } else {
                llPercentage?.visibility = View.GONE
            }
            if(getActivity()?.intent?.extras !=null){
                dynamicText = requireActivity().intent?.getStringExtra("bank_statement_upload_text").toString()
                binding!!.tvStatement.text = dynamicText
            }
            (activity as BaseActivity).userId = (activity as BaseActivity).userDetails.userId
            (activity as BaseActivity).cnModel =
                CNModel(context, activity, Constants.RequestFrom.APPLY_LOAN)
            utility = Utility.getInstance()
            uploadDocuments = UploadDocuments((activity as BaseActivity).userId)

            val bundle = arguments
            if (bundle != null) {
                requiredDocuments =
                    bundle.getSerializable(Constants.SP_REQUIRED_DOCUMENTS) as RequiredDocuments?
            }

            if (requiredDocuments!!.proofOfId == 1) {
                llIdProof?.visibility = View.VISIBLE
                isIdProofPending = true
                noOfRequiredDocs += 1
            } else {
                llIdProof?.visibility = View.GONE
                isIdProofPending = false
            }
            if (requiredDocuments!!.signature == 1) {
                llSignature?.visibility = View.VISIBLE
                isSignaturePending = true
                noOfRequiredDocs += 1
            } else {
                llSignature?.visibility = View.GONE
                isSignaturePending = false
            }
            if (requiredDocuments!!.proofOfAddress == 1) {
                llAddressProof?.visibility = View.VISIBLE
                isAddProofPending = true
                noOfRequiredDocs += 1
                llSameAdr.visibility = VISIBLE
            } else {
                llAddressProof?.visibility = View.GONE
                isAddProofPending = false
                llSameAdr.visibility = GONE
            }

            if (requiredDocuments!!.bankStatement == 1) {
                llBankStatement?.visibility = View.VISIBLE
                isBankStmtPending = true
                noOfRequiredDocs += 1
            } else {
                llBankStatement?.visibility = View.GONE
                isBankStmtPending = false
            }

            if (requiredDocuments!!.salSlip == 1) {
                llSalarySlip?.visibility = View.VISIBLE
                isSalSlipPending = true
                noOfRequiredDocs += 1
            } else {
                llSalarySlip?.visibility = View.GONE
                isSalSlipPending = false
            }
            if (requiredDocuments!!.proofOfPresentAddress == 1) {
                llPresentAdr?.visibility = View.VISIBLE
                noOfRequiredDocs += 1
            } else {
                llPresentAdr?.visibility = View.GONE
            }

            llIdProof.setOnClickListener(this)
            ivId.setOnClickListener(this)

            llAddressProof.setOnClickListener(this)
            ivAdr1.setOnClickListener(this)
            ivAdr2.setOnClickListener(this)

            llSalarySlip.setOnClickListener(this)
            ivSlip.setOnClickListener(this)

            llBankStatement.setOnClickListener(this)
            ivBank1.setOnClickListener(this)
            ivBank2.setOnClickListener(this)
            ivBank3.setOnClickListener(this)

            llSignature.setOnClickListener(this)
            ivSign.setOnClickListener(this)

            llPresentAdr.setOnClickListener(this)
            ivPrAdr1.setOnClickListener(this)
            ivPrAdr2.setOnClickListener(this)

            ivSubmit.setOnClickListener(this)
            etGuidelines.setOnClickListener(this)

            sharedPreferences = (activity as DashboardActivity).sharedPreferences

            if (sharedPreferences!!.getBoolean(Constants.DOCS_HELP_SHOWN)) {
                frameHelp.visibility = GONE
            } else {
                if (((activity as DashboardActivity).userDetails.hasTakenFirstLoan != 1)) {
                    sharedPreferences!!.putBoolean(Constants.DOCS_HELP_SHOWN, true)
                    frameHelp.visibility = VISIBLE
                } else {
                    frameHelp.visibility = GONE
                }
            }

            frameHelp.setOnTouchListener { _, _ ->
                frameHelp.visibility = GONE
                sharedPreferences!!.putBoolean(Constants.DOCS_HELP_SHOWN, true)
                false
            }

            llSameAdr.setOnClickListener {
                if (!cbSameAdr.isChecked) {
                    if ((uploadDocuments!!.addressProof!!.isEmpty() || uploadDocuments!!.addressProof!! == "")
                        && (uploadDocuments!!.addressProof2!!.isEmpty() || uploadDocuments!!.addressProof2!! == "")
                    ) {
                        cbSameAdr.isChecked = false
                        llPrAdrAttachments.visibility = GONE
                        setExpand(tvSelectPrAdr, false)
                    } else {
                        cbSameAdr.isChecked = true
                        llPrAdrAttachments.visibility = GONE
                        setExpand(tvSelectPrAdr, true)
                    }
                } else {
                    cbSameAdr.isChecked = false
                }
            }
            /* cbSameAdr.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener { _, isChecked ->
                 if (isChecked) {
                     if ((uploadDocuments!!.addressProof!!.isEmpty() || uploadDocuments!!.addressProof!! == "")
                             && (uploadDocuments!!.addressProof2!!.isEmpty() || uploadDocuments!!.addressProof2!! == "")) {
                         cbSameAdr.isChecked = false
                         llPrAdrAttachments.visibility = GONE
                         setExpand(tvSelectPrAdr, false)
                     }else{

                     }
                 } else {
                     llPrAdrAttachments.visibility = GONE
                     setExpand(tvSelectPrAdr, true)
                      uploadDocuments!!.proof_present_address = uploadDocuments!!.addressProof!!
                      uploadDocuments!!.proof_present_address2 = uploadDocuments!!.addressProof2!!
                      uploadDocuments!!.present_address_pwd = uploadDocuments!!.addressProofPwd!!
                      uploadDocuments!!.present_add_as_poa = true
                 }
             })*/

        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
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


                    val obj = JSONObject()
                    try {
                        obj.put("cnid",(activity as BaseActivity).userDetails.qcId)
                        obj.put(getString(R.string.interaction_type),"Upload Documents Guidelines Clicked")
                    } catch (e: JSONException) {
                        throw RuntimeException(e)
                    }
                    TrackingUtil.pushEvent(obj, getString(R.string.upload_documents_page_interacted))

                    val intent = Intent(activity, DocsGuidelinesActivity::class.java)
                    intent.putExtra(
                        "url",
                        "https://api.capitalnow.in/mpage/upload-documents-guidelines"
                    )
                    startActivity(intent)
                    activity?.overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up)
                }
                R.id.llIdProof -> {
                    if (ivId.visibility == VISIBLE) {
                        ivId.visibility = GONE
                        setExpand(tvSelectId, true)
                    } else {
                        ivId.visibility = VISIBLE
                        setExpand(tvSelectId, false)
                    }
                }
                R.id.ivId -> {
                    if (isIdSelected) {
                        isIdSelected = false
                        ivId.setImageDrawable(
                            ContextCompat.getDrawable(
                                requireContext(),
                                R.drawable.ic_upload_doc_up
                            )
                        )
                    } else {
                        checkPermission(Manifest.permission.CAMERA,
                            CAMERA_PERMISSION_CODE
                        )
                        selectImage(CP_PROOF_OF_ID)
                    }
                }
                R.id.llAddressProof -> {

                    val obj = JSONObject()
                    try {
                        obj.put("cnid",(activity as BaseActivity).userDetails.qcId)
                        obj.put(getString(R.string.interaction_type),"Proof of Address")
                    } catch (e: JSONException) {
                        throw RuntimeException(e)
                    }
                    TrackingUtil.pushEvent(obj, getString(R.string.upload_documents_page_interacted))

                    if (llAdrAttachments.visibility == VISIBLE) {
                        llAdrAttachments.visibility = GONE
                        setExpand(tvSelectAdr, true)
                    } else {
                        llAdrAttachments.visibility = VISIBLE
                        setExpand(tvSelectAdr, false)
                    }
                }
                R.id.ivAdr1 -> {
                    if (isAdr1Selected) {
                        isAdr1Selected = false
                        ivAdr1.setImageDrawable(
                            ContextCompat.getDrawable(
                                requireContext(),
                                R.drawable.ic_upload_doc_up
                            )
                        )
                    } else {
                        checkPermission(Manifest.permission.CAMERA,
                            CAMERA_PERMISSION_CODE
                        )
                        selectImage(CP_PROOF_OF_ADDRESS)
                    }
                }
                R.id.ivAdr2 -> {
                    if (isAdr2Selected) {
                        isAdr2Selected = false
                        ivAdr2.setImageDrawable(
                            ContextCompat.getDrawable(
                                requireContext(),
                                R.drawable.ic_upload_doc_up
                            )
                        )
                    } else {
                        checkPermission(Manifest.permission.CAMERA,
                            CAMERA_PERMISSION_CODE
                        )
                        selectImage(CP_PROOF_OF_ADDRESS_2)
                    }
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
                        checkPermission(Manifest.permission.CAMERA,
                            CAMERA_PERMISSION_CODE
                        )
                        selectImage(CP_SAL_SLIP)
                    }
                }
                R.id.llSalarySlip -> {
                    if (llSlipAttachments.visibility == VISIBLE) {
                        llSlipAttachments.visibility = GONE
                        setExpand(tvSelectSlip, true)
                    } else {
                        llSlipAttachments.visibility = VISIBLE
                        setExpand(tvSelectSlip, false)
                    }
                }
                R.id.llBankStatement -> {
                    if (llBankAttachments.visibility == VISIBLE) {
                        llBankAttachments.visibility = GONE
                        setExpand(tvSelectBank, true)
                    } else {
                        llBankAttachments.visibility = VISIBLE
                        setExpand(tvSelectBank, false)
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
                R.id.llSignature -> {
                    if (ivSign.visibility == VISIBLE) {
                        ivSign.visibility = GONE
                        setExpand(tvSelectSign, true)
                    } else {
                        ivSign.visibility = VISIBLE
                        setExpand(tvSelectSign, false)
                    }
                }
                R.id.ivSign -> {
                    if (isSignSelected) {
                        isSignSelected = false
                        ivSign.setImageDrawable(
                            ContextCompat.getDrawable(
                                requireContext(),
                                R.drawable.ic_upload_doc_up
                            )
                        )
                    } else {
                        //chooseFile(CP_SIGN)
                        checkPermission(Manifest.permission.CAMERA,
                            CAMERA_PERMISSION_CODE
                        )
                        selectImage(CP_SIGN)
                    }
                }
                R.id.llPresentAdr -> {

                    val obj = JSONObject()
                    try {
                        obj.put("cnid",(activity as BaseActivity).userDetails.qcId)
                        obj.put(getString(R.string.interaction_type),"Present Address Proof")
                    } catch (e: JSONException) {
                        throw RuntimeException(e)
                    }
                    TrackingUtil.pushEvent(obj, getString(R.string.upload_documents_page_interacted))

                    if (llPrAdrAttachments.visibility == VISIBLE) {
                        llPrAdrAttachments.visibility = GONE
                        setExpand(tvSelectPrAdr, true)
                    } else {
                        llPrAdrAttachments.visibility = VISIBLE
                        setExpand(tvSelectPrAdr, false)
                    }
                }
                R.id.ivPrAdr1 -> {
                    if (isPrAdr1Selected) {
                        isPrAdr1Selected = false
                        ivPrAdr1.setImageDrawable(
                            ContextCompat.getDrawable(
                                requireContext(),
                                R.drawable.ic_upload_doc_up
                            )
                        )
                    } else {
                        checkPermission(Manifest.permission.CAMERA,
                            CAMERA_PERMISSION_CODE
                        )
                        selectImage(CP_PRESENT_ADDRESS_1)
                    }
                }
                R.id.ivPrAdr2 -> {
                    if (isPrAdr2Selected) {
                        isPrAdr2Selected = false
                        ivPrAdr2.setImageDrawable(
                            ContextCompat.getDrawable(
                                requireContext(),
                                R.drawable.ic_upload_doc_up
                            )
                        )
                    } else {
                        checkPermission(Manifest.permission.CAMERA,
                            CAMERA_PERMISSION_CODE
                        )
                        selectImage(CP_PRESENT_ADDRESS_2)
                    }
                }
                R.id.ivSubmit -> {
                    val obj = JSONObject()
                    try {
                        obj.put("cnid",(activity as BaseActivity).userDetails.qcId)
                        obj.put(getString(R.string.interaction_type),"Click on SUBMIT")
                    } catch (e: JSONException) {
                        throw RuntimeException(e)
                    }
                    TrackingUtil.pushEvent(obj, getString(R.string.upload_documents_page_interacted))

                    validateUploadPendingDocuments()
                }




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
        val intent =
            if (current_operation == CP_BANK_STMT || current_operation == CP_BANK_STMT_2 || current_operation == CP_BANK_STMT_3) {
                getPDFChooserIntent()
            } else if (current_operation == CP_SIGN) {
                getImageChooserIntent()
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
                                    if (current_operation != CP_SIGN) {
                                        uploadFileWithProgress(
                                            it,
                                            isGooglePhotosUri,
                                            selectedFilePath.substring(
                                                selectedFilePath.lastIndexOf(
                                                    "/"
                                                ) + 1
                                            )
                                        )
                                    } else {
                                        showCrop(
                                            it,
                                            isGooglePhotosUri,
                                            selectedFilePath,
                                            selectedDocumentUri
                                        )
                                    }
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
                    val result = data?.getStringExtra("crop")
                    if (resultCode == RESULT_OK) {
                        uploadFileWithProgress(result!!, cropGooglePhotosUri!!, cropFileName!!)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            } else if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
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
                            if (current_operation != CP_SIGN) {
                                uploadFileWithProgress(
                                    it,
                                    false,
                                    selectedFilePath.substring(selectedFilePath.lastIndexOf("/") + 1)
                                )
                            } else {
                                showCrop(it, false, selectedFilePath, mMakePhotoUri!!)
                            }
                        }
                    }
                }
            }
        } catch (e: Exception) {
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
            startActivityForResult(intent, CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE)
        } catch (e: java.lang.Exception) {
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

    private fun isAllDocumentSelected(): Boolean {

        if (cbSameAdr.isChecked) {
            if ((uploadDocuments!!.addressProof!!.isEmpty() || uploadDocuments!!.addressProof!! == "") && (uploadDocuments!!.addressProof2!!.isEmpty() || uploadDocuments!!.addressProof2!! == "")) {
                cbSameAdr.isChecked = false
            } else {
                uploadDocuments!!.proof_present_address = uploadDocuments!!.addressProof!!
                uploadDocuments!!.proof_present_address2 = uploadDocuments!!.addressProof2!!
                uploadDocuments!!.present_address_pwd = uploadDocuments!!.addressProofPwd!!
                uploadDocuments!!.present_add_as_poa = true
                presentAddressUrl = addressUrl
            }
        }
        return if (isSignaturePending) {
            if (uploadDocuments!!.customersignature.isNotEmpty()) {
                (uploadDocuments!!.idProof.isNotEmpty() || (uploadDocuments!!.addressProof.isNotEmpty() || uploadDocuments!!.addressProof2.isNotEmpty())
                        || uploadDocuments!!.salSlip.isNotEmpty() || (uploadDocuments!!.bankStmt.isNotEmpty()
                        || uploadDocuments!!.bankStmt2.isNotEmpty() || uploadDocuments!!.bankStmt3.isNotEmpty())
                        || uploadDocuments!!.customersignature.isNotEmpty())
            } else {
                false
            }
        } else {
            (uploadDocuments!!.idProof.isNotEmpty() || (uploadDocuments!!.addressProof.isNotEmpty() || uploadDocuments!!.addressProof2.isNotEmpty())
                    || uploadDocuments!!.salSlip.isNotEmpty() || (uploadDocuments!!.bankStmt.isNotEmpty()
                    || uploadDocuments!!.bankStmt2.isNotEmpty() || uploadDocuments!!.bankStmt3.isNotEmpty())
                    || uploadDocuments!!.customersignature.isNotEmpty() || uploadDocuments!!.proof_present_address.isNotEmpty() || uploadDocuments!!.proof_present_address2.isNotEmpty())
        }
    }

    private fun validateUploadPendingDocuments() {
        val salSlipPwd: String = etSlipPwd?.text.toString()
        val addressProofPwd: String = etAddressPwd?.text.toString()
        val bankStatementPwd: String = etBankSlipPassword?.text.toString()

        uploadDocuments!!.salSlipPassword = salSlipPwd
        uploadDocuments!!.bankStmtPassword = bankStatementPwd
        uploadDocuments!!.addressProofPwd = addressProofPwd
        if (cbSameAdr.isChecked) {
            uploadDocuments!!.present_address_pwd = addressProofPwd
            uploadDocuments!!.present_add_as_poa = true
        } else {
            uploadDocuments!!.present_address_pwd = etPrAdrPwd.text.toString().trim()
            uploadDocuments!!.present_add_as_poa = false
        }

        if (isAllDocumentSelected()) {
            if (requiredDocuments!!.signature_consent_req.toInt() == 1) {
                showConsent()
            } else {
                submitInitialDocs()
            }
        } else {
            displayToast(getString(R.string.upload_docs_validation_msg_1))

        }
    }

    private fun showConsent() {

        val builder = android.app.AlertDialog.Builder(context)
        val view = layoutInflater.inflate(R.layout.dialog_consent, null)

        val tvTitle = view.findViewById<CNTextView>(R.id.tvTitle)
        val tv_privacy_link = view.findViewById<CNTextView>(R.id.tv_privacy_link)
        val tvCancel = view.findViewById<CNTextView>(R.id.tvCancel)
        val tvOk = view.findViewById<CNTextView>(R.id.tvOk)
        val cbAgreeTerms = view.findViewById<AppCompatCheckBox>(R.id.cbAgreeTerms)

        tvTitle.text = (activity as DashboardActivity).getString(R.string.consent_title)

        builder.setView(view)
        builder.setCancelable(true)
        val dialog = builder.create()
        dialog?.show()

        var startIndex: Int
        var endIndex: Int
        val ss: SpannableString?
        if (requiredDocuments?.userTermsData != null && requiredDocuments?.userTermsData!!.message != null) {
            val words: List<String> = requiredDocuments?.userTermsData!!.findText!!.split("||")
            val links: List<String> = requiredDocuments?.userTermsData!!.replaceLinks!!.split("||")
            ss = SpannableString(requiredDocuments?.userTermsData!!.message)
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
                tv_privacy_link?.text = ss
                tv_privacy_link?.movementMethod = LinkMovementMethod.getInstance()
            }
        }

        tvCancel.setOnClickListener {
            dialog.dismiss()
        }
        tvOk.setOnClickListener {
            if (cbAgreeTerms.isChecked) {
                dialog.dismiss()
                submitInitialDocs()
            } else {
                (activity as DashboardActivity).displayToast("Please Agree & Authorize")
            }
        }
        cbAgreeTerms.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                tvOk.isEnabled = true
                tvOk.backgroundTintList = ContextCompat.getColorStateList(
                    activity as DashboardActivity,
                    R.color.Primary1
                )
            } else {
                tvOk.isEnabled = false
                tvOk.backgroundTintList = ContextCompat.getColorStateList(
                    activity as DashboardActivity,
                    R.color.colorGrey
                )
            }
        }


        val displayMetrics = DisplayMetrics()
        activity?.windowManager?.defaultDisplay?.getMetrics(displayMetrics)
        val displayWidth: Int = displayMetrics.widthPixels
        val displayHeight: Int = displayMetrics.heightPixels
        val layoutParams: WindowManager.LayoutParams = WindowManager.LayoutParams()
        layoutParams.copyFrom(dialog.window!!.attributes)
        val dialogWindowWidth = (displayWidth * 0.9f).toInt()
        layoutParams.width = dialogWindowWidth
        dialog.window!!.attributes = layoutParams

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

    private fun submitInitialDocs() {
        try {
            CNProgressDialog.showProgressDialog(context, Constants.LOADING_MESSAGE)
            val genericAPIService = GenericAPIService(context)
            val submitInitialDocsReq = SubmitInitialDocsReq()
            submitInitialDocsReq.setUserId((activity as BaseActivity).userId)
            submitInitialDocsReq.setBankStatementsPassword(uploadDocuments!!.bankStmtPassword)
            submitInitialDocsReq.setIdProofUrl(uploadDocuments!!.idProof)
            submitInitialDocsReq.setSalSlipUrl(uploadDocuments!!.salSlip)
            submitInitialDocsReq.addressProofPwd = uploadDocuments!!.addressProofPwd
            submitInitialDocsReq.salSlipPassword = uploadDocuments!!.salSlipPassword
            submitInitialDocsReq.setBankStatementsUrl(statementsUrl)
            submitInitialDocsReq.setAddressProofUrl(addressUrl)
            submitInitialDocsReq.isPresent_add_as_poa = uploadDocuments!!.present_add_as_poa
            submitInitialDocsReq.present_address_pwd = uploadDocuments!!.present_address_pwd
            submitInitialDocsReq.proof_present_address = presentAddressUrl
            submitInitialDocsReq.signature_consent = "1"
            submitInitialDocsReq.customersignature = uploadDocuments!!.customersignature
            submitInitialDocsReq.deviceUniqueId = Utility.getInstance().getDeviceUniqueId(activity)
            Log.d("docs_req", Gson().toJson(submitInitialDocsReq))
            val token = (activity as BaseActivity).userToken
            genericAPIService.submitInitialDocs(submitInitialDocsReq,token)
            genericAPIService.setOnDataListener { responseBody ->
                CNProgressDialog.hideProgressDialog()
                val genericResponse = Gson().fromJson(responseBody, GenericResponse::class.java)
                if (genericResponse != null && genericResponse.status) {
                    /*if (genericResponse.isReferenceRedirection) {
                        val result = ContextCompat.checkSelfPermission(
                            activity as DashboardActivity,
                            Manifest.permission.READ_CONTACTS
                        )
                        if (result == PackageManager.PERMISSION_GRANTED) {
                            (activity as DashboardActivity).replaceFrag(
                                AddReferencesFragment(),
                                (activity as DashboardActivity).getString(R.string.add_references),
                                null
                            )
                        } else {
                            (activity as DashboardActivity).replaceFrag(
                                AddReferencesFragment(),
                                (activity as DashboardActivity).getString(R.string.add_references),
                                null
                            )
                        }
                        (activity as DashboardActivity).isFromApply = false
                        (activity as DashboardActivity).selectedTab =
                            (activity as DashboardActivity).getString(R.string.add_references)
                        (activity as DashboardActivity).navMenuAdapter.setSelectedTab((activity as DashboardActivity).selectedTab!!)
                        adgydeCounting((activity as DashboardActivity).getString(R.string.upload_document_first_time_user))
                    } else {
                        (activity as DashboardActivity).selectedTab =
                            (activity as DashboardActivity).getString(R.string.home)
                        (activity as DashboardActivity).navMenuAdapter.setSelectedTab((activity as DashboardActivity).selectedTab!!)
                        (activity as DashboardActivity).isFromApply = false
                        (activity as DashboardActivity).getApplyLoanData()
                    }*/
                            (activity as DashboardActivity).selectedTab = (activity as DashboardActivity).getString(R.string.home)
                    (activity as DashboardActivity).navMenuAdapter.setSelectedTab((activity as DashboardActivity).selectedTab!!)
                    (activity as DashboardActivity).isFromApply = false
                    (activity as DashboardActivity).getApplyLoanData(false)
                } else {
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

    private fun updateSelectedFilePath(fileUrl: String, isExternalDoc: Boolean, fileName: String?) {
        try {
            val textView: CNTextView? = null
            fileUploadAjaxRequest = FileUploadAjaxRequest()
            fileUploadAjaxRequest!!.userId = (activity as BaseActivity).userId
            var fileUrls: String? = ""
            fileUrls = fileUrl

            when (current_operation) {
                CP_PROOF_OF_ID -> {
                    if (uploadDocuments!!.addressProof != "") {
                        circularProgressBar?.progress = 50
                        txtProgressCircular?.text = "50%"
                    } else {
                        circularProgressBar?.progress = 35
                        txtProgressCircular?.text = "35%"
                    }
                    uploadDocuments!!.idProof = fileUrl
                    uploadDocuments!!.isIdProofExt = isExternalDoc
                    fileUploadAjaxRequest!!.urlsFor = Constants.FileUploadAjaxCallKeys.ID_PROOF
                    isIdSelected = true
                    checkIsPdf(fileUrl, ivId)
                }
                CP_PROOF_OF_ADDRESS -> {
                    if (uploadDocuments!!.idProof != "") {
                        circularProgressBar?.progress = 50
                        txtProgressCircular?.text = "50%"
                    } else {
                        circularProgressBar?.progress = 35
                        txtProgressCircular?.text = "35%"
                    }
                    uploadDocuments!!.addressProof = fileUrl
                    uploadDocuments!!.isAddressProofExt = isExternalDoc
                    if (uploadDocuments!!.addressProof2 != null && uploadDocuments!!.addressProof2 != "") fileUrls =
                        fileUrls + "," + uploadDocuments!!.addressProof2
                    fileUploadAjaxRequest!!.urlsFor = Constants.FileUploadAjaxCallKeys.ADDRESS_PROOF
                    addressUrl = fileUrls
                    isAdr1Selected = true
                    checkIsPdf(fileUrl, ivAdr1)
                }
                CP_PROOF_OF_ADDRESS_2 -> {
                    uploadDocuments!!.addressProof2 = fileUrl
                    fileUploadAjaxRequest!!.urlsFor = Constants.FileUploadAjaxCallKeys.ADDRESS_PROOF
                    if (uploadDocuments!!.addressProof != null && uploadDocuments!!.addressProof != "") fileUrls =
                        fileUrls + "," + uploadDocuments!!.addressProof
                    addressUrl = fileUrls
                    isAdr2Selected = true
                    checkIsPdf(fileUrl, ivAdr2)
                }
                CP_SAL_SLIP -> {
                    if (uploadDocuments!!.addressProof != "" && uploadDocuments!!.idProof != "") {
                        circularProgressBar?.progress = 65
                        txtProgressCircular?.text = "65%"
                    } else if (uploadDocuments!!.addressProof != "") {
                        circularProgressBar?.progress = 50
                        txtProgressCircular?.text = "50%"
                    } else if (uploadDocuments!!.idProof != "") {
                        circularProgressBar?.progress = 50
                        txtProgressCircular?.text = "50%"
                    } else {
                        circularProgressBar?.progress = 35
                        txtProgressCircular?.text = "35%"
                    }
                    isSlipSelected = true
                    uploadDocuments!!.salSlip = fileUrl
                    uploadDocuments!!.isSalSlipExt = isExternalDoc
                    fileUploadAjaxRequest!!.urlsFor = Constants.FileUploadAjaxCallKeys.SAL_SLIP
                    checkIsPdf(fileUrl, ivSlip)
                }
                CP_BANK_STMT -> {
                    uploadDocuments!!.bankStmt = fileUrl
                    uploadDocuments!!.isBankStmtExt = isExternalDoc
                    fileUploadAjaxRequest!!.urlsFor =
                        Constants.FileUploadAjaxCallKeys.BANK_STATEMENTS
                    if (uploadDocuments!!.bankStmt2 != null && uploadDocuments!!.bankStmt2 != "") fileUrls =
                        fileUrls + "," + uploadDocuments!!.bankStmt2
                    if (uploadDocuments!!.bankStmt3 != null && uploadDocuments!!.bankStmt3 != "") fileUrls =
                        fileUrls + "," + uploadDocuments!!.bankStmt3
                    statementsUrl = fileUrls

                    isBank1Selected = true
                    checkIsPdf(fileUrl, ivBank1)
                }
                CP_BANK_STMT_2 -> {
                    uploadDocuments!!.bankStmt2 = fileUrl
                    fileUploadAjaxRequest!!.urlsFor =
                        Constants.FileUploadAjaxCallKeys.BANK_STATEMENTS
                    if (uploadDocuments!!.bankStmt != null && uploadDocuments!!.bankStmt != "") fileUrls =
                        fileUrls + "," + uploadDocuments!!.bankStmt
                    if (uploadDocuments!!.bankStmt3 != null && uploadDocuments!!.bankStmt3 != "") fileUrls =
                        fileUrls + "," + uploadDocuments!!.bankStmt3
                    statementsUrl = fileUrls

                    isBank2Selected = true
                    checkIsPdf(fileUrl, ivBank2)
                }
                CP_BANK_STMT_3 -> {
                    circularProgressBar?.progress = 80
                    txtProgressCircular?.text = "80%"
                    uploadDocuments!!.bankStmt3 = fileUrl
                    fileUploadAjaxRequest!!.urlsFor =
                        Constants.FileUploadAjaxCallKeys.BANK_STATEMENTS
                    if (uploadDocuments!!.bankStmt2 != null && uploadDocuments!!.bankStmt2 != "") fileUrls =
                        fileUrls + "," + uploadDocuments!!.bankStmt2
                    if (uploadDocuments!!.bankStmt != null && uploadDocuments!!.bankStmt != "") fileUrls =
                        fileUrls + "," + uploadDocuments!!.bankStmt
                    statementsUrl = fileUrls

                    isBank3Selected = true
                    checkIsPdf(fileUrl, ivBank3)
                }
                CP_SIGN -> {
                    isSignSelected = true
                    circularProgressBar?.progress = 80
                    txtProgressCircular?.text = "80%"

                    uploadDocuments!!.customersignature = fileUrl
                    //uploadDocuments!!.isAddressProofExt = isExternalDoc
                    fileUploadAjaxRequest!!.urlsFor = Constants.FileUploadAjaxCallKeys.SIGNATURE
                    checkIsPdf(fileUrl, ivSign)
                }
                CP_PRESENT_ADDRESS_1 -> {
                    uploadDocuments!!.proof_present_address = fileUrl
                    if (uploadDocuments!!.proof_present_address2 != null && uploadDocuments!!.proof_present_address2 != "") fileUrls =
                        fileUrls + "," + uploadDocuments!!.proof_present_address2
                    fileUploadAjaxRequest!!.urlsFor =
                        Constants.FileUploadAjaxCallKeys.PRESENT_ADDRESS_PROOF
                    presentAddressUrl = fileUrls
                    isPrAdr1Selected = true
                    checkIsPdf(fileUrl, ivPrAdr1)
                }
                CP_PRESENT_ADDRESS_2 -> {
                    uploadDocuments!!.proof_present_address2 = fileUrl
                    uploadDocuments!!.isAddressProofExt = isExternalDoc
                    if (uploadDocuments!!.proof_present_address != null && uploadDocuments!!.proof_present_address != "") fileUrls =
                        fileUrls + "," + uploadDocuments!!.proof_present_address
                    fileUploadAjaxRequest!!.urlsFor =
                        Constants.FileUploadAjaxCallKeys.PRESENT_ADDRESS_PROOF
                    presentAddressUrl = fileUrls
                    isPrAdr2Selected = true
                    checkIsPdf(fileUrl, ivPrAdr2)
                }
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
        if (fileUrl.toUpperCase(Locale.ROOT).contains(".PDF")) {
            return true
        }
        return false
    }

    private fun updateFileAjaxCall() {
        val genericAPIService = GenericAPIService(context)
        fileUploadAjaxRequest?.deviceUniqueId = Utility.getInstance().getDeviceUniqueId(activity)
        Log.d("file ajax", Gson().toJson(fileUploadAjaxRequest))
        val token = (activity as BaseActivity).userToken
        genericAPIService.uploadFileToServer(fileUploadAjaxRequest, token)
    }

    /*private fun adgydeCounting(value: String) {
        val params = HashMap<String, Any>()
        val key = (activity as DashboardActivity).getString(R.string.upload_document_key)
        params[key] = value //patrametre name,value change to event
        //  AdGyde.onCountingEvent(key, params) //eventid,params
        AppsFlyerLib.getInstance().logEvent(activity as DashboardActivity, key, params)

        val logger = AppEventsLogger.newLogger(this.context)
        val bundle = Bundle()
        bundle.putString(key, value)
        logger.logEvent(getString(R.string.upload_document_key), bundle)

        BranchEvent("UploadedDocuments")
            .addCustomDataProperty("UploadedDocuments", "Uploaded_Documents")
            .setCustomerEventAlias("Uploaded_Documents")
            .logEvent(activity as DashboardActivity)
    }*/

    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        try {
            if (event?.action == MotionEvent.ACTION_DOWN) {
                val DRAWABLE_RIGHT: Int = 2

            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return false
    }

    private fun checkPermission(permission: String, requestCode: Int) {
        if (ContextCompat.checkSelfPermission(requireActivity(), permission) == PackageManager.PERMISSION_DENIED) {

            // Requesting the permission
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(permission), requestCode)
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
        if (requestCode == Constants.REQUEST_CODE_READ_EXTERNAL_STORAGE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openGallery(current_operation)
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

    fun getImageUri(inContext: Context, inImage: Bitmap): Uri? {
        val bytes = ByteArrayOutputStream()
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path =
            MediaStore.Images.Media.insertImage(inContext.contentResolver, inImage, "Title", null)
        return Uri.parse(path)
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