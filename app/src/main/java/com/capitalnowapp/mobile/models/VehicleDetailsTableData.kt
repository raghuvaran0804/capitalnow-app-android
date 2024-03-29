package com.capitalnowapp.mobile.models

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName
import java.io.Serializable


class VehicleDetailsTableData : Serializable{
    @SerializedName("vehicle_id")
    @Expose
    var vehicleId: String? = null

    @SerializedName("name")
    @Expose
    var name: String? = null

    @SerializedName("twlv_model")
    @Expose
    var twlvModel: String? = null

    @SerializedName("twlv_varient")
    @Expose
    var twlvVarient: String? = null

    @SerializedName("twlv_image")
    @Expose
    var twlvImage: String? = null

    @SerializedName("twlv_onroad_price")
    @Expose
    var twlvOnroadPrice: String? = null

    @SerializedName("twlv_exshowroom_price")
    @Expose
    var twlvExshowroomPrice: String? = null

    @SerializedName("twlv_avail_count")
    @Expose
    var twlvAvailCount: String? = null

    @SerializedName("twlv_status")
    @Expose
    var twlvStatus: String? = null

    @SerializedName("color")
    @Expose
    var color: String? = null

    @SerializedName("address")
    @Expose
    var address: String? = null

    @SerializedName("area")
    @Expose
    var area: String? = null

    @SerializedName("city")
    @Expose
    var city: String? = null

    @SerializedName("state")
    @Expose
    var state: String? = null

    @SerializedName("pincode")
    @Expose
    var pincode: String? = null

    @SerializedName("dealer_name")
    @Expose
    var dealerName: String? = null

    @SerializedName("twld_branch_code")
    @Expose
    var twldBranchCode: String? = null

    @SerializedName("twl_owner_name")
    @Expose
    var twlOwnerName: String? = null

    @SerializedName("twl_owner_number")
    @Expose
    var twlOwnerNumber: String? = null

    @SerializedName("DownPayment")
    @Expose
    var downPayment: Int? = null

    @SerializedName("Eligibility")
    @Expose
    var eligibility: String? = null

    @SerializedName("VehicleType")
    @Expose
    var vehicleType: String? = null
}