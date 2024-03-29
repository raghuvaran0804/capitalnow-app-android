package com.capitalnowapp.mobile.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable


class GetProcessPageContentData : Serializable{
    @SerializedName("title")
    @Expose
    var title: String? = null

    @SerializedName("subTitle")
    @Expose
    var subTitle: String? = null

}
