package com.capitalnowapp.mobile.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable


class SubmitContactUSResponseData : Serializable {
    @SerializedName("web_cnt_id")
    @Expose
    private val webCntId: Int? = null

    @SerializedName("web_cnt_existing_customer")
    @Expose
    private val webCntExistingCustomer: Int? = null

    @SerializedName("web_cnt_name")
    @Expose
    private val webCntName: String? = null

    @SerializedName("web_cnt_mobile_number")
    @Expose
    private val webCntMobileNumber: String? = null

    @SerializedName("web_cnt_query")
    @Expose
    private val webCntQuery: String? = null

    @SerializedName("web_cnt_sub_query")
    @Expose
    private val webCntSubQuery: String? = null

    @SerializedName("web_cnt_email")
    @Expose
    private val webCntEmail: String? = null

    @SerializedName("web_cnt_message")
    @Expose
    private val webCntMessage: String? = null

    @SerializedName("web_cnt_updated_at")
    @Expose
    private val webCntUpdatedAt: String? = null

    @SerializedName("web_cnt_created_at")
    @Expose
    private val webCntCreatedAt: String? = null
}
