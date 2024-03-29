package com.capitalnowapp.mobile.models

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName




class AreaTableData {
    @SerializedName("area")
    @Expose
    var area: String? = null

    override fun toString(): String {
        return area.toString()
    }

}
