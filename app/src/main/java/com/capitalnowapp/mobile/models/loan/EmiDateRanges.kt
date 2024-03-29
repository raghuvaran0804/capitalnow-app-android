package com.capitalnowapp.mobile.models.loan

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable


class EmiDateRanges :Serializable{

    @SerializedName("min")
    @Expose
    var min: Int? = null

    @SerializedName("max")
    @Expose
    var max: Int? = null

    @SerializedName("interval")
    @Expose
    var interval: Int? = null

}
