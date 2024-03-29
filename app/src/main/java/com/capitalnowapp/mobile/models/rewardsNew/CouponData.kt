package com.capitalnowapp.mobile.models.rewardsNew

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable


class CouponData : Serializable{
    @SerializedName("coupon_categories")
    @Expose
    var couponCategories: List<CouponCategory>? = null

    @SerializedName("reward_points_available")
    @Expose
    var rewardPointsAvailable: String? = null

    @SerializedName("referral_code")
    @Expose
    var referralCode: String? = null

    @SerializedName("share_txt")
    @Expose
    var shareText: String? = null

    @SerializedName("get_points")
    @Expose
    var getPoints: String? = null

}
