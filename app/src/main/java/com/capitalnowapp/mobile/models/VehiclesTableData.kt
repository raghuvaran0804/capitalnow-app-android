package com.capitalnowapp.mobile.models

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName
import java.io.Serializable


class VehiclesTableData :Serializable {
    @SerializedName("vehicle_id")
    @Expose
    var vehicleId: String? = null

    @SerializedName("name")
    @Expose
    var name: String? = null

    @SerializedName("twlv_brand")
    @Expose
    var twlvBrand: String? = null

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

    override fun toString(): String {
        return name.toString()
    }
}