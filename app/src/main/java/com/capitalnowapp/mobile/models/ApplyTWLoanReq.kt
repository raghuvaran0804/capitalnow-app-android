package com.capitalnowapp.mobile.models

import com.capitalnowapp.mobile.models.loan.InstalmentData
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable


class ApplyTWLoanReq : Serializable {
    @SerializedName("api_key")
    @Expose
    var apiKey: String? = null

    @SerializedName("twl_amount")
    @Expose
    var twlAmount: Int? = null

    @SerializedName("twl_total")
    @Expose
    var twlTotal: Int? = null

    @SerializedName("twl_accept_pre_agreement")
    @Expose
    var twlAcceptPreAgreement: Int? = null

    @SerializedName("twl_emi_count")
    @Expose
    var twlEmiCount: Int? = null

    @SerializedName("twl_dealer_id")
    @Expose
    var twlDealerId: String? = null

    @SerializedName("twl_bike_id")
    @Expose
    var twlBikeId: Int? = null

    @SerializedName("twl_bike_rate")
    @Expose
    var twlBikeRate: Int? = null

    @SerializedName("twl_instalments")
    @Expose
    var twlInstalments: List<InstalmentData>? = null

    @SerializedName("current_location")
    @Expose
    var currentLocation: String? = null

    @SerializedName("device_unique_id")
    @Expose
    var deviceUniqueId: String? = null

    @SerializedName("twl_dealer_name")
    @Expose
    var twlDealerName: String? = null

    @SerializedName("twl_city_name")
    @Expose
    var twlCityName: String? = null

    @SerializedName("twl_area_name")
    @Expose
    var twlAreaName: String? = null

    @SerializedName("twl_brand")
    @Expose
    var twlBrand: String? = null

    @SerializedName("twl_req_start_date")
    @Expose
    var startDate: String? = null

    @SerializedName("twl_req_start_month")
    @Expose
    var startMonth: String? = null
}