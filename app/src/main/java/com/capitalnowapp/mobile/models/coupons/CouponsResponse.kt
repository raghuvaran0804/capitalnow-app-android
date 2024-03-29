package com.capitalnowapp.mobile.models.coupons

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable


class CouponsResponse : Serializable {

    @SerializedName("razor_pay_api_key")
    @Expose
    private val razorPayApiKey: String? = null

    @SerializedName("reward_points")
    @Expose
    var rewardPoints: String? = null

    @SerializedName("username")
    @Expose
    val username: String? = null

    @SerializedName("status")
    @Expose
    var status: Boolean = false

    @SerializedName("coupons_list")
    @Expose
    val couponsList: List<CouponsData> = ArrayList()
}