package com.capitalnowapp.mobile.models.profile

import com.capitalnowapp.mobile.models.loan.Value
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class ProfileBasic : Serializable {

    @SerializedName("title")
    @Expose
    val title: String? = null

    @SerializedName("image")
    @Expose
    val image: String? = null

    @SerializedName("expand")
    @Expose
    val expand: Boolean? = null

    @SerializedName("details")
    @Expose
    val details: List<Value>? = null
}