package com.capitalnowapp.mobile.models.rewardsNew

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable


class CouponInfoData : Serializable{
    @SerializedName("cup_ref_id")
    @Expose
    var cupRefId: String? = null

    @SerializedName("cup_name")
    @Expose
    var cupName: String? = null

    @SerializedName("cup_description")
    @Expose
    var cupDescription: String? = null

    @SerializedName("cup_terms_conditions")
    @Expose
    var cupTermsConditions: String? = null

    @SerializedName("cup_category")
    @Expose
    var cupCategory: String? = null

    @SerializedName("cup_value")
    @Expose
    var cupValue: Int? = null

    @SerializedName("cup_points")
    @Expose
    var cupPoints: Int? = null

    @SerializedName("cup_image_url")
    @Expose
    var cupImageUrl: String? = null

    @SerializedName("cup_website_url")
    @Expose
    var cupWebsiteUrl: String? = null

    @SerializedName("cup_expiry_date")
    @Expose
    var cupExpiryDate: String? = null

    @SerializedName("cup_user_available_points")
    @Expose
    var cupUserAvailablePoints: String? = null

    @SerializedName("reedemed_at")
    @Expose
    var reedemedAt: List<String>? = null

}
