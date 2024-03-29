package com.capitalnowapp.mobile.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable


class BbpsBillPayData : Serializable{

    @SerializedName("pb_priority")
    @Expose
    var pbPriority: Int? = null

    @SerializedName("pb_name")
    @Expose
    var pbName: String? = null

    @SerializedName("pb_key")
    @Expose
    var pbKey: String? = null

    @SerializedName("pb_url")
    @Expose
    var pbUrl: String? = null

}
