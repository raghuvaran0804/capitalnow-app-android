package com.capitalnowapp.mobile.models.offerModel

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable


class CSGenericData :Serializable {
    @SerializedName("missingParameters")
    @Expose
    var missingParameters: List<String>? = null

}
