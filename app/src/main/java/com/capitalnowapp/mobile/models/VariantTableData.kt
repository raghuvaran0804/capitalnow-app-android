package com.capitalnowapp.mobile.models

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName
import java.io.Serializable


class VarientTableData : Serializable{

    @SerializedName("varient_name")
    @Expose
    var varientName: String? = null

    override fun toString(): String {
        return varientName.toString()
    }
}