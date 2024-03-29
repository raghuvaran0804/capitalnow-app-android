package com.capitalnowapp.mobile.models.loan

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class InsuranceData : Serializable{

    @SerializedName("policy_no")
    @Expose
    var policyNo: String? = null

    @SerializedName("policy_link")
    @Expose
    var policyLink: String? = null

}
