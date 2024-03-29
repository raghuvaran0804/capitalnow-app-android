package com.capitalnowapp.mobile.models

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName




class ScrollChangeColor {
    @SerializedName("trigger")
    @Expose
    var trigger: String? = null

    @SerializedName("color")
    @Expose
    var color: String? = null

    @SerializedName("first_index")
    @Expose
    var firstIndex: Int? = null

    @SerializedName("last_index")
    @Expose
    var lastIndex: Int? = null

}
