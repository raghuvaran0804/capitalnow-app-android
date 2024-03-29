package com.capitalnowapp.mobile.models.managerdetails

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable


class ManagerResponse : Serializable {

    @SerializedName("razor_pay_api_key")
    @Expose
    val razorPayApiKey: String? = null

    @SerializedName("status")
    @Expose
    val status: Boolean? = null

    @SerializedName("ph_image")
    @Expose
    val phImage: String = ""

    @SerializedName("status_code")
    @Expose
    val statusCode: Int? = null

    @SerializedName("message")
    @Expose
    val message: String = ""

    @SerializedName("relationship_manager")
    @Expose
    val relationshipManager: RelationshipManager? = null

    @SerializedName("recovery_officer")
    @Expose
    val recoveryOfficer: RecoveryOfficer? = null
}