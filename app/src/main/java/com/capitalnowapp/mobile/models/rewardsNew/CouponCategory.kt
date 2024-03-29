package com.capitalnowapp.mobile.models.rewardsNew

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable


class CouponCategory :  Serializable{

    @SerializedName("cup_category")
    @Expose
    var cupCategory: String? = null

    override fun toString(): String {
        return cupCategory.toString()
    }
}
