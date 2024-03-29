package com.capitalnowapp.mobile.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable


class CustomPopUp : Serializable {
    @SerializedName("title")
    @Expose
    var title: String? = null

    @SerializedName("content")
    @Expose
    var content: String? = null

    @SerializedName("btn")
    @Expose
    var btn: String? = null

}