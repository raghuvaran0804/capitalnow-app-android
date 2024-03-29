package com.capitalnowapp.mobile.models.rewardsNew

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable


class RedeemedCoupon : Serializable{

    @SerializedName("redeem_log_id")
    @Expose
    var redeemLogId: Int? = null

    @SerializedName("cuprl_expiry_date")
    @Expose
    var cuprlExpiryDate: String? = null

    @SerializedName("cup_value")
    @Expose
    var cupValue: String? = null

    @SerializedName("redeemed_points")
    @Expose
    var redeemedPoints: Int? = null

    @SerializedName("image_url")
    @Expose
    var imageUrl: String? = null

}
