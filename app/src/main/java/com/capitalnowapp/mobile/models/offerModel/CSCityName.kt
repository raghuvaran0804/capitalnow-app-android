package com.capitalnowapp.mobile.models.offerModel

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable


class CSCityName : Serializable {
    @SerializedName("name")
    @Expose
    var name: String? = null
}
