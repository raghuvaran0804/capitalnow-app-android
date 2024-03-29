package com.capitalnowapp.mobile.models.coupons

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable


class CouponsDetails : Serializable {
    @SerializedName("CouponID")
    @Expose
    val couponID: String? = null

    @SerializedName("WebsiteName")
    @Expose
    val websiteName: String? = null

    @SerializedName("CouponCode")
    @Expose
    val couponCode: String? = null

    @SerializedName("CouponTitle")
    @Expose
    val couponTitle: String? = null

    @SerializedName("CouponDescription")
    @Expose
    val couponDescription: String? = null

    @SerializedName("WebsiteLogo")
    @Expose
    val websiteLogo: String? = null

    @SerializedName("WebsiteImage")
    @Expose
    val websiteImage: String? = null

    @SerializedName("Link")
    @Expose
    val link: String? = null

    @SerializedName("ExpiryDate")
    @Expose
    val expiryDate: String? = null

    @SerializedName("Points")
    @Expose
    val points: String? = null

    @SerializedName("Redeemed")
    @Expose
    val redeemed: Boolean = false

}
