package com.capitalnowapp.mobile.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable


class SaveReferencesReq : Serializable{
    @SerializedName("api_key")
    @Expose
    var apiKey: String? = null

    @SerializedName("user_id")
    @Expose
    var userId: String? = null

    @SerializedName("contact_no1")
    @Expose
    var contactNo1: String? = null

    @SerializedName("contact_name1")
    @Expose
    var contactName1: String? = null

    @SerializedName("contact_relation1")
    @Expose
    var contactRelation1: String? = null

    @SerializedName("contact_no2")
    @Expose
    var contactNo2: String? = null

    @SerializedName("contact_name2")
    @Expose
    var contactName2: String? = null

    @SerializedName("contact_relation2")
    @Expose
    var contactRelation2: String? = null

    @SerializedName("contact_no3")
    @Expose
    var contactNo3: String? = null

    @SerializedName("contact_name3")
    @Expose
    var contactName3: String? = null

    @SerializedName("contact_relation3")
    @Expose
    var contactRelation3: String? = null

    @SerializedName("contact_no4")
    @Expose
    var contactNo4: String? = null

    @SerializedName("contact_name4")
    @Expose
    var contactName4: String? = null

    @SerializedName("contact_relation4")
    @Expose
    var contactRelation4: String? = null

    @SerializedName("contact_no5")
    @Expose
    var contactNo5: String? = null

    @SerializedName("contact_name5")
    @Expose
    var contactName5: String? = null

    @SerializedName("contact_relation5")
    @Expose
    var contactRelation5: String? = null

    @SerializedName("device_unique_id")
    @Expose
    var deviceUniqueId: String? = null

}
