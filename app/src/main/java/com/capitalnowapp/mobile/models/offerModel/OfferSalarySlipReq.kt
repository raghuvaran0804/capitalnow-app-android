package com.capitalnowapp.mobile.models.offerModel

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class OfferSalarySlipReq: Serializable {

    @SerializedName("upload_salary_slip")
    @Expose
    var uploadSalarySlip: String? = null

    @SerializedName("page_no")
    @Expose
    var pageNo: Int? = null
}