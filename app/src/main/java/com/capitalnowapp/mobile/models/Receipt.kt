package com.capitalnowapp.mobile.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable


class Receipt : Serializable{
    @SerializedName("id")
    @Expose
    private val id: String? = null

    @SerializedName("date")
    @Expose
    private val date: String? = null

}
