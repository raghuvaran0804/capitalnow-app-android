package com.capitalnowapp.mobile.beans;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class CcaGatewayDetails implements Serializable {

    @SerializedName("merchant_id")
    @Expose
    private String merchantId;
    @SerializedName("access_code")
    @Expose
    private String accessCode;
    @SerializedName("transaction_url")
    @Expose
    private String transactionUrl;
    @SerializedName("redirect_url")
    @Expose
    private String redirectUrl;
    @SerializedName("cancel_url")
    @Expose
    private String cancelUrl;

    @SerializedName("data_accept")
    @Expose
    private String dataAccept;

    public String getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(String merchantId) {
        this.merchantId = merchantId;
    }

    public String getAccessCode() {
        return accessCode;
    }

    public void setAccessCode(String accessCode) {
        this.accessCode = accessCode;
    }

    public String getTransactionUrl() {
        return transactionUrl;
    }

    public void setTransactionUrl(String transactionUrl) {
        this.transactionUrl = transactionUrl;
    }

    public String getRedirectUrl() {
        return redirectUrl;
    }

    public void setRedirectUrl(String redirectUrl) {
        this.redirectUrl = redirectUrl;
    }

    public String getCancelUrl() {
        return cancelUrl;
    }

    public void setCancelUrl(String cancelUrl) {
        this.cancelUrl = cancelUrl;
    }

    public String getDataAccept() {
        return dataAccept;
    }

    public void setDataAccept(String dataAccept) {
        this.dataAccept = dataAccept;
    }
}
