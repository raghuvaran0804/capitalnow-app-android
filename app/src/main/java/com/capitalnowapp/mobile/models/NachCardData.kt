package com.capitalnowapp.mobile.models

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class NachCardData :Serializable {

    @SerializedName("title")
    @Expose
    var title: String? = null

    @SerializedName("description")
    @Expose
    var description: String? = null

    @SerializedName("button")
    @Expose
    var button: String? = null

    @SerializedName("nach_data")
    @Expose
    var nachData: NachData? = null
}