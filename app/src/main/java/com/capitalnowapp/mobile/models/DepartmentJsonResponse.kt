package com.capitalnowapp.mobile.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable


class DepartmentJsonResponse : Serializable{

    @SerializedName("department")
    @Expose
    var department: List<String>? = null

}
