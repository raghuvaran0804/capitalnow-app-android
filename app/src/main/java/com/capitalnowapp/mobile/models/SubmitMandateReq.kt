package com.capitalnowapp.mobile.models

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName
import java.io.Serializable


class SubmitMandateReq : Serializable{

    @SerializedName("api_key")
    @Expose
    var apiKey: String? = null

    @SerializedName("digio_status")
    @Expose
    var digioStatus: String? = null

    @SerializedName("digio_doc_id")
    @Expose
    var digioDocId: String? = null

    @SerializedName("digio_message")
    @Expose
    var digioMessage: String? = null

}