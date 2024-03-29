package com.capitalnowapp.mobile.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable


class CouponsListData : Serializable{
    @SerializedName("coupons_list")
    @Expose
    var couponsList: List<CouponsList>? = null

    @SerializedName("total_coupons")
    @Expose
    var totalCoupons: Int? = null

}
