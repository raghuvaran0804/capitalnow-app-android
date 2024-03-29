package com.capitalnowapp.mobile.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class IdTextData : Serializable{

    @SerializedName("id")
    @Expose
    var id: Int? = null

    @SerializedName("text")
    @Expose
    var text: String? = null

    @SerializedName("api_key")
    @Expose
    var apiKey: String? = null

    var isChecked: Boolean = false
}