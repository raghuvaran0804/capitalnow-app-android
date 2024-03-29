package com.capitalnowapp.mobile.models.rewardsNew

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable


class RedeemedCouponsData : Serializable{

    @SerializedName("reward_points_available")
    @Expose
    var rewardPointsAvailable: String? = null

    @SerializedName("redeemed_coupons")
    @Expose
    var redeemedCoupons: List<RedeemedCoupon>? = null

}
