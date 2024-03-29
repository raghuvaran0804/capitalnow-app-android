package com.capitalnowapp.mobile.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable


class RedirectionPendingDocument : Serializable{

    @SerializedName("title")
    @Expose
    var title: String? = null

    @SerializedName("key")
    @Expose
    var key: String? = null

    @SerializedName("doc_status")
    @Expose
    var docStatus: Int? = null

    @SerializedName("status_redirect")
    @Expose
    var statusRedirect: Int? = null

}
