package com.capitalnowapp.mobile.models

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName
import java.io.Serializable


class BrandTableData : Serializable{

    @SerializedName("twlv_brand")
    @Expose
    var twlvBrand: String? = null
}