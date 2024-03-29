package com.capitalnowapp.mobile.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable


class CancelReason :Serializable{
    @SerializedName("cancel_rid")
    @Expose
    var cancelRid: String? = null

    @SerializedName("cancel_reason")
    @Expose
    var cancelReason: String? = null

    @SerializedName("cancel_ltype")
    @Expose
    var cancelLtype: String? = null

    @SerializedName("cancel_rorder")
    @Expose
    var cancelRorder: String? = null

    @SerializedName("cancel_rstatus")
    @Expose
    var cancelRstatus: String? = null
    override fun toString(): String {
        return cancelReason.toString()
    }
}
