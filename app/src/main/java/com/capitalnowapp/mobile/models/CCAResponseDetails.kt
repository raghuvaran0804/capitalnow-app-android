package com.capitalnowapp.mobile.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable


class CCAResponseDetails : Serializable {

    @SerializedName("order_id")
    @Expose
    var orderId: String? = null

    @SerializedName("tracking_id")
    @Expose
    var trackingId: String? = null

    @SerializedName("bank_ref_no")
    @Expose
    var bankRefNo: String? = null

    @SerializedName("order_status")
    @Expose
    var orderStatus: String? = null

    @SerializedName("failure_message")
    @Expose
    var failureMessage: String? = null

    @SerializedName("payment_mode")
    @Expose
    var paymentMode: String? = null

    @SerializedName("card_name")
    @Expose
    var cardName: String? = null

    @SerializedName("status_code")
    @Expose
    var statusCode: String? = null

    @SerializedName("status_message")
    @Expose
    var statusMessage: String? = null

    @SerializedName("currency")
    @Expose
    var currency: String? = null

    @SerializedName("amount")
    @Expose
    var amount: String? = null

    @SerializedName("billing_name")
    @Expose
    var billingName: String? = null

    @SerializedName("billing_address")
    @Expose
    var billingAddress: String? = null

    @SerializedName("billing_city")
    @Expose
    var billingCity: String? = null

    @SerializedName("billing_state")
    @Expose
    var billingState: String? = null

    @SerializedName("billing_zip")
    @Expose
    var billingZip: String? = null

    @SerializedName("billing_country")
    @Expose
    var billingCountry: String? = null

    @SerializedName("billing_tel")
    @Expose
    var billingTel: String? = null

    @SerializedName("billing_email")
    @Expose
    var billingEmail: String? = null

    @SerializedName("delivery_name")
    @Expose
    var deliveryName: String? = null

    @SerializedName("delivery_address")
    @Expose
    var deliveryAddress: String? = null

    @SerializedName("delivery_city")
    @Expose
    var deliveryCity: String? = null

    @SerializedName("delivery_state")
    @Expose
    var deliveryState: String? = null

    @SerializedName("delivery_zip")
    @Expose
    var deliveryZip: String? = null

    @SerializedName("delivery_country")
    @Expose
    var deliveryCountry: String? = null

    @SerializedName("delivery_tel")
    @Expose
    var deliveryTel: String? = null

    @SerializedName("merchant_param1")
    @Expose
    var merchantParam1: String? = null

    @SerializedName("merchant_param2")
    @Expose
    var merchantParam2: String? = null

    @SerializedName("merchant_param3")
    @Expose
    var merchantParam3: String? = null

    @SerializedName("merchant_param4")
    @Expose
    var merchantParam4: String? = null

    @SerializedName("merchant_param5")
    @Expose
    var merchantParam5: String? = null

    @SerializedName("vault")
    @Expose
    var vault: String? = null

    @SerializedName("offer_type")
    @Expose
    var offerType: String? = null

    @SerializedName("offer_code")
    @Expose
    var offerCode: String? = null

    @SerializedName("discount_value")
    @Expose
    var discountValue: String? = null

    @SerializedName("mer_amount")
    @Expose
    var merAmount: String? = null

    @SerializedName("eci_value")
    @Expose
    var eciValue: String? = null

    @SerializedName("retry")
    @Expose
    var retry: String? = null

    @SerializedName("response_code")
    @Expose
    var responseCode: String? = null

    @SerializedName("billing_notes")
    @Expose
    var billingNotes: String? = null

    @SerializedName("trans_date")
    @Expose
    var transDate: String? = null

    @SerializedName("bin_country")
    @Expose
    var binCountry: String? = null
}