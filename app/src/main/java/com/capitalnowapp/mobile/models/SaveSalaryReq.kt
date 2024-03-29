package com.capitalnowapp.mobile.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable


class SaveSalaryReq :Serializable {

    @SerializedName("api_key")
    @Expose
    var apiKey: String? = null

    @SerializedName("monthly_sal")
    @Expose
    var monthlySal: String? = null

}
