package com.capitalnowapp.mobile.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable


class MemberUpgradeData : Serializable{
    @SerializedName("link")
    @Expose
    var link: String? = null

    @SerializedName("content")
    @Expose
    var content: String? = null

}
