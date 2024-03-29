package com.capitalnowapp.mobile.models

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName
import java.io.Serializable


class PendingDocData : Serializable{
    @SerializedName("is_doc_required")
    @Expose
    var isDocRequired: Boolean? = null

    @SerializedName("required_documents")
    @Expose
    var requiredDocuments: List<RequiredPendingDocument>? = null

    @SerializedName("redirection_documents")
    @Expose
    var redirectionDocuments: List<RedirectionPendingDocument>? = null

    @SerializedName("title")
    @Expose
    var title: String? = null

    @SerializedName("description")
    @Expose
    var description: String? = null
}
