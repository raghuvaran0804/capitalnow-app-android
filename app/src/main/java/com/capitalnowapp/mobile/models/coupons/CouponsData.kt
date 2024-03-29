package com.capitalnowapp.mobile.models.coupons

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable


class CouponsData : Serializable {
    @SerializedName("category")
    @Expose
    val category: String? = null

    @SerializedName("bg_colors")
    @Expose
    val bgColors: String? = null

    @SerializedName("is_redeemed")
    @Expose
    val isRedeemed: Boolean? = false

    @SerializedName("list")
    @Expose
    val list: List<CouponsDetails>? = null

}
