package com.capitalnowapp.mobile.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable


class DesignationJsonResponse  : Serializable{

    @SerializedName("designation")
    @Expose
    var designation: List<String>? = null

}
