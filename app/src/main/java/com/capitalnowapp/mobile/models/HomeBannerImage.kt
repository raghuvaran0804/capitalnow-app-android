package com.capitalnowapp.mobile.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable


class HomeBannerImage : Serializable{

    @SerializedName("b_btn_position")
    @Expose
    var bBtnPosition: String? = null

    @SerializedName("b_type")
    @Expose
    var bType: String? = null

    @SerializedName("banner_link")
    @Expose
    var bannerLink: String? = null

    @SerializedName("redirection_url")
    @Expose
    var redirectionUrl: String? = null

    @SerializedName("share_msg")
    @Expose
    var shareMsg: String? = null

    @SerializedName("is_clickable")
    @Expose
    var isClickable: Int? = null

}
