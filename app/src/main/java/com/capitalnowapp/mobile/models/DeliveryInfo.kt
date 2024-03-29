package com.capitalnowapp.mobile.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable


class DeliveryInfo :Serializable {

    @SerializedName("del_image")
    @Expose
    var delImage: String? = null

    @SerializedName("del_title")
    @Expose
    var delTitle: String? = null

    @SerializedName("del_discription")
    @Expose
    var delDiscription: String? = null

}
