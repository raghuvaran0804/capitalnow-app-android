package com.capitalnowapp.mobile.models.rewardsNew

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable


class RedeemCouponData : Serializable {
    @SerializedName("reward_points_available")
    @Expose
    var rewardPointsAvailable: String? = null

    @SerializedName("redeem_log_id")
    @Expose
    var redeemLogId: Int? = null

}
