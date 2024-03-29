package com.capitalnowapp.mobile.models.rewardsNew

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable


class GetEmailCouponDetailsReq : Serializable {

    @SerializedName("api_key")
    @Expose
    var apiKey: String? = null

    @SerializedName("redeem_log_id")
    @Expose
    var redeemLogId: String? = null

    @SerializedName("mail_id")
    @Expose
    var mailId: String? = null

}
