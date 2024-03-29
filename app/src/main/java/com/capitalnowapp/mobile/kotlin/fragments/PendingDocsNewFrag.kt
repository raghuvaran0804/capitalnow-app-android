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
import com.capitalnowapp.mobile.databinding.FragmentPendingDocsNewBinding
import com.capitalnowapp.mobile.kotlin.activities.DashboardActivity
import com.capitalnowapp.mobile.kotlin.activities.FaceMatchActivity
import com.capitalnowapp.mobile.kotlin.activities.UploadBankDetailsActivity
import com.capitalnowapp.mobile.kotlin.utils.FileUtils
import com.capitalnowapp.mobile.models.FileUploadAjaxRequest
import com.capitalnowapp.mobile.models.FileUploadResponse
import com.capitalnowapp.mobile.models.GetPendingDocReq
import com.capitalnowapp.mobile.models.GetPendingDocResponse
import com.capitalnowapp.mobile.models.UploadPendingDocReq
import com.capitalnowapp.mobile.models.UploadPendingDocReq_Local
import com.capitalnowapp.mobile.models.UploadPendingDocResponse
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


class PendingDocsNewFrag : Fragment() {
    companion object {
        private const val CAMERA_PERMISSION_CODE = 100
    }
    private var tempFile: File? = null
    private var salarySlipPassword: String? = null
    private var fileUploadAjaxRequest: FileUploadAjaxRequest? = null
    private var presentAddress1Url: String = ""
    private var presentAddress2Url: String = ""
    private var salarySlipUrl: String = ""
    private var proofOfIdUrl: String = ""
    private var POPAddress1URL: String = ""
    private var POPAddress2URL: String = ""
    private var ProofOfEmp1Url: String = ""
    private var ProofOfEmp2Url: String = ""
    private var LoanNoc1Url: String = ""
    private var LoanNoc2Url: String = ""
    private var BC1Url: String = ""
    private var BC2Url: String = ""
    private var BC3Url: String = ""
    private var BC4Url: String = ""
    private var BC5Url: String = ""
    private var BC6Url: String = ""
    private var selectedPresentAddress1Path: String? = ""
    private var selectedPresentAddress2Path: String? = ""
    private var selectedSalarySlipPath: String? = ""
    private var selectedProofOfIDPath: String? = ""
    private var selectedPOPAddressPath1: String? = ""
    private var selectedPOPAddressPath2: String? = ""
    private var selectedProofOfEmpPath1: String? = ""
    private var selectedProofOfEmpPath2: String? = ""
    private var selectedLoanNocPath1: String? = ""
    private var selectedLoanNocPath2: String? = ""
    private var selectedBounceClearPath1: String? = ""
    private var selectedBounceClearPath2: String? = ""
    private var selectedBounceClearPath3: String? = ""
    private var selectedBounceClearPath4: String? = ""
    private var selectedBounceClearPath5: String? = ""
    private var selectedBounceClearPath6: String? = ""
    private val PRESENT_ADDRESS_PROOF1_IMG = 1
    private val PRESENT_ADDRESS_PROOF2_IMG = 2
    private val SALARY_SLIP_IMG = 3
    private val PROOF_OF_ID_IMG = 4
    private val PROOF_OF_PRESENT_ADDRESS1 = 5
    private val PROOF_OF_PRESENT_ADDRESS2 = 6
    private val PROOF_OF_EMPLOYMENT1 = 7
    private val PROOF_OF_EMPLOYMENT2 = 8
    private val LOAN_NOC1 = 9
    private val LOAN_NOC2 = 10
    private val BOUNCE_CLEAR1 = 11
    private val BOUNCE_CLEAR2 = 12
    private val BOUNCE_CLEAR3 = 13
    private val BOUNCE_CLEAR4 = 14
    private val BOUNCE_CLEAR5 = 15
    private val BOUNCE_CLEAR6 = 16
    private var isSelectedPresentAddress1Path: Boolean = false
    private var isSelectedPresentAddress2Path: Boolean = false
    private var isSelectedSalarySlipPath: Boolean = false
    private var isSelectedProofOfIDPath: Boolean = false
    private var isSelectedPOPAddressPath1: Boolean = false
    private var isSelectedPOPAddressPath2: Boolean = false
    private var isSelectedProofOfEmpPath1: Boolean = false
    private var isSelectedProofOfEmpPath2: Boolean = false
    private var isSelectedLoanNocPath1: Boolean = false
    private var isSelectedLoanNocPath2: Boolean = false
    private var isSelectedBounceClearPath1: Boolean = false
    private var isSelectedBounceClearPath2: Boolean = false
    private var isSelectedBounceClearPath3: Boolean = false
    private var isSelectedBounceClearPath4: Boolean = false
    private var isSelectedBounceClearPath5: Boolean = false
    private var isSelectedBounceClearPath6: Boolean = false
    private var binding: FragmentPendingDocsNewBinding? = null
    private var current_operation: Int = 1
    private var mMakePhotoUri: Uri? = null
    private val REQUEST_IMAGE_CAPTURE = 102
    private var currenntPath = ""
    private var uploadPendingDocReq = UploadPendingDocReq()
    private var uploadPendingDocReq_Local = UploadPendingDocReq_Local()
    private var atleastOneValidDocPresent = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentPendingDocsNewBinding.inflate(inflater, container, false)
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
            TrackingUtil.pushEvent(obj, getString(R.string.pending_documents_page_landed))

            salarySlipPassword = binding?.etSlipPwd.toString().trim()
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
                    if (isSelectedPresentAddress1Path) {
                        isSelectedPresentAddress1Path = false
                        binding?.ivFirst?.setImageDrawable(
                            ContextCompat.getDrawable(
                                requireContext(),
                                R.drawable.ic_upload_doc_up_new
                            )
                        )
                        presentAddress1Url = ""
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
                    if (isSelectedPresentAddress2Path) {
                        isSelectedPresentAddress2Path = false
                        binding?.ivSecond?.setImageDrawable(
                            ContextCompat.getDrawable(
                                requireContext(),
                                R.drawable.ic_upload_doc_up_new
                            )
                        )
                        presentAddress2Url = ""
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
                    if (isSelectedSalarySlipPath) {
                        isSelectedSalarySlipPath = false
                        binding?.ivSalSlip?.setImageDrawable(
                            ContextCompat.getDrawable(
                                requireContext(),
                                R.drawable.ic_upload_doc_up_new
                            )
                        )
                        salarySlipUrl = ""
                    } else {
                        selectImage(SALARY_SLIP_IMG)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            binding?.ivProofOfId?.setOnClickListener {
                try {
                    checkPermission(
                        Manifest.permission.CAMERA, CAMERA_PERMISSION_CODE
                    )
                    checkPermission(
                        Manifest.permission.WRITE_EXTERNAL_STORAGE, CAMERA_PERMISSION_CODE
                    )
                    if (isSelectedProofOfIDPath) {
                        isSelectedProofOfIDPath = false
                        binding?.ivProofOfId?.setImageDrawable(
                            ContextCompat.getDrawable(
                                requireContext(), R.drawable.ic_upload_doc_up_new
                            )
                        )
                        proofOfIdUrl = ""
                    } else {
                        selectImage(PROOF_OF_ID_IMG)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            binding?.ivProofOfPAddress1?.setOnClickListener {
                try {
                    checkPermission(
                        Manifest.permission.CAMERA, CAMERA_PERMISSION_CODE
                    )
                    checkPermission(
                        Manifest.permission.WRITE_EXTERNAL_STORAGE, CAMERA_PERMISSION_CODE
                    )
                    if (isSelectedPOPAddressPath1) {
                        isSelectedPOPAddressPath1 = false
                        binding?.ivProofOfPAddress1?.setImageDrawable(
                            ContextCompat.getDrawable(
                                requireContext(), R.drawable.ic_upload_doc_up_new
                            )
                        )
                        POPAddress1URL = ""
                    } else {
                        selectImage(PROOF_OF_PRESENT_ADDRESS1)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            binding?.ivProofOfPAddress2?.setOnClickListener {
                try {
                    checkPermission(
                        Manifest.permission.CAMERA, CAMERA_PERMISSION_CODE
                    )
                    checkPermission(
                        Manifest.permission.WRITE_EXTERNAL_STORAGE, CAMERA_PERMISSION_CODE
                    )
                    if (isSelectedPOPAddressPath2) {
                        isSelectedPOPAddressPath2 = false
                        binding?.ivProofOfPAddress2?.setImageDrawable(
                            ContextCompat.getDrawable(
                                requireContext(), R.drawable.ic_upload_doc_up_new
                            )
                        )
                        POPAddress2URL = ""
                    } else {
                        selectImage(PROOF_OF_PRESENT_ADDRESS2)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            binding?.ivProofOfEmployment1?.setOnClickListener {
                try {
                    checkPermission(
                        Manifest.permission.CAMERA, CAMERA_PERMISSION_CODE
                    )
                    checkPermission(
                        Manifest.permission.WRITE_EXTERNAL_STORAGE, CAMERA_PERMISSION_CODE
                    )
                    if (isSelectedProofOfEmpPath1) {
                        isSelectedProofOfEmpPath1 = false
                        binding?.ivProofOfEmployment1?.setImageDrawable(
                            ContextCompat.getDrawable(
                                requireContext(), R.drawable.ic_upload_doc_up_new
                            )
                        )
                        ProofOfEmp1Url = ""
                    } else {
                        selectImage(PROOF_OF_EMPLOYMENT1)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            binding?.ivProofOfEmployment2?.setOnClickListener {
                try {
                    checkPermission(
                        Manifest.permission.CAMERA, CAMERA_PERMISSION_CODE
                    )
                    checkPermission(
                        Manifest.permission.WRITE_EXTERNAL_STORAGE, CAMERA_PERMISSION_CODE
                    )
                    if (isSelectedProofOfEmpPath2) {
                        isSelectedProofOfEmpPath2 = false
                        binding?.ivProofOfEmployment2?.setImageDrawable(
                            ContextCompat.getDrawable(
                                requireContext(), R.drawable.ic_upload_doc_up_new
                            )
                        )
                        ProofOfEmp2Url = ""
                    } else {
                        selectImage(PROOF_OF_EMPLOYMENT2)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            binding?.ivLoanNoc1?.setOnClickListener {
                try {
                    checkPermission(
                        Manifest.permission.CAMERA, CAMERA_PERMISSION_CODE
                    )
                    checkPermission(
                        Manifest.permission.WRITE_EXTERNAL_STORAGE, CAMERA_PERMISSION_CODE
                    )
                    if (isSelectedLoanNocPath1) {
                        isSelectedLoanNocPath1 = false
                        binding?.ivLoanNoc1?.setImageDrawable(
                            ContextCompat.getDrawable(
                                requireContext(), R.drawable.ic_upload_doc_up_new
                            )
                        )
                        LoanNoc1Url = ""
                    } else {
                        selectImage(LOAN_NOC1)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            binding?.ivLoanNoc2?.setOnClickListener {
                try {
                    checkPermission(
                        Manifest.permission.CAMERA, CAMERA_PERMISSION_CODE
                    )
                    checkPermission(
                        Manifest.permission.WRITE_EXTERNAL_STORAGE, CAMERA_PERMISSION_CODE
                    )
                    if (isSelectedLoanNocPath2) {
                        isSelectedLoanNocPath2 = false
                        binding?.ivLoanNoc2?.setImageDrawable(
                            ContextCompat.getDrawable(
                                requireContext(), R.drawable.ic_upload_doc_up_new
                            )
                        )
                        LoanNoc2Url = ""
                    } else {
                        selectImage(LOAN_NOC2)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            binding?.ivBCProof1?.setOnClickListener {
                try {
                    checkPermission(
                        Manifest.permission.CAMERA, CAMERA_PERMISSION_CODE
                    )
                    checkPermission(
                        Manifest.permission.WRITE_EXTERNAL_STORAGE, CAMERA_PERMISSION_CODE
                    )
                    if (isSelectedBounceClearPath1) {
                        isSelectedBounceClearPath1 = false
                        binding?.ivBCProof1?.setImageDrawable(
                            ContextCompat.getDrawable(
                                requireContext(), R.drawable.ic_upload_doc_up_new
                            )
                        )
                        BC1Url = ""
                    } else {
                        selectImage(BOUNCE_CLEAR1)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            binding?.ivBCProof2?.setOnClickListener {
                try {
                    checkPermission(
                        Manifest.permission.CAMERA, CAMERA_PERMISSION_CODE
                    )
                    checkPermission(
                        Manifest.permission.WRITE_EXTERNAL_STORAGE, CAMERA_PERMISSION_CODE
                    )
                    if (isSelectedBounceClearPath2) {
                        isSelectedBounceClearPath2 = false
                        binding?.ivBCProof2?.setImageDrawable(
                            ContextCompat.getDrawable(
                                requireContext(), R.drawable.ic_upload_doc_up_new
                            )
                        )
                        BC2Url = ""
                    } else {
                        selectImage(BOUNCE_CLEAR2)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            binding?.ivBCProof3?.setOnClickListener {
                try {
                    checkPermission(
                        Manifest.permission.CAMERA, CAMERA_PERMISSION_CODE
                    )
                    checkPermission(
                        Manifest.permission.WRITE_EXTERNAL_STORAGE, CAMERA_PERMISSION_CODE
                    )
                    if (isSelectedBounceClearPath3) {
                        isSelectedBounceClearPath3 = false
                        binding?.ivBCProof3?.setImageDrawable(
                            ContextCompat.getDrawable(
                                requireContext(), R.drawable.ic_upload_doc_up_new
                            )
                        )
                        BC3Url = ""
                    } else {
                        selectImage(BOUNCE_CLEAR3)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            binding?.ivBCProof4?.setOnClickListener {
                try {
                    checkPermission(
                        Manifest.permission.CAMERA, CAMERA_PERMISSION_CODE
                    )
                    checkPermission(
                        Manifest.permission.WRITE_EXTERNAL_STORAGE, CAMERA_PERMISSION_CODE
                    )
                    if (isSelectedBounceClearPath4) {
                        isSelectedBounceClearPath4 = false
                        binding?.ivBCProof4?.setImageDrawable(
                            ContextCompat.getDrawable(
                                requireContext(), R.drawable.ic_upload_doc_up_new
                            )
                        )
                        BC4Url = ""
                    } else {
                        selectImage(BOUNCE_CLEAR4)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            binding?.ivBCProof5?.setOnClickListener {
                try {
                    checkPermission(
                        Manifest.permission.CAMERA, CAMERA_PERMISSION_CODE
                    )
                    checkPermission(
                        Manifest.permission.WRITE_EXTERNAL_STORAGE, CAMERA_PERMISSION_CODE
                    )
                    if (isSelectedBounceClearPath5) {
                        isSelectedBounceClearPath5 = false
                        binding?.ivBCProof5?.setImageDrawable(
                            ContextCompat.getDrawable(
                                requireContext(), R.drawable.ic_upload_doc_up_new
                            )
                        )
                        BC5Url = ""
                    } else {
                        selectImage(BOUNCE_CLEAR5)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            binding?.ivBCProof6?.setOnClickListener {
                try {
                    checkPermission(
                        Manifest.permission.CAMERA, CAMERA_PERMISSION_CODE
                    )
                    checkPermission(
                        Manifest.permission.WRITE_EXTERNAL_STORAGE, CAMERA_PERMISSION_CODE
                    )
                    if (isSelectedBounceClearPath6) {
                        isSelectedBounceClearPath6 = false
                        binding?.ivBCProof6?.setImageDrawable(
                            ContextCompat.getDrawable(
                                requireContext(), R.drawable.ic_upload_doc_up_new
                            )
                        )
                        BC6Url = ""
                    } else {
                        selectImage(BOUNCE_CLEAR6)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            binding?.tvBankStatement?.setOnClickListener {

                val obj = JSONObject()
                try {
                    obj.put("cnid", (activity as BaseActivity).userDetails.qcId)
                    obj.put(getString(R.string.interaction_type), "Bank Account Statement Clicked")
                } catch (e: JSONException) {
                    throw RuntimeException(e)
                }
                TrackingUtil.pushEvent(obj, getString(R.string.pending_documents_page_interacted))

                val intent = Intent(activity, UploadBankDetailsActivity::class.java)
                (activity as BaseActivity).sharedPreferences.putBoolean("fromDocs", true)
                startActivity(intent)
            }

            binding?.tvSignature?.setOnClickListener {

                val obj = JSONObject()
                try {
                    obj.put("cnid", (activity as BaseActivity).userDetails.qcId)
                    obj.put(getString(R.string.interaction_type), "Customer Signature Clicked")
                } catch (e: JSONException) {
                    throw RuntimeException(e)
                }
                TrackingUtil.pushEvent(obj, getString(R.string.pending_documents_page_interacted))

                (activity as BaseActivity).sharedPreferences.putBoolean("fromDocs", true)
                (activity as DashboardActivity).replaceFrag(
                    SignatureConsentFragment(), "Signature Consent", null
                )
            }

            binding?.tvVideoKYC?.setOnClickListener {

                val obj = JSONObject()
                try {
                    obj.put("cnid", (activity as BaseActivity).userDetails.qcId)
                    obj.put(getString(R.string.interaction_type), "Video KYC Clicked")
                } catch (e: JSONException) {
                    throw RuntimeException(e)
                }
                TrackingUtil.pushEvent(obj, getString(R.string.pending_documents_page_interacted))

                val intent = Intent(activity, FaceMatchActivity::class.java)
                (activity as BaseActivity).sharedPreferences.putBoolean("fromDocs", true)
                startActivity(intent)
            }

            binding?.tvAddReferences?.setOnClickListener {

                val obj = JSONObject()
                try {
                    obj.put("cnid", (activity as BaseActivity).userDetails.qcId)
                    obj.put(getString(R.string.interaction_type), "Add References Clicked")
                } catch (e: JSONException) {
                    throw RuntimeException(e)
                }
                TrackingUtil.pushEvent(obj, getString(R.string.pending_documents_page_interacted))

                (activity as BaseActivity).sharedPreferences.putBoolean("fromDocs", true)
                (activity as DashboardActivity).replaceFrag(
                    ReferencesNewFragment(), "Add References", null
                )
            }

            binding?.tvUploadPendingDocs?.setOnClickListener {

                val obj = JSONObject()
                try {
                    obj.put("cnid", (activity as BaseActivity).userDetails.qcId)
                    obj.put(getString(R.string.interaction_type), "Upload Button Clicked")
                } catch (e: JSONException) {
                    throw RuntimeException(e)
                }
                TrackingUtil.pushEvent(obj, getString(R.string.pending_documents_page_interacted))
                if (atleastOneValidDocPresent || validatePendingDocs()) {
                    uploadPendingDocs()
                } else {
                    //(activity as BaseActivity).displayToast(resources.getString(R.string.upload_docs_validation_msg_1))
                    Toast.makeText(
                        context,
                        resources.getString(R.string.upload_docs_validation_msg_1),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            val data =
                (activity as DashboardActivity).sharedPreferences.getString(Constants.PENDING_DOCS_DATA)
            if (data != null && data.isNotEmpty()) {
                uploadPendingDocReq_Local =
                    Gson().fromJson(data, UploadPendingDocReq_Local::class.java)
                setLocalData()
            }

            binding!!.etProofOfPAddress1Pwd.addTextChangedListener(object : TextWatcher {
                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                }

                override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                }

                override fun afterTextChanged(s: Editable) {
                    uploadPendingDocReq_Local.proofAddressPass = s.toString()
                }
            })
            binding!!.etSlipPwd.addTextChangedListener(object : TextWatcher {
                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                }

                override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                }

                override fun afterTextChanged(s: Editable) {
                    uploadPendingDocReq_Local.latSalPass = s.toString()

                }
            })
            binding!!.etProofOfEmploymentPwd.addTextChangedListener(object : TextWatcher {
                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                }

                override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                }

                override fun afterTextChanged(s: Editable) {
                    uploadPendingDocReq_Local.proofEmpPass = s.toString()
                }
            })

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setLocalData() {
        if (uploadPendingDocReq_Local.preAddress1?.isNotEmpty() == true) {
            binding?.ivFirst?.setImageDrawable(
                ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.ic_upload_doc_image_new
                )
            )
            isSelectedPresentAddress1Path = true
            presentAddress1Url = uploadPendingDocReq_Local.preAddress1!!
        }
        if (uploadPendingDocReq_Local.preAddress2?.isNotEmpty() == true) {
            binding?.ivSecond?.setImageDrawable(
                ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.ic_upload_doc_image_new
                )
            )
            isSelectedPresentAddress2Path = true
            presentAddress2Url = uploadPendingDocReq_Local.preAddress2!!
        }
        if (uploadPendingDocReq_Local.latSalSlip?.isNotEmpty() == true) {
            binding?.ivSalSlip?.setImageDrawable(
                ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.ic_upload_doc_image_new
                )
            )
            isSelectedSalarySlipPath = true
            salarySlipUrl = uploadPendingDocReq_Local.latSalSlip!!
        }
        if (uploadPendingDocReq_Local.latSalPass?.isNotEmpty() == true) {
            binding?.etSlipPwd?.setText(uploadPendingDocReq_Local.proofAddressPass)
        }
        if (uploadPendingDocReq_Local.proofOfId?.isNotEmpty() == true) {
            binding?.ivProofOfId?.setImageDrawable(
                ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.ic_upload_doc_image_new
                )
            )
            isSelectedProofOfIDPath = true
            proofOfIdUrl = uploadPendingDocReq_Local.proofOfId!!
        }

        if (uploadPendingDocReq_Local.proofOfPerAddress1?.isNotEmpty() == true) {
            binding?.ivProofOfPAddress1?.setImageDrawable(
                ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.ic_upload_doc_image_new
                )
            )
            isSelectedPOPAddressPath1 = true
            POPAddress1URL = uploadPendingDocReq_Local.proofOfPerAddress1!!
        }
        if (uploadPendingDocReq_Local.proofOfPerAddress2?.isNotEmpty() == true) {
            binding?.ivProofOfPAddress2?.setImageDrawable(
                ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.ic_upload_doc_image_new
                )
            )
            isSelectedPOPAddressPath2 = true
            POPAddress2URL = uploadPendingDocReq_Local.proofOfPerAddress2!!
        }
        if (uploadPendingDocReq_Local.proofAddressPass?.isNotEmpty() == true) {
            binding?.etProofOfPAddress1Pwd?.setText(uploadPendingDocReq_Local.proofAddressPass)
        }
        if (uploadPendingDocReq_Local.proofOfEmp1?.isNotEmpty() == true) {
            binding?.ivProofOfEmployment1?.setImageDrawable(
                ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.ic_upload_doc_image_new
                )
            )
            isSelectedProofOfEmpPath1 = true
            ProofOfEmp1Url = uploadPendingDocReq_Local.proofOfEmp1!!
        }
        if (uploadPendingDocReq_Local.proofOfEmp2?.isNotEmpty() == true) {
            binding?.ivProofOfEmployment2?.setImageDrawable(
                ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.ic_upload_doc_image_new
                )
            )
            isSelectedProofOfEmpPath2 = true
            ProofOfEmp2Url = uploadPendingDocReq_Local.proofOfEmp2!!
        }
        if (uploadPendingDocReq_Local.proofEmpPass?.isNotEmpty() == true) {
            binding?.etProofOfEmploymentPwd?.setText(uploadPendingDocReq_Local.proofEmpPass)
        }
        if (uploadPendingDocReq_Local.loanNoc1?.isNotEmpty() == true) {
            binding?.ivLoanNoc1?.setImageDrawable(
                ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.ic_upload_doc_image_new
                )
            )
            isSelectedLoanNocPath1 = true
            LoanNoc1Url = uploadPendingDocReq_Local.loanNoc1!!
        }
        if (uploadPendingDocReq_Local.loanNoc2?.isNotEmpty() == true) {
            binding?.ivLoanNoc2?.setImageDrawable(
                ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.ic_upload_doc_image_new
                )
            )
            isSelectedLoanNocPath2 = true
            LoanNoc2Url = uploadPendingDocReq_Local.loanNoc2!!
        }
        if (uploadPendingDocReq_Local.bounceClearanceProof1?.isNotEmpty() == true) {
            binding?.ivBCProof1?.setImageDrawable(
                ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.ic_upload_doc_image_new
                )
            )
            isSelectedBounceClearPath1 = true
            BC1Url = uploadPendingDocReq_Local.bounceClearanceProof1!!
        }
        if (uploadPendingDocReq_Local.bounceClearanceProof2?.isNotEmpty() == true) {
            binding?.ivBCProof2?.setImageDrawable(
                ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.ic_upload_doc_image_new
                )
            )
            isSelectedBounceClearPath2 = true
            BC2Url = uploadPendingDocReq_Local.bounceClearanceProof2!!
        }
        if (uploadPendingDocReq_Local.bounceClearanceProof3?.isNotEmpty() == true) {
            binding?.ivBCProof3?.setImageDrawable(
                ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.ic_upload_doc_image_new
                )
            )
            isSelectedBounceClearPath3 = true
            BC3Url = uploadPendingDocReq_Local.bounceClearanceProof3!!
        }
        if (uploadPendingDocReq_Local.bounceClearanceProof4?.isNotEmpty() == true) {
            binding?.ivBCProof4?.setImageDrawable(
                ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.ic_upload_doc_image_new
                )
            )
            isSelectedBounceClearPath4 = true
            BC4Url = uploadPendingDocReq_Local.bounceClearanceProof4!!
        }
        if (uploadPendingDocReq_Local.bounceClearanceProof5?.isNotEmpty() == true) {
            binding?.ivBCProof5?.setImageDrawable(
                ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.ic_upload_doc_image_new
                )
            )
            isSelectedBounceClearPath5 = true
            BC5Url = uploadPendingDocReq_Local.bounceClearanceProof5!!
        }
        if (uploadPendingDocReq_Local.bounceClearanceProof6?.isNotEmpty() == true) {
            binding?.ivBCProof6?.setImageDrawable(
                ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.ic_upload_doc_image_new
                )
            )
            isSelectedBounceClearPath6 = true
            BC6Url = uploadPendingDocReq_Local.bounceClearanceProof6!!
        }
    }

    private fun validatePendingDocs(): Boolean {
        try {
            return (salarySlipUrl.trim().isNotEmpty() || presentAddress1Url.trim()
                .isNotEmpty() || presentAddress2Url.trim().isNotEmpty() || proofOfIdUrl.trim()
                ?.isNotEmpty()!! || POPAddress1URL.trim()
                .isNotEmpty() || POPAddress2URL.trim().isNotEmpty() || ProofOfEmp1Url.trim()
                .isNotEmpty() || ProofOfEmp2Url.trim().isNotEmpty() || LoanNoc1Url.trim()
                .isNotEmpty() || LoanNoc2Url.trim().isNotEmpty() || BC1Url.trim()
                .isNotEmpty() || BC2Url.trim().isNotEmpty() || BC3Url.trim()
                .isNotEmpty() || BC4Url.trim().isNotEmpty() || BC5Url.trim()
                .isNotEmpty() || BC6Url.trim().isNotEmpty())
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return false
    }

    private fun uploadPendingDocs() {
        try {
            val genericAPIService = GenericAPIService(activity, 0)
            uploadPendingDocReq = UploadPendingDocReq()
            val token = (activity as BaseActivity).userToken
            var POIDurl = ""
            var POPAUrl = ""
            var pOEmp = ""
            var loanNoc = ""
            var bounceClearUrl = ""
            var presentAddressUrl = ""
            var salSlipUrl = ""
            if (presentAddress1Url.isNotEmpty() && presentAddress2Url.isNotEmpty()) {
                presentAddressUrl = "$presentAddress1Url,$presentAddress2Url"
            } else if (presentAddress1Url.isNotEmpty()) {
                presentAddressUrl = presentAddress1Url
            } else if (presentAddress2Url.isNotEmpty()) {
                presentAddressUrl = presentAddress2Url
            }
            if (salarySlipUrl.isNotEmpty()) {
                salSlipUrl = salarySlipUrl
            }
            if (proofOfIdUrl.isNotEmpty()) {
                POIDurl = proofOfIdUrl
            }
            if (POPAddress1URL.isNotEmpty() && POPAddress2URL.isNotEmpty()) {
                POPAUrl = "$POPAddress1URL,$POPAddress2URL"
            } else if (POPAddress1URL.isNotEmpty()) {
                POPAUrl = POPAddress1URL
            } else if (POPAddress2URL.isNotEmpty()) {
                POPAUrl = POPAddress2URL
            }
            if (ProofOfEmp1Url.isNotEmpty() && ProofOfEmp2Url.isNotEmpty()) {
                pOEmp = "$ProofOfEmp1Url,$ProofOfEmp2Url"
            } else if (ProofOfEmp1Url.isNotEmpty()) {
                pOEmp = ProofOfEmp1Url
            } else if (ProofOfEmp2Url.isNotEmpty()) {
                pOEmp = ProofOfEmp2Url
            }
            if (LoanNoc1Url.isNotEmpty() && LoanNoc2Url.isNotEmpty()) {
                loanNoc = "$LoanNoc1Url,$LoanNoc2Url"
            } else if (LoanNoc1Url.isNotEmpty()) {
                loanNoc = LoanNoc1Url
            } else if (LoanNoc2Url.isNotEmpty()) {
                loanNoc = LoanNoc2Url
            }
            if (BC1Url.isNotEmpty() && BC2Url.isNotEmpty() && BC3Url.isNotEmpty() && BC4Url.isNotEmpty() && BC5Url.isNotEmpty() && BC6Url.isNotEmpty()) {
                bounceClearUrl = "$BC1Url,$BC2Url,$BC2Url,$BC4Url,$BC5Url,$BC6Url"
            } else if (BC1Url.isNotEmpty()) {
                bounceClearUrl = BC1Url
            } else if (BC2Url.isNotEmpty()) {
                bounceClearUrl = BC2Url
            } else if (BC3Url.isNotEmpty()) {
                bounceClearUrl = BC3Url
            } else if (BC4Url.isNotEmpty()) {
                bounceClearUrl = BC4Url
            } else if (BC5Url.isNotEmpty()) {
                bounceClearUrl = BC5Url
            } else if (BC6Url.isNotEmpty()) {
                bounceClearUrl = BC6Url
            }
            uploadPendingDocReq.preAddress = presentAddressUrl
            uploadPendingDocReq.latSalSlip = salSlipUrl
            uploadPendingDocReq.latSalPass = binding?.etSlipPwd?.text.toString().trim()
            uploadPendingDocReq.proofOfId = POIDurl
            uploadPendingDocReq.proofOfPerAddress = POPAUrl
            uploadPendingDocReq.proofAddressPass =
                binding?.etProofOfPAddress1Pwd?.text.toString().trim()
            uploadPendingDocReq.proofOfEmp = pOEmp
            uploadPendingDocReq.proofEmpPass =
                binding?.etProofOfEmploymentPwd?.text.toString().trim()
            uploadPendingDocReq.loanNoc = loanNoc
            uploadPendingDocReq.bounceClearanceProof = bounceClearUrl
            genericAPIService.uploadPendingDocuments(uploadPendingDocReq, token)
            genericAPIService.setOnDataListener { responseBody ->
                val uploadPendingDocResponse =
                    Gson().fromJson(responseBody, UploadPendingDocResponse::class.java)
                if (uploadPendingDocResponse != null && uploadPendingDocResponse.status == true) {
                    //(activity as DashboardActivity).getApplyLoanData(true)
                    uploadPendingDocReq_Local = UploadPendingDocReq_Local()
                    saveData()
                    val intent = Intent(context, DashboardActivity::class.java)
                    intent.flags =
                        Intent.FLAG_ACTIVITY_NO_ANIMATION or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                } else {
                    Toast.makeText(context, uploadPendingDocResponse.message, Toast.LENGTH_SHORT)
                        .show()
                }
                genericAPIService.setOnErrorListener {
                    fun errorData(throwable: Throwable?) {
                        //Failure
                    }
                }
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    private fun getPendingDocuments() {
        try {

            val genericAPIService = GenericAPIService(activity, 0)
            val getPendingDocReq = GetPendingDocReq()
            val token = (activity as BaseActivity).userToken
            genericAPIService.getPendingDocuments(getPendingDocReq, token)
            genericAPIService.setOnDataListener { responseBody ->

                val getPendingDocResponse =
                    Gson().fromJson(responseBody, GetPendingDocResponse::class.java)
                if (getPendingDocResponse != null && getPendingDocResponse.status == true && getPendingDocResponse.data?.isDocRequired == true) {
                    setPendingDocsData(getPendingDocResponse)
                } else {

                }
                genericAPIService.setOnErrorListener {
                    fun errorData(throwable: Throwable?) {

                        //Failure
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setPendingDocsData(pendingDocResponse: GetPendingDocResponse?) {
        try {
            binding?.tvTitle?.text = pendingDocResponse?.data?.title
            val requiredDocs = pendingDocResponse?.data?.requiredDocuments
            for (pendingDocs in requiredDocs!!.withIndex()) {
                if (pendingDocs.value.key.equals("lat_sal_slip")) {
                    binding?.llLatestSalarySlip?.visibility = View.VISIBLE
                    binding?.tvLatestSalarySlip?.text = pendingDocs.value.label
                    binding?.tvNote?.visibility = View.VISIBLE
                    binding?.tvNoteText?.visibility = View.VISIBLE
                    binding?.tvUploadPendingDocs?.visibility = View.VISIBLE
                }
                if (pendingDocs.value.key.equals("pre_address")) {
                    binding?.llPresentAddress?.visibility = View.VISIBLE
                    binding?.tvPresentAddressTitle?.text = pendingDocs.value.label
                    binding?.tvPresentAddressText?.text = pendingDocs.value.subLabel.toString()
                    binding?.tvNote?.visibility = View.VISIBLE
                    binding?.tvNoteText?.visibility = View.VISIBLE
                    binding?.tvUploadPendingDocs?.visibility = View.VISIBLE
                }
                if (pendingDocs.value.key.equals("proof_of_id")) {
                    binding?.llProofOfId?.visibility = View.VISIBLE
                    binding?.tvProofOfId?.text = pendingDocs.value.label
                    binding?.tvProofOfIdText?.text = pendingDocs.value.subLabel.toString()
                    binding?.tvNote?.visibility = View.VISIBLE
                    binding?.tvNoteText?.visibility = View.VISIBLE
                    binding?.tvUploadPendingDocs?.visibility = View.VISIBLE
                }
                if (pendingDocs.value.key.equals("proof_of_per_address")) {
                    binding?.llProofOfPAddress?.visibility = View.VISIBLE
                    binding?.tvProofOfPAddress?.text = pendingDocs.value.label
                    binding?.tvProofOfPAddressText?.text = pendingDocs.value.subLabel.toString()
                    binding?.tvNote?.visibility = View.VISIBLE
                    binding?.tvNoteText?.visibility = View.VISIBLE
                    binding?.tvUploadPendingDocs?.visibility = View.VISIBLE
                }
                if (pendingDocs.value.key.equals("proof_of_emp")) {
                    binding?.llProofOfEmployment?.visibility = View.VISIBLE
                    binding?.tvProofOfEmployment?.text = pendingDocs.value.label
                    binding?.tvProofOfEmploymentText?.text = pendingDocs.value.subLabel.toString()
                    binding?.tvUploadPendingDocs?.visibility = View.VISIBLE
                    binding?.tvNote?.visibility = View.VISIBLE
                    binding?.tvNoteText?.visibility = View.VISIBLE
                }
                if (pendingDocs.value.key.equals("bounce_clearance_proof")) {
                    binding?.llBCProof?.visibility = View.VISIBLE
                    binding?.tvBCProof?.text = pendingDocs.value.label
                    binding?.tvNote?.visibility = View.VISIBLE
                    binding?.tvNoteText?.visibility = View.VISIBLE
                    binding?.tvUploadPendingDocs?.visibility = View.VISIBLE
                }
                if (pendingDocs.value.key.equals("loan_noc")) {
                    binding?.llLoanNoc?.visibility = View.VISIBLE
                    binding?.tvLoanNoc?.text = pendingDocs.value.label
                    binding?.tvNote?.visibility = View.VISIBLE
                    binding?.tvNoteText?.visibility = View.VISIBLE
                    binding?.tvUploadPendingDocs?.visibility = View.VISIBLE
                }
            }
            val redirectionDocs = pendingDocResponse?.data?.redirectionDocuments
            for (pendingRedirectionDocs in redirectionDocs!!.withIndex()) {
                if (pendingRedirectionDocs.value.key.equals("bnk_statement")) {
                    if (pendingRedirectionDocs.value.docStatus == 0) {
                        binding?.tvBankStatement?.visibility = View.VISIBLE
                        binding?.llReComplete?.visibility = View.VISIBLE
                    } else {
                        atleastOneValidDocPresent = true
                        binding?.tvBankStatementSuccess?.visibility = View.VISIBLE
                        binding?.tvBankStatement?.visibility = View.GONE
                        binding?.llReComplete?.visibility = View.VISIBLE
                    }
                }
                if (pendingRedirectionDocs.value.key.equals("cus_sign")) {
                    if (pendingRedirectionDocs.value.docStatus == 0) {
                        binding?.tvSignature?.visibility = View.VISIBLE
                        binding?.llReComplete?.visibility = View.VISIBLE
                    } else {
                        atleastOneValidDocPresent = true
                        binding?.tvSignatureSuccess?.visibility = View.VISIBLE
                        binding?.tvSignature?.visibility = View.GONE
                        binding?.llReComplete?.visibility = View.VISIBLE
                    }
                }
                if (pendingRedirectionDocs.value.key.equals("video_kyc")) {
                    if (pendingRedirectionDocs.value.docStatus == 0) {
                        binding?.tvVideoKYC?.visibility = View.VISIBLE
                        binding?.llReComplete?.visibility = View.VISIBLE
                    } else {
                        atleastOneValidDocPresent = true
                        binding?.tvVideoKYCSuccess?.visibility = View.VISIBLE
                        binding?.tvVideoKYC?.visibility = View.GONE
                        binding?.llReComplete?.visibility = View.VISIBLE
                    }
                }
                if (pendingRedirectionDocs.value.key.equals("contact_reference")) {
                    if (pendingRedirectionDocs.value.docStatus == 0) {
                        binding?.tvAddReferences?.visibility = View.VISIBLE
                        binding?.llReComplete?.visibility = View.VISIBLE
                    } else {
                        atleastOneValidDocPresent = true
                        binding?.tvAddReferencesSuccess?.visibility = View.VISIBLE
                        binding?.tvAddReferences?.visibility = View.GONE
                        binding?.llReComplete?.visibility = View.VISIBLE
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    private fun checkPermission(permission: String, requestCode: Int) {
        if (ContextCompat.checkSelfPermission(
                requireActivity(), permission
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
                            )/*val bitmap = BitmapFactory.decodeFile(photoFile.path)
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
                                "JPEG", ignoreCase = true
                            ) || fileType.equals("PNG", ignoreCase = true) || fileType.equals(
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
                                        selectedDocumentUri, (activity as DashboardActivity)
                                    )
                                }/*selectedFilePath = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
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
                                            selectedPresentAddress1Path = selectedFilePath
                                            isSelectedPresentAddress1Path = true
                                            if (isSelectedPresentAddress1Path) {
                                                binding?.ivFirst?.setImageDrawable(
                                                    ContextCompat.getDrawable(
                                                        requireContext(),
                                                        R.drawable.ic_upload_doc_image_new
                                                    )
                                                )
                                                uploadPendingDocReq_Local.preAddress1 = ""
                                                if(selectedPresentAddress1Path != null && selectedPresentAddress1Path != "") {
                                                    saveData()
                                                }
                                            }
                                            uploadFileWithProgress(
                                                it, false, selectedPresentAddress1Path!!.substring(
                                                    selectedPresentAddress1Path!!.lastIndexOf(
                                                        "/"
                                                    ) + 1
                                                )
                                            )
                                        }

                                        PRESENT_ADDRESS_PROOF2_IMG -> {
                                            selectedPresentAddress2Path = selectedFilePath
                                            isSelectedPresentAddress2Path = true
                                            if (isSelectedPresentAddress2Path) {
                                                binding?.ivSecond?.setImageDrawable(
                                                    ContextCompat.getDrawable(
                                                        requireContext(),
                                                        R.drawable.ic_upload_doc_image_new
                                                    )
                                                )
                                                uploadPendingDocReq_Local.preAddress2 = ""
                                                if(selectedPresentAddress2Path != null && selectedPresentAddress2Path != "") {
                                                    saveData()
                                                }
                                            }
                                            uploadFileWithProgress(
                                                it, false, selectedPresentAddress2Path!!.substring(
                                                    selectedPresentAddress2Path!!.lastIndexOf(
                                                        "/"
                                                    ) + 1
                                                )
                                            )
                                        }

                                        SALARY_SLIP_IMG -> {
                                            selectedSalarySlipPath = selectedFilePath
                                            isSelectedSalarySlipPath = true
                                            if (isSelectedSalarySlipPath) {
                                                binding?.ivSalSlip?.setImageDrawable(
                                                    ContextCompat.getDrawable(
                                                        requireContext(),
                                                        R.drawable.ic_upload_doc_image_new
                                                    )
                                                )
                                                uploadPendingDocReq_Local.preAddress2 = ""

                                                if(selectedSalarySlipPath != null && selectedSalarySlipPath != "") {
                                                    saveData()
                                                }

                                            }
                                            uploadFileWithProgress(
                                                it, false, selectedSalarySlipPath!!.substring(
                                                    selectedSalarySlipPath!!.lastIndexOf(
                                                        "/"
                                                    ) + 1
                                                )
                                            )
                                        }

                                        PROOF_OF_ID_IMG -> {
                                            selectedProofOfIDPath = selectedFilePath
                                            isSelectedProofOfIDPath = true
                                            if (isSelectedProofOfIDPath) {
                                                binding?.ivProofOfId?.setImageDrawable(
                                                    ContextCompat.getDrawable(
                                                        requireContext(),
                                                        R.drawable.ic_upload_doc_image_new
                                                    )
                                                )
                                                uploadPendingDocReq_Local.proofOfId = ""
                                                if(selectedProofOfIDPath != null && selectedProofOfIDPath != "") {
                                                    saveData()
                                                }

                                            }
                                            uploadFileWithProgress(
                                                it, false, selectedProofOfIDPath!!.substring(
                                                    selectedProofOfIDPath!!.lastIndexOf(
                                                        "/"
                                                    ) + 1
                                                )
                                            )
                                        }

                                        PROOF_OF_PRESENT_ADDRESS1 -> {
                                            selectedPOPAddressPath1 = selectedFilePath
                                            isSelectedPOPAddressPath1 = true
                                            if (isSelectedPOPAddressPath1) {
                                                binding?.ivProofOfPAddress1?.setImageDrawable(
                                                    ContextCompat.getDrawable(
                                                        requireContext(),
                                                        R.drawable.ic_upload_doc_image_new
                                                    )
                                                )
                                                uploadPendingDocReq_Local.proofOfPerAddress1 = ""
                                                if(selectedPOPAddressPath1 != null && selectedPOPAddressPath1 != "") {
                                                    saveData()
                                                }
                                            }
                                            uploadFileWithProgress(
                                                it, false, selectedPOPAddressPath1!!.substring(
                                                    selectedPOPAddressPath1!!.lastIndexOf(
                                                        "/"
                                                    ) + 1
                                                )
                                            )
                                        }

                                        PROOF_OF_PRESENT_ADDRESS2 -> {
                                            selectedPOPAddressPath2 = selectedFilePath
                                            isSelectedPOPAddressPath2 = true
                                            if (isSelectedPOPAddressPath2) {
                                                binding?.ivProofOfPAddress2?.setImageDrawable(
                                                    ContextCompat.getDrawable(
                                                        requireContext(),
                                                        R.drawable.ic_upload_doc_image_new
                                                    )
                                                )
                                                uploadPendingDocReq_Local.proofOfPerAddress2 = ""
                                                if(selectedPOPAddressPath2 != null && selectedPOPAddressPath2 != "") {
                                                    saveData()
                                                }
                                            }
                                            uploadFileWithProgress(
                                                it, false, selectedPOPAddressPath2!!.substring(
                                                    selectedPOPAddressPath2!!.lastIndexOf(
                                                        "/"
                                                    ) + 1
                                                )
                                            )
                                        }

                                        PROOF_OF_EMPLOYMENT1 -> {
                                            selectedProofOfEmpPath1 = selectedFilePath
                                            isSelectedProofOfEmpPath1 = true
                                            if (isSelectedProofOfEmpPath1) {
                                                binding?.ivProofOfEmployment1?.setImageDrawable(
                                                    ContextCompat.getDrawable(
                                                        requireContext(),
                                                        R.drawable.ic_upload_doc_image_new
                                                    )
                                                )
                                                uploadPendingDocReq_Local.proofOfEmp1 = ""
                                                if(selectedProofOfEmpPath1 != null && selectedProofOfEmpPath1 != "") {
                                                    saveData()
                                                }

                                            }
                                            uploadFileWithProgress(
                                                it, false, selectedProofOfEmpPath1!!.substring(
                                                    selectedProofOfEmpPath1!!.lastIndexOf(
                                                        "/"
                                                    ) + 1
                                                )
                                            )
                                        }

                                        PROOF_OF_EMPLOYMENT2 -> {
                                            selectedProofOfEmpPath2 = selectedFilePath
                                            isSelectedProofOfEmpPath2 = true
                                            if (isSelectedProofOfEmpPath2) {
                                                binding?.ivProofOfEmployment2?.setImageDrawable(
                                                    ContextCompat.getDrawable(
                                                        requireContext(),
                                                        R.drawable.ic_upload_doc_image_new
                                                    )
                                                )
                                                uploadPendingDocReq_Local.proofOfEmp2 = ""
                                                if(selectedProofOfEmpPath2 != null && selectedProofOfEmpPath2 != "") {
                                                    saveData()
                                                }
                                            }
                                            uploadFileWithProgress(
                                                it, false, selectedProofOfEmpPath2!!.substring(
                                                    selectedProofOfEmpPath2!!.lastIndexOf(
                                                        "/"
                                                    ) + 1
                                                )
                                            )
                                        }

                                        LOAN_NOC1 -> {
                                            selectedLoanNocPath1 = selectedFilePath
                                            isSelectedLoanNocPath1 = true
                                            if (isSelectedLoanNocPath1) {
                                                binding?.ivLoanNoc1?.setImageDrawable(
                                                    ContextCompat.getDrawable(
                                                        requireContext(),
                                                        R.drawable.ic_upload_doc_image_new
                                                    )
                                                )
                                                uploadPendingDocReq_Local.loanNoc1 = ""
                                                if(selectedLoanNocPath1 != null && selectedLoanNocPath1 != "") {
                                                    saveData()
                                                }
                                            }
                                            uploadFileWithProgress(
                                                it, false, selectedLoanNocPath1!!.substring(
                                                    selectedLoanNocPath1!!.lastIndexOf(
                                                        "/"
                                                    ) + 1
                                                )
                                            )
                                        }

                                        LOAN_NOC2 -> {
                                            selectedLoanNocPath2 = selectedFilePath
                                            isSelectedLoanNocPath2 = true
                                            if (isSelectedLoanNocPath2) {
                                                binding?.ivLoanNoc2?.setImageDrawable(
                                                    ContextCompat.getDrawable(
                                                        requireContext(),
                                                        R.drawable.ic_upload_doc_image_new
                                                    )
                                                )
                                                uploadPendingDocReq_Local.loanNoc2 = ""
                                                if(selectedLoanNocPath2 != null && selectedLoanNocPath2 != "") {
                                                    saveData()
                                                }
                                            }
                                            uploadFileWithProgress(
                                                it, false, selectedLoanNocPath2!!.substring(
                                                    selectedLoanNocPath2!!.lastIndexOf(
                                                        "/"
                                                    ) + 1
                                                )
                                            )
                                        }

                                        BOUNCE_CLEAR1 -> {
                                            selectedBounceClearPath1 = selectedFilePath
                                            isSelectedBounceClearPath1 = true
                                            if (isSelectedBounceClearPath1) {
                                                binding?.ivBCProof1?.setImageDrawable(
                                                    ContextCompat.getDrawable(
                                                        requireContext(),
                                                        R.drawable.ic_upload_doc_image_new
                                                    )
                                                )
                                                uploadPendingDocReq_Local.bounceClearanceProof1 = ""
                                                if(selectedBounceClearPath1 != null && selectedBounceClearPath1 != "") {
                                                    saveData()
                                                }
                                            }
                                            uploadFileWithProgress(
                                                it, false, selectedBounceClearPath1!!.substring(
                                                    selectedBounceClearPath1!!.lastIndexOf(
                                                        "/"
                                                    ) + 1
                                                )
                                            )
                                        }

                                        BOUNCE_CLEAR2 -> {
                                            selectedBounceClearPath2 = selectedFilePath
                                            isSelectedBounceClearPath2 = true
                                            if (isSelectedBounceClearPath2) {
                                                binding?.ivBCProof2?.setImageDrawable(
                                                    ContextCompat.getDrawable(
                                                        requireContext(),
                                                        R.drawable.ic_upload_doc_image_new
                                                    )
                                                )
                                                uploadPendingDocReq_Local.bounceClearanceProof2 = ""
                                                if(selectedBounceClearPath2 != null && selectedBounceClearPath2 != "") {
                                                    saveData()
                                                }
                                            }
                                            uploadFileWithProgress(
                                                it, false, selectedBounceClearPath2!!.substring(
                                                    selectedBounceClearPath2!!.lastIndexOf(
                                                        "/"
                                                    ) + 1
                                                )
                                            )
                                        }

                                        BOUNCE_CLEAR3 -> {
                                            selectedBounceClearPath3 = selectedFilePath
                                            isSelectedBounceClearPath3 = true
                                            if (isSelectedBounceClearPath3) {
                                                binding?.ivBCProof3?.setImageDrawable(
                                                    ContextCompat.getDrawable(
                                                        requireContext(),
                                                        R.drawable.ic_upload_doc_image_new
                                                    )
                                                )
                                                uploadPendingDocReq_Local.bounceClearanceProof3 = ""
                                                if(selectedBounceClearPath3 != null && selectedBounceClearPath3 != "") {
                                                    saveData()
                                                }
                                            }
                                            uploadFileWithProgress(
                                                it, false, selectedBounceClearPath3!!.substring(
                                                    selectedBounceClearPath3!!.lastIndexOf(
                                                        "/"
                                                    ) + 1
                                                )
                                            )
                                        }

                                        BOUNCE_CLEAR4 -> {
                                            selectedBounceClearPath4 = selectedFilePath
                                            isSelectedBounceClearPath4 = true
                                            if (isSelectedBounceClearPath4) {
                                                binding?.ivBCProof4?.setImageDrawable(
                                                    ContextCompat.getDrawable(
                                                        requireContext(),
                                                        R.drawable.ic_upload_doc_image_new
                                                    )
                                                )
                                                uploadPendingDocReq_Local.bounceClearanceProof4 = ""
                                                if(selectedBounceClearPath4 != null && selectedBounceClearPath4 != "") {
                                                    saveData()
                                                }
                                            }
                                            uploadFileWithProgress(
                                                it, false, selectedBounceClearPath4!!.substring(
                                                    selectedBounceClearPath4!!.lastIndexOf(
                                                        "/"
                                                    ) + 1
                                                )
                                            )
                                        }

                                        BOUNCE_CLEAR5 -> {
                                            selectedBounceClearPath5 = selectedFilePath
                                            isSelectedBounceClearPath5 = true
                                            if (isSelectedBounceClearPath5) {
                                                binding?.ivBCProof5?.setImageDrawable(
                                                    ContextCompat.getDrawable(
                                                        requireContext(),
                                                        R.drawable.ic_upload_doc_image_new
                                                    )
                                                )
                                                uploadPendingDocReq_Local.bounceClearanceProof5 = ""
                                                if(selectedBounceClearPath5 != null && selectedBounceClearPath5 != "") {
                                                    saveData()
                                                }
                                            }
                                            uploadFileWithProgress(
                                                it, false, selectedBounceClearPath5!!.substring(
                                                    selectedBounceClearPath5!!.lastIndexOf(
                                                        "/"
                                                    ) + 1
                                                )
                                            )
                                        }

                                        BOUNCE_CLEAR6 -> {
                                            selectedBounceClearPath6 = selectedFilePath
                                            isSelectedBounceClearPath6 = true
                                            if (isSelectedBounceClearPath6) {
                                                binding?.ivBCProof6?.setImageDrawable(
                                                    ContextCompat.getDrawable(
                                                        requireContext(),
                                                        R.drawable.ic_upload_doc_image_new
                                                    )
                                                )
                                                uploadPendingDocReq_Local.bounceClearanceProof6 = ""
                                                if(selectedBounceClearPath6 != null && selectedBounceClearPath6 != "") {
                                                    saveData()
                                                }
                                            }
                                            uploadFileWithProgress(
                                                it, false, selectedBounceClearPath6!!.substring(
                                                    selectedBounceClearPath6!!.lastIndexOf(
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
                                selectedPresentAddress1Path = result
                                isSelectedPresentAddress1Path = true
                                if (isSelectedPresentAddress1Path) {
                                    binding?.ivFirst?.setImageDrawable(
                                        ContextCompat.getDrawable(
                                            requireContext(), R.drawable.ic_upload_doc_image_new
                                        )
                                    )
                                }
                                uploadFileWithProgress(
                                    selectedPresentAddress1Path.toString(),
                                    false,
                                    selectedPresentAddress1Path!!.substring(
                                        selectedPresentAddress1Path!!.lastIndexOf(
                                            "/"
                                        ) + 1
                                    )
                                )
                            }

                            PRESENT_ADDRESS_PROOF2_IMG -> {
                                selectedPresentAddress2Path = result
                                isSelectedPresentAddress2Path = true
                                if (isSelectedPresentAddress2Path) {
                                    binding?.ivSecond?.setImageDrawable(
                                        ContextCompat.getDrawable(
                                            requireContext(), R.drawable.ic_upload_doc_image_new
                                        )
                                    )
                                }
                                uploadFileWithProgress(
                                    selectedPresentAddress2Path.toString(),
                                    false,
                                    selectedPresentAddress2Path!!.substring(
                                        selectedPresentAddress2Path!!.lastIndexOf(
                                            "/"
                                        ) + 1
                                    )
                                )
                            }

                            SALARY_SLIP_IMG -> {
                                selectedSalarySlipPath = result
                                isSelectedSalarySlipPath = true
                                if (isSelectedSalarySlipPath) {
                                    binding?.ivSalSlip?.setImageDrawable(
                                        ContextCompat.getDrawable(
                                            requireContext(), R.drawable.ic_upload_doc_image_new
                                        )
                                    )
                                }
                                uploadFileWithProgress(
                                    selectedSalarySlipPath.toString(),
                                    false,
                                    selectedSalarySlipPath!!.substring(
                                        selectedSalarySlipPath!!.lastIndexOf(
                                            "/"
                                        ) + 1
                                    )
                                )
                            }

                            PROOF_OF_ID_IMG -> {
                                selectedProofOfIDPath = result
                                isSelectedProofOfIDPath = true
                                if (isSelectedProofOfIDPath) {
                                    binding?.ivProofOfId?.setImageDrawable(
                                        ContextCompat.getDrawable(
                                            requireContext(), R.drawable.ic_upload_doc_image_new
                                        )
                                    )
                                }
                                uploadFileWithProgress(
                                    selectedProofOfIDPath.toString(),
                                    false,
                                    selectedProofOfIDPath!!.substring(
                                        selectedProofOfIDPath!!.lastIndexOf(
                                            "/"
                                        ) + 1
                                    )
                                )
                            }

                            PROOF_OF_PRESENT_ADDRESS1 -> {
                                selectedPOPAddressPath1 = result
                                isSelectedPOPAddressPath1 = true
                                if (isSelectedPOPAddressPath1) {
                                    binding?.ivProofOfPAddress1?.setImageDrawable(
                                        ContextCompat.getDrawable(
                                            requireContext(), R.drawable.ic_upload_doc_image_new
                                        )
                                    )
                                }
                                uploadFileWithProgress(
                                    selectedPOPAddressPath1.toString(),
                                    false,
                                    selectedPOPAddressPath1!!.substring(
                                        selectedPOPAddressPath1!!.lastIndexOf(
                                            "/"
                                        ) + 1
                                    )
                                )
                            }

                            PROOF_OF_PRESENT_ADDRESS2 -> {
                                selectedPOPAddressPath2 = result
                                isSelectedPOPAddressPath2 = true
                                if (isSelectedPOPAddressPath2) {
                                    binding?.ivProofOfPAddress2?.setImageDrawable(
                                        ContextCompat.getDrawable(
                                            requireContext(), R.drawable.ic_upload_doc_image_new
                                        )
                                    )
                                }
                                uploadFileWithProgress(
                                    selectedPOPAddressPath2.toString(),
                                    false,
                                    selectedPOPAddressPath2!!.substring(
                                        selectedPOPAddressPath2!!.lastIndexOf(
                                            "/"
                                        ) + 1
                                    )
                                )
                            }

                            PROOF_OF_EMPLOYMENT1 -> {
                                selectedProofOfEmpPath1 = result
                                isSelectedProofOfEmpPath1 = true
                                if (isSelectedProofOfEmpPath1) {
                                    binding?.ivProofOfEmployment1?.setImageDrawable(
                                        ContextCompat.getDrawable(
                                            requireContext(), R.drawable.ic_upload_doc_image_new
                                        )
                                    )
                                }
                                uploadFileWithProgress(
                                    selectedProofOfEmpPath1.toString(),
                                    false,
                                    selectedProofOfEmpPath1!!.substring(
                                        selectedProofOfEmpPath1!!.lastIndexOf(
                                            "/"
                                        ) + 1
                                    )
                                )
                            }

                            PROOF_OF_EMPLOYMENT2 -> {
                                selectedProofOfEmpPath2 = result
                                isSelectedProofOfEmpPath2 = true
                                if (isSelectedProofOfEmpPath2) {
                                    binding?.ivProofOfEmployment2?.setImageDrawable(
                                        ContextCompat.getDrawable(
                                            requireContext(), R.drawable.ic_upload_doc_image_new
                                        )
                                    )
                                }
                                uploadFileWithProgress(
                                    selectedProofOfEmpPath2.toString(),
                                    false,
                                    selectedProofOfEmpPath2!!.substring(
                                        selectedProofOfEmpPath2!!.lastIndexOf(
                                            "/"
                                        ) + 1
                                    )
                                )
                            }

                            LOAN_NOC1 -> {
                                selectedLoanNocPath1 = result
                                isSelectedLoanNocPath1 = true
                                if (isSelectedLoanNocPath1) {
                                    binding?.ivLoanNoc1?.setImageDrawable(
                                        ContextCompat.getDrawable(
                                            requireContext(), R.drawable.ic_upload_doc_image_new
                                        )
                                    )
                                }
                                uploadFileWithProgress(
                                    selectedLoanNocPath1.toString(),
                                    false,
                                    selectedLoanNocPath1!!.substring(
                                        selectedLoanNocPath1!!.lastIndexOf(
                                            "/"
                                        ) + 1
                                    )
                                )
                            }

                            LOAN_NOC2 -> {
                                selectedLoanNocPath2 = result
                                isSelectedLoanNocPath2 = true
                                if (isSelectedLoanNocPath2) {
                                    binding?.ivLoanNoc2?.setImageDrawable(
                                        ContextCompat.getDrawable(
                                            requireContext(), R.drawable.ic_upload_doc_image_new
                                        )
                                    )
                                }
                                uploadFileWithProgress(
                                    selectedLoanNocPath2.toString(),
                                    false,
                                    selectedLoanNocPath2!!.substring(
                                        selectedLoanNocPath2!!.lastIndexOf(
                                            "/"
                                        ) + 1
                                    )
                                )
                            }

                            BOUNCE_CLEAR1 -> {
                                selectedBounceClearPath1 = result
                                isSelectedBounceClearPath1 = true
                                if (isSelectedBounceClearPath1) {
                                    binding?.ivBCProof1?.setImageDrawable(
                                        ContextCompat.getDrawable(
                                            requireContext(), R.drawable.ic_upload_doc_image_new
                                        )
                                    )
                                }
                                uploadFileWithProgress(
                                    selectedBounceClearPath1.toString(),
                                    false,
                                    selectedBounceClearPath1!!.substring(
                                        selectedBounceClearPath1!!.lastIndexOf(
                                            "/"
                                        ) + 1
                                    )
                                )
                            }

                            BOUNCE_CLEAR2 -> {
                                selectedBounceClearPath2 = result
                                isSelectedBounceClearPath2 = true
                                if (isSelectedBounceClearPath2) {
                                    binding?.ivBCProof2?.setImageDrawable(
                                        ContextCompat.getDrawable(
                                            requireContext(), R.drawable.ic_upload_doc_image_new
                                        )
                                    )
                                }
                                uploadFileWithProgress(
                                    selectedBounceClearPath2.toString(),
                                    false,
                                    selectedBounceClearPath2!!.substring(
                                        selectedBounceClearPath2!!.lastIndexOf(
                                            "/"
                                        ) + 1
                                    )
                                )
                            }

                            BOUNCE_CLEAR3 -> {
                                selectedBounceClearPath3 = result
                                isSelectedBounceClearPath3 = true
                                if (isSelectedBounceClearPath3) {
                                    binding?.ivBCProof3?.setImageDrawable(
                                        ContextCompat.getDrawable(
                                            requireContext(), R.drawable.ic_upload_doc_image_new
                                        )
                                    )
                                }
                                uploadFileWithProgress(
                                    selectedBounceClearPath3.toString(),
                                    false,
                                    selectedBounceClearPath3!!.substring(
                                        selectedBounceClearPath3!!.lastIndexOf(
                                            "/"
                                        ) + 1
                                    )
                                )
                            }

                            BOUNCE_CLEAR4 -> {
                                selectedBounceClearPath4 = result
                                isSelectedBounceClearPath4 = true
                                if (isSelectedBounceClearPath4) {
                                    binding?.ivBCProof4?.setImageDrawable(
                                        ContextCompat.getDrawable(
                                            requireContext(), R.drawable.ic_upload_doc_image_new
                                        )
                                    )
                                }
                                uploadFileWithProgress(
                                    selectedBounceClearPath4.toString(),
                                    false,
                                    selectedBounceClearPath4!!.substring(
                                        selectedBounceClearPath4!!.lastIndexOf(
                                            "/"
                                        ) + 1
                                    )
                                )
                            }

                            BOUNCE_CLEAR5 -> {
                                selectedBounceClearPath5 = result
                                isSelectedBounceClearPath5 = true
                                if (isSelectedBounceClearPath5) {
                                    binding?.ivBCProof5?.setImageDrawable(
                                        ContextCompat.getDrawable(
                                            requireContext(), R.drawable.ic_upload_doc_image_new
                                        )
                                    )
                                }
                                uploadFileWithProgress(
                                    selectedBounceClearPath5.toString(),
                                    false,
                                    selectedBounceClearPath5!!.substring(
                                        selectedBounceClearPath5!!.lastIndexOf(
                                            "/"
                                        ) + 1
                                    )
                                )
                            }

                            BOUNCE_CLEAR6 -> {
                                selectedBounceClearPath6 = result
                                isSelectedBounceClearPath6 = true
                                if (isSelectedBounceClearPath6) {
                                    binding?.ivBCProof6?.setImageDrawable(
                                        ContextCompat.getDrawable(
                                            requireContext(), R.drawable.ic_upload_doc_image_new
                                        )
                                    )
                                }
                                uploadFileWithProgress(
                                    selectedBounceClearPath6.toString(),
                                    false,
                                    selectedBounceClearPath6!!.substring(
                                        selectedBounceClearPath6!!.lastIndexOf(
                                            "/"
                                        ) + 1
                                    )
                                )
                            }


                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            else if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
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
                                    selectedPresentAddress1Path = selectedFilePath
                                    isSelectedPresentAddress1Path = true
                                    if (isSelectedPresentAddress1Path) {
                                        binding?.ivFirst?.setImageDrawable(
                                            ContextCompat.getDrawable(
                                                requireContext(),
                                                R.drawable.ic_upload_doc_image_new
                                            )
                                        )
                                    }
                                    uploadFileWithProgress(
                                        it, false, selectedPresentAddress1Path!!.substring(
                                            selectedPresentAddress1Path!!.lastIndexOf(
                                                "/"
                                            ) + 1
                                        )
                                    )
                                }

                                PRESENT_ADDRESS_PROOF2_IMG -> {
                                    selectedPresentAddress2Path = selectedFilePath
                                    isSelectedPresentAddress2Path = true
                                    if (isSelectedPresentAddress2Path) {
                                        binding?.ivSecond?.setImageDrawable(
                                            ContextCompat.getDrawable(
                                                requireContext(),
                                                R.drawable.ic_upload_doc_image_new
                                            )
                                        )
                                    }
                                    uploadFileWithProgress(
                                        it, false, selectedPresentAddress2Path!!.substring(
                                            selectedPresentAddress2Path!!.lastIndexOf(
                                                "/"
                                            ) + 1
                                        )
                                    )
                                }

                                SALARY_SLIP_IMG -> {
                                    selectedSalarySlipPath = selectedFilePath
                                    isSelectedSalarySlipPath = true
                                    if (isSelectedSalarySlipPath) {
                                        binding?.ivSalSlip?.setImageDrawable(
                                            ContextCompat.getDrawable(
                                                requireContext(),
                                                R.drawable.ic_upload_doc_image_new
                                            )
                                        )
                                    }
                                    uploadFileWithProgress(
                                        it, false, selectedSalarySlipPath!!.substring(
                                            selectedSalarySlipPath!!.lastIndexOf(
                                                "/"
                                            ) + 1
                                        )
                                    )
                                }

                                PROOF_OF_ID_IMG -> {
                                    selectedProofOfIDPath = selectedFilePath
                                    isSelectedProofOfIDPath = true
                                    if (isSelectedProofOfIDPath) {
                                        binding?.ivProofOfId?.setImageDrawable(
                                            ContextCompat.getDrawable(
                                                requireContext(), R.drawable.ic_upload_doc_image_new
                                            )
                                        )
                                    }
                                    uploadFileWithProgress(
                                        it, false, selectedProofOfIDPath!!.substring(
                                            selectedProofOfIDPath!!.lastIndexOf(
                                                "/"
                                            ) + 1
                                        )
                                    )
                                }

                                PROOF_OF_PRESENT_ADDRESS1 -> {
                                    selectedPOPAddressPath1 = selectedFilePath
                                    isSelectedPOPAddressPath1 = true
                                    if (isSelectedPOPAddressPath1) {
                                        binding?.ivProofOfPAddress1?.setImageDrawable(
                                            ContextCompat.getDrawable(
                                                requireContext(), R.drawable.ic_upload_doc_image_new
                                            )
                                        )
                                    }
                                    uploadFileWithProgress(
                                        it, false, selectedPOPAddressPath1!!.substring(
                                            selectedPOPAddressPath1!!.lastIndexOf(
                                                "/"
                                            ) + 1
                                        )
                                    )
                                }

                                PROOF_OF_PRESENT_ADDRESS2 -> {
                                    selectedPOPAddressPath2 = selectedFilePath
                                    isSelectedPOPAddressPath2 = true
                                    if (isSelectedPOPAddressPath2) {
                                        binding?.ivProofOfPAddress2?.setImageDrawable(
                                            ContextCompat.getDrawable(
                                                requireContext(), R.drawable.ic_upload_doc_image_new
                                            )
                                        )
                                    }
                                    uploadFileWithProgress(
                                        it, false, selectedPOPAddressPath2!!.substring(
                                            selectedPOPAddressPath2!!.lastIndexOf(
                                                "/"
                                            ) + 1
                                        )
                                    )
                                }

                                PROOF_OF_EMPLOYMENT1 -> {
                                    selectedProofOfEmpPath1 = selectedFilePath
                                    isSelectedProofOfEmpPath1 = true
                                    if (isSelectedProofOfEmpPath1) {
                                        binding?.ivProofOfEmployment1?.setImageDrawable(
                                            ContextCompat.getDrawable(
                                                requireContext(), R.drawable.ic_upload_doc_image_new
                                            )
                                        )
                                    }
                                    uploadFileWithProgress(
                                        it, false, selectedProofOfEmpPath1!!.substring(
                                            selectedProofOfEmpPath1!!.lastIndexOf(
                                                "/"
                                            ) + 1
                                        )
                                    )
                                }

                                PROOF_OF_EMPLOYMENT2 -> {
                                    selectedProofOfEmpPath2 = selectedFilePath
                                    isSelectedProofOfEmpPath2 = true
                                    if (isSelectedProofOfEmpPath2) {
                                        binding?.ivProofOfEmployment2?.setImageDrawable(
                                            ContextCompat.getDrawable(
                                                requireContext(), R.drawable.ic_upload_doc_image_new
                                            )
                                        )
                                    }
                                    uploadFileWithProgress(
                                        it, false, selectedProofOfEmpPath2!!.substring(
                                            selectedProofOfEmpPath2!!.lastIndexOf(
                                                "/"
                                            ) + 1
                                        )
                                    )
                                }

                                LOAN_NOC1 -> {
                                    selectedLoanNocPath1 = selectedFilePath
                                    isSelectedLoanNocPath1 = true
                                    if (isSelectedLoanNocPath1) {
                                        binding?.ivLoanNoc1?.setImageDrawable(
                                            ContextCompat.getDrawable(
                                                requireContext(), R.drawable.ic_upload_doc_image_new
                                            )
                                        )
                                    }
                                    uploadFileWithProgress(
                                        it, false, selectedLoanNocPath1!!.substring(
                                            selectedLoanNocPath1!!.lastIndexOf(
                                                "/"
                                            ) + 1
                                        )
                                    )
                                }

                                LOAN_NOC2 -> {
                                    selectedLoanNocPath2 = selectedFilePath
                                    isSelectedLoanNocPath2 = true
                                    if (isSelectedLoanNocPath2) {
                                        binding?.ivLoanNoc2?.setImageDrawable(
                                            ContextCompat.getDrawable(
                                                requireContext(), R.drawable.ic_upload_doc_image_new
                                            )
                                        )
                                    }
                                    uploadFileWithProgress(
                                        it, false, selectedLoanNocPath2!!.substring(
                                            selectedLoanNocPath2!!.lastIndexOf(
                                                "/"
                                            ) + 1
                                        )
                                    )
                                }

                                BOUNCE_CLEAR1 -> {
                                    selectedBounceClearPath1 = selectedFilePath
                                    isSelectedBounceClearPath1 = true
                                    if (isSelectedBounceClearPath1) {
                                        binding?.ivBCProof1?.setImageDrawable(
                                            ContextCompat.getDrawable(
                                                requireContext(), R.drawable.ic_upload_doc_image_new
                                            )
                                        )
                                    }
                                    uploadFileWithProgress(
                                        it, false, selectedBounceClearPath1!!.substring(
                                            selectedBounceClearPath1!!.lastIndexOf(
                                                "/"
                                            ) + 1
                                        )
                                    )
                                }

                                BOUNCE_CLEAR2 -> {
                                    selectedBounceClearPath2 = selectedFilePath
                                    isSelectedBounceClearPath2 = true
                                    if (isSelectedBounceClearPath2) {
                                        binding?.ivBCProof2?.setImageDrawable(
                                            ContextCompat.getDrawable(
                                                requireContext(), R.drawable.ic_upload_doc_image_new
                                            )
                                        )
                                    }
                                    uploadFileWithProgress(
                                        it, false, selectedBounceClearPath2!!.substring(
                                            selectedBounceClearPath2!!.lastIndexOf(
                                                "/"
                                            ) + 1
                                        )
                                    )
                                }

                                BOUNCE_CLEAR3 -> {
                                    selectedBounceClearPath3 = selectedFilePath
                                    isSelectedBounceClearPath3 = true
                                    if (isSelectedBounceClearPath3) {
                                        binding?.ivBCProof3?.setImageDrawable(
                                            ContextCompat.getDrawable(
                                                requireContext(), R.drawable.ic_upload_doc_image_new
                                            )
                                        )
                                    }
                                    uploadFileWithProgress(
                                        it, false, selectedBounceClearPath3!!.substring(
                                            selectedBounceClearPath3!!.lastIndexOf(
                                                "/"
                                            ) + 1
                                        )
                                    )
                                }

                                BOUNCE_CLEAR4 -> {
                                    selectedBounceClearPath4 = selectedFilePath
                                    isSelectedBounceClearPath4 = true
                                    if (isSelectedBounceClearPath4) {
                                        binding?.ivBCProof4?.setImageDrawable(
                                            ContextCompat.getDrawable(
                                                requireContext(), R.drawable.ic_upload_doc_image_new
                                            )
                                        )
                                    }
                                    uploadFileWithProgress(
                                        it, false, selectedBounceClearPath4!!.substring(
                                            selectedBounceClearPath4!!.lastIndexOf(
                                                "/"
                                            ) + 1
                                        )
                                    )
                                }

                                BOUNCE_CLEAR5 -> {
                                    selectedBounceClearPath5 = selectedFilePath
                                    isSelectedBounceClearPath5 = true
                                    if (isSelectedBounceClearPath5) {
                                        binding?.ivBCProof5?.setImageDrawable(
                                            ContextCompat.getDrawable(
                                                requireContext(), R.drawable.ic_upload_doc_image_new
                                            )
                                        )
                                    }
                                    uploadFileWithProgress(
                                        it, false, selectedBounceClearPath5!!.substring(
                                            selectedBounceClearPath5!!.lastIndexOf(
                                                "/"
                                            ) + 1
                                        )
                                    )
                                }

                                BOUNCE_CLEAR6 -> {
                                    selectedBounceClearPath6 = selectedFilePath
                                    isSelectedBounceClearPath6 = true
                                    if (isSelectedBounceClearPath6) {
                                        binding?.ivBCProof6?.setImageDrawable(
                                            ContextCompat.getDrawable(
                                                requireContext(), R.drawable.ic_upload_doc_image_new
                                            )
                                        )
                                    }
                                    uploadFileWithProgress(
                                        it, false, selectedBounceClearPath6!!.substring(
                                            selectedBounceClearPath6!!.lastIndexOf(
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
        val token = (activity as BaseActivity).userToken
        val apiKey = RequestBody.create("text/plain".toMediaTypeOrNull(), token)
        val userId: RequestBody = RequestBody.create(
            "text/plain".toMediaTypeOrNull(), (activity as DashboardActivity).userDetails.userId!!
        )

        val call: retrofit2.Call<FileUploadResponse?>? =
            fileBody.apiService.uploadFile(fileToUpload, apiKey, userId)
        call?.enqueue(object : Callback<FileUploadResponse?> {
            override fun onResponse(
                call: retrofit2.Call<FileUploadResponse?>, response: Response<FileUploadResponse?>
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
                    presentAddress1Url = fileUrls
                    if (presentAddress2Url.isNotEmpty()) {
                        fileUrls = "$fileUrls,$presentAddress2Url"
                    }
                    fileUploadAjaxRequest!!.urlsFor =
                        Constants.FileUploadAjaxCallKeys.PRESENT_ADDRESS_PROOF
                    uploadPendingDocReq.preAddress = fileUrls
                    uploadPendingDocReq_Local.preAddress1 = fileUrls
                    isSelectedPresentAddress1Path = true
                    saveData()

                }

                PRESENT_ADDRESS_PROOF2_IMG -> {
                    presentAddress2Url = fileUrls;
                    if (presentAddress1Url.isNotEmpty()) {
                        fileUrls = "$presentAddress1Url,$fileUrls"
                    }
                    fileUploadAjaxRequest!!.urlsFor = Constants.FileUploadAjaxCallKeys.PRESENT_ADDRESS_PROOF
                    isSelectedPresentAddress2Path = true
                    uploadPendingDocReq_Local.preAddress2 = fileUrls
                    saveData()
                }

                SALARY_SLIP_IMG -> {
                    salarySlipUrl = fileUrls
                    if (salarySlipUrl.isNotEmpty()) {
                        fileUrls = salarySlipUrl
                    }
                    fileUploadAjaxRequest!!.urlsFor = Constants.FileUploadAjaxCallKeys.SAL_SLIP
                    uploadPendingDocReq.latSalSlip = fileUrls
                    isSelectedSalarySlipPath = true
                    uploadPendingDocReq_Local.latSalSlip = fileUrls
                    saveData()

                }

                PROOF_OF_ID_IMG -> {
                    proofOfIdUrl = fileUrls
                    if (proofOfIdUrl.isNotEmpty()) {
                        fileUrls = proofOfIdUrl
                    }
                    fileUploadAjaxRequest!!.urlsFor = Constants.FileUploadAjaxCallKeys.PROOF_OF_ID
                    uploadPendingDocReq.proofOfId = fileUrls
                    uploadPendingDocReq_Local.proofOfId = fileUrls
                    isSelectedProofOfIDPath = true
                    saveData()
                }

                PROOF_OF_PRESENT_ADDRESS1 -> {
                    POPAddress1URL = fileUrls
                    if (POPAddress2URL.isNotEmpty()) {
                        fileUrls = "$fileUrls,$POPAddress2URL"
                    }
                    fileUploadAjaxRequest!!.urlsFor = Constants.FileUploadAjaxCallKeys.PROOF_OF_PER_ADDRESS
                    isSelectedPOPAddressPath1 = true
                    uploadPendingDocReq_Local.proofOfPerAddress1 = fileUrls
                    saveData()
                }

                PROOF_OF_PRESENT_ADDRESS2 -> {
                    POPAddress2URL = fileUrls;
                    if (POPAddress1URL.isNotEmpty()) {
                        fileUrls = "$POPAddress1URL,$fileUrls"
                    }
                    fileUploadAjaxRequest!!.urlsFor =
                        Constants.FileUploadAjaxCallKeys.PROOF_OF_PER_ADDRESS
                    isSelectedPOPAddressPath1 = true
                    uploadPendingDocReq_Local.proofOfPerAddress2 = fileUrls
                    saveData()
                }

                PROOF_OF_EMPLOYMENT1 -> {
                    ProofOfEmp1Url = fileUrls;
                    if (ProofOfEmp2Url.isNotEmpty()) {
                        fileUrls = "$fileUrls,$ProofOfEmp2Url"
                    }
                    fileUploadAjaxRequest!!.urlsFor = Constants.FileUploadAjaxCallKeys.PROOF_OF_EMP
                    isSelectedProofOfEmpPath1 = true
                    uploadPendingDocReq_Local.proofOfEmp1 = fileUrls
                    saveData()
                }

                PROOF_OF_EMPLOYMENT2 -> {
                    ProofOfEmp2Url = fileUrls;
                    if (ProofOfEmp1Url.isNotEmpty()) {
                        fileUrls = "$ProofOfEmp1Url,$fileUrls"
                    }
                    fileUploadAjaxRequest!!.urlsFor = Constants.FileUploadAjaxCallKeys.PROOF_OF_EMP
                    isSelectedProofOfEmpPath2 = true
                    uploadPendingDocReq_Local.proofOfEmp2 = fileUrls
                    saveData()
                }

                LOAN_NOC1 -> {
                    LoanNoc1Url = fileUrls;
                    if (LoanNoc2Url.isNotEmpty()) {
                        fileUrls = "$fileUrls,$LoanNoc2Url"
                    }
                    fileUploadAjaxRequest!!.urlsFor = Constants.FileUploadAjaxCallKeys.LOAN_NOC
                    isSelectedLoanNocPath1 = true
                    uploadPendingDocReq_Local.loanNoc1 = fileUrls
                    saveData()
                }

                LOAN_NOC2 -> {
                    LoanNoc2Url = fileUrls;
                    if (LoanNoc1Url.isNotEmpty()) {
                        fileUrls = "$LoanNoc1Url,$fileUrls"
                    }
                    fileUploadAjaxRequest!!.urlsFor = Constants.FileUploadAjaxCallKeys.LOAN_NOC
                    isSelectedLoanNocPath2 = true
                    uploadPendingDocReq_Local.loanNoc2 = fileUrls
                    saveData()
                }

                BOUNCE_CLEAR1 -> {
                    BC1Url = fileUrls;
                    if (BC2Url.isNotEmpty() && BC3Url.isNotEmpty() && BC4Url.isNotEmpty() && BC5Url.isNotEmpty() && BC6Url.isNotEmpty()) {
                        fileUrls = "$fileUrls,$BC2Url,$BC3Url,$BC4Url,$BC5Url,$BC6Url"
                    }
                    fileUploadAjaxRequest!!.urlsFor =
                        Constants.FileUploadAjaxCallKeys.BOUNCE_CLEARANCE_PROOF
                    isSelectedBounceClearPath1 = true
                    uploadPendingDocReq_Local.bounceClearanceProof1 = fileUrls
                    saveData()
                }

                BOUNCE_CLEAR2 -> {
                    BC2Url = fileUrls;
                    if (BC1Url.isNotEmpty() && BC3Url.isNotEmpty() && BC4Url.isNotEmpty() && BC5Url.isNotEmpty() && BC6Url.isNotEmpty()) {
                        fileUrls = "$BC1Url,$fileUrls,$BC3Url,$BC4Url,$BC5Url,$BC6Url"
                    }
                    fileUploadAjaxRequest!!.urlsFor =
                        Constants.FileUploadAjaxCallKeys.BOUNCE_CLEARANCE_PROOF
                    isSelectedBounceClearPath2 = true
                    uploadPendingDocReq_Local.bounceClearanceProof2 = fileUrls
                    saveData()
                }

                BOUNCE_CLEAR3 -> {
                    BC3Url = fileUrls;
                    if (BC1Url.isNotEmpty() && BC2Url.isNotEmpty() && BC4Url.isNotEmpty() && BC5Url.isNotEmpty() && BC6Url.isNotEmpty()) {
                        fileUrls = "$BC1Url,$BC2Url,$fileUrls,$BC4Url,$BC5Url,$BC6Url"
                    }
                    fileUploadAjaxRequest!!.urlsFor =
                        Constants.FileUploadAjaxCallKeys.BOUNCE_CLEARANCE_PROOF
                    isSelectedBounceClearPath3 = true
                    uploadPendingDocReq_Local.bounceClearanceProof3 = fileUrls
                    saveData()
                }

                BOUNCE_CLEAR4 -> {
                    BC4Url = fileUrls;
                    if (BC1Url.isNotEmpty() && BC2Url.isNotEmpty() && BC3Url.isNotEmpty() && BC5Url.isNotEmpty() && BC6Url.isNotEmpty()) {
                        fileUrls = "$BC1Url,$BC2Url,$BC3Url,$fileUrls,$BC5Url,$BC6Url"
                    }
                    fileUploadAjaxRequest!!.urlsFor =
                        Constants.FileUploadAjaxCallKeys.BOUNCE_CLEARANCE_PROOF
                    isSelectedBounceClearPath4 = true
                    uploadPendingDocReq_Local.bounceClearanceProof4 = fileUrls
                    saveData()
                }

                BOUNCE_CLEAR5 -> {
                    BC5Url = fileUrls;
                    if (BC1Url.isNotEmpty() && BC2Url.isNotEmpty() && BC3Url.isNotEmpty() && BC4Url.isNotEmpty() && BC6Url.isNotEmpty()) {
                        fileUrls = "$BC1Url,$BC2Url,$BC3Url,$BC4Url,$fileUrls,$BC6Url"
                    }
                    fileUploadAjaxRequest!!.urlsFor =
                        Constants.FileUploadAjaxCallKeys.BOUNCE_CLEARANCE_PROOF
                    isSelectedBounceClearPath5 = true
                    uploadPendingDocReq_Local.bounceClearanceProof5 = fileUrls
                    saveData()
                }

                BOUNCE_CLEAR6 -> {
                    BC6Url = fileUrls;
                    if (BC1Url.isNotEmpty() && BC2Url.isNotEmpty() && BC3Url.isNotEmpty() && BC4Url.isNotEmpty() && BC5Url.isNotEmpty()) {
                        fileUrls = "$BC1Url,$BC2Url,$BC3Url,$BC4Url,$BC5Url,$fileUrls"
                    }
                    fileUploadAjaxRequest!!.urlsFor =
                        Constants.FileUploadAjaxCallKeys.BOUNCE_CLEARANCE_PROOF
                    isSelectedBounceClearPath6 = true
                    uploadPendingDocReq_Local.bounceClearanceProof6 = fileUrls
                    saveData()
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
                    0, 0, R.drawable.ic_remove_image, 0
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

    override fun onResume() {
        super.onResume()
        getPendingDocuments()
    }

    fun saveData() {
        try{
        (activity as BaseActivity).sharedPreferences.putString(
            Constants.PENDING_DOCS_DATA,
            Gson().toJson(uploadPendingDocReq_Local)
        )
        }catch (e: Exception){
            e.printStackTrace()
        }
    }
}