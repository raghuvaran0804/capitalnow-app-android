package com.capitalnowapp.mobile.models

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName
import java.io.Serializable


class DealerTableData :Serializable{
    @SerializedName("dealer_id")
    @Expose
    var dealerId: String? = null

    @SerializedName("name")
    @Expose
    var name: String? = null

    @SerializedName("twld_branch_code")
    @Expose
    var twldBranchCode: String? = null

    @SerializedName("twl_owner_name")
    @Expose
    var twlOwnerName: String? = null

    @SerializedName("twl_owner_number")
    @Expose
    var twlOwnerNumber: String? = null

    override fun toString(): String {
        return name.toString()
    }

}