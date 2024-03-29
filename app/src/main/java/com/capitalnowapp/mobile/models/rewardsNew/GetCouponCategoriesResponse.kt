package com.capitalnowapp.mobile.models.rewardsNew

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable


class GetCouponCategoriesResponse : Serializable {

    @SerializedName("status")
    @Expose
    var status: Boolean? = null

    @SerializedName("code")
    @Expose
    var code: Int? = null

    @SerializedName("message")
    @Expose
    var message: String? = null

    @SerializedName("status_code")
    @Expose
    var statusCode: Int? = null

    @SerializedName("data")
    @Expose
    var couponData: CouponData? = null
}