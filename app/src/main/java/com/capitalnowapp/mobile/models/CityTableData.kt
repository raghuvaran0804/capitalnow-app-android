package com.capitalnowapp.mobile.models

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName
import java.io.Serializable


class CityTableData : Serializable {

    @SerializedName("city_name")
    @Expose
    var cityName: String? = null

    override fun toString(): String {
        return cityName.toString()
    }

}
