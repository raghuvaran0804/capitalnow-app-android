package com.capitalnowapp.mobile.models.coupons

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable


class RedeemCouponResponse : Serializable {

    @SerializedName("razor_pay_api_key")
    @Expose
    val razorPayApiKey: String? = null

    @SerializedName("message")
    @Expose
    val message: String? = null

    @SerializedName("status")
    @Expose
    val status: Boolean? = null

    @SerializedName("rewards_points_remaining")
    @Expose
    val rewardsPointsRemaining: Int? = null

    @SerializedName("coupon_code")
    @Expose
    val couponCode: String? = null

    @SerializedName("Username")
    @Expose
    val username: String? = null

}
