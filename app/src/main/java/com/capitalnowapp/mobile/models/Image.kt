package com.capitalnowapp.mobile.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable


class Image : Serializable{
    @SerializedName("twl_banner_link")
    @Expose
    var twlBannerLink: String? = null

    @SerializedName("is_clickable")
    @Expose
    var isClickable: String? = null

    @SerializedName("b_type")
    @Expose
    var bType: String? = null

    @SerializedName("b_btn_position")
    @Expose
    var bBtnPosition: String? = null

}
