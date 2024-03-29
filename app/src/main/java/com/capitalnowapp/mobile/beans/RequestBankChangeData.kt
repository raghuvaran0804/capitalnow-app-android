package com.capitalnowapp.mobile.beans

import java.io.Serializable

class RequestBankChangeData : Serializable {

    var userId: String? = null
    var accountHolderName: String? = null
    var accountNumber: String? = null
    var ifscCode: String? = null
    var bankName: String? = null
    var branchName: String? = null
    var bankStatement: String? = null
    var isIdProofExt = false
    var bankAccountPassword: String? = null
}