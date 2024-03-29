package com.capitalnowapp.mobile.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class CalandarEvents :Serializable{
    @SerializedName("api_key")
    @Expose
    var apiKey: String? = null
    @SerializedName("event_ID")
    @Expose
    var eventId: String? = null
    @SerializedName("title")
    @Expose
    var title: String? = null
    @SerializedName("mOrganizer")
    @Expose
    var mOrganizer: String? = null
    @SerializedName("dtStart")
    @Expose
    var dtStart: String? = null
    @SerializedName("dtEnd")
    @Expose
    var dtEnd: String? = null

}