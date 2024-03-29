package com.capitalnowapp.mobile.models.managerdetails

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable


class RecoveryOfficer : Serializable {

    @SerializedName("title")
    @Expose
    val title: String? = null

    @SerializedName("name")
    @Expose
    val name: String? = null

    @SerializedName("mobno")
    @Expose
    val mobno: String? = null

    @SerializedName("hobbies")
    @Expose
    val hobbies: String? = null

    @SerializedName("lang")
    @Expose
    val lang: String? = null

    @SerializedName("city")
    @Expose
    val city: String? = null

    @SerializedName("stakeholder_id")
    @Expose
    val stakeholderId: String? = null

    @SerializedName("customer_relationship_avatar")
    @Expose
    val customerRelationshipAvatar: String? = null
}
