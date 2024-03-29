package com.capitalnowapp.mobile.models.rewardsNew

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable


class RedeemCouponDetailsData : Serializable{

    @SerializedName("reward_points_available")
    @Expose
    var rewardPointsAvailable: String? = null

    @SerializedName("terms_and_condition_url")
    @Expose
    var termsAndConditionUrl: String? = null

    @SerializedName("cup_image_url")
    @Expose
    var cupImageUrl: String? = null

    @SerializedName("card_number")
    @Expose
    var cardNumber: String? = null

    @SerializedName("expiration_date")
    @Expose
    var expirationDate: String? = null

    @SerializedName("card_pin")
    @Expose
    var cardPin: String? = null

    @SerializedName("redeem_log_id")
    @Expose
    var redeemLogId: String? = null

    @SerializedName("coupon_title")
    @Expose
    var couponTitle: String? = null

    @SerializedName("user_email")
    @Expose
    var userEmail: String? = null

    @SerializedName("is_email_sent")
    @Expose
    var isEmailSent: Boolean? = null

    @SerializedName("activation_url")
    @Expose
    var activationUrl: String? = null

    @SerializedName("activation_code")
    @Expose
    var activationCode: String? = null

    @SerializedName("redeemed_points")
    @Expose
    var redeemedPoints: String? = null

    @SerializedName("cup_value")
    @Expose
    var cupValue: String? = null

    @SerializedName("reedemed_at")
    @Expose
    var reedemedAt: List<String>? = null

    @SerializedName("cup_description")
    @Expose
    var cupDescription: String? = null
}
