package com.capitalnowapp.mobile.models.rewardsNew

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable


class EmailData : Serializable {

    @SerializedName("is_email_sent")
    @Expose
    var isEmailSent: Boolean? = null

}
