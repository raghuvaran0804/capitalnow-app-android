package com.capitalnowapp.mobile.models.loan

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName




class OfferDetails {
    @SerializedName("save_text")
    @Expose
    var saveText: String? = null

    @SerializedName("on_text")
    @Expose
    var onText: String? = null
}
