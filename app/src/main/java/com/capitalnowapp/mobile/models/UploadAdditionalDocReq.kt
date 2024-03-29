package com.capitalnowapp.mobile.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable


class UploadAdditionalDocReq: Serializable {

    @SerializedName("api_key")
    @Expose
    var apiKey: String? = null

    @SerializedName("pre_address")
    @Expose
    var preAddress: String? = null

    @SerializedName("lat_sal_slip")
    @Expose
    var latSalSlip: String? = null

    @SerializedName("lat_sal_pass")
    @Expose
    var latSalPass: String? = null
}