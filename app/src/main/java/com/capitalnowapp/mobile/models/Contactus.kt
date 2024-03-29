package com.capitalnowapp.mobile.models

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName

class Contactus {
    @SerializedName("query")
    @Expose
    var query: List<ContactUsQuery>? = null
}