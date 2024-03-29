package com.capitalnowapp.mobile.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable


class LoanRangeData : Serializable{

    @SerializedName("min")
    @Expose
    var min: Int? = null

    @SerializedName("max")
    @Expose
    var max: Int? = null

    @SerializedName("increament")
    @Expose
    var increament: Int? = null

}
