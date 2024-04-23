package com.capitalnowapp.mobile.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable


class AnalysisListData : Serializable{

    @SerializedName("type")
    @Expose
    var type: String? = null

    @SerializedName("lable")
    @Expose
    var lable: String? = null

    @SerializedName("title")
    @Expose
    var title: String? = null

    @SerializedName("desc")
    @Expose
    var desc: String? = null

    @SerializedName("is_selected")
    @Expose
    var isSelected: Boolean? = null

    @SerializedName("is_mobile_number")
    @Expose
    var isMobileNumber: Boolean? = null

    @SerializedName("is_recomended")
    @Expose
    var isRecomended: Boolean? = null

    @SerializedName("caution")
    @Expose
    var caution: Boolean? = null

    @SerializedName("footer_text")
    @Expose
    var footerText: String? = null

    @SerializedName("bank_state")
    @Expose
    var bankState: String? = null

    @SerializedName("mob_numbers")
    @Expose
    var mobNumbers: List<String>? = null

    var checked: Boolean? = false

}
