package com.capitalnowapp.mobile.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable


class UploadPendingDocReq : Serializable {

    @SerializedName("api_key")
    @Expose
    var apiKey: String? = null

    @SerializedName("pre_address")
    @Expose
    var preAddress: String? = null

    @SerializedName("lat_sal_slip")
    @Expose
    var latSalSlip: String? = null

    @SerializedName("proof_of_id")
    @Expose
    var proofOfId: String? = null

    @SerializedName("proof_of_per_address")
    @Expose
    var proofOfPerAddress: String? = null

    @SerializedName("proof_of_emp")
    @Expose
    var proofOfEmp: String? = null

    @SerializedName("bounce_clearance_proof")
    @Expose
    var bounceClearanceProof: String? = null

    @SerializedName("loan_noc")
    @Expose
    var loanNoc: String? = null

    @SerializedName("lat_sal_pass")
    @Expose
    var latSalPass: String? = null

    @SerializedName("proof_address_pass")
    @Expose
    var proofAddressPass: String? = null

    @SerializedName("proof_emp_pass")
    @Expose
    var proofEmpPass: String? = null

    @SerializedName("bnk_statement")
    @Expose
    var bnkStatement: String? = null

    @SerializedName("video_kyc")
    @Expose
    var videoKyc: String? = null

    @SerializedName("cus_sign")
    @Expose
    var cusSign: String? = null

    @SerializedName("contact_reference")
    @Expose
    var contactReference: String? = null

}