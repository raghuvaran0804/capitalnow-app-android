package com.capitalnowapp.mobile.beans;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class CashBackData implements Serializable {
    @SerializedName("refer_to")
    @Expose
    private String referTo;
    @SerializedName("refer_by")
    @Expose
    private String referBy;
    @SerializedName("amount")
    @Expose
    private String amount;
    @SerializedName("transaction_id")
    @Expose
    private String transactionId;
    @SerializedName("date")
    @Expose
    private String date;
    @SerializedName("type")
    @Expose
    private String type;

    public String getReferTo() {
        return referTo;
    }

    public String getReferBy() {
        return referBy;
    }

    public String getAmount() {
        return amount;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public String getDate() {
        return date;
    }

    public String getType() {
        return type;
    }

    @Override
    public String toString() {
        return "CashbackData{" +
                "referTo='" + referTo + '\'' +
                ", referBy='" + referBy + '\'' +
                ", amount='" + amount + '\'' +
                ", transactionId='" + transactionId + '\'' +
                ", date='" + date + '\'' +
                ", type='" + type + '\'' +
                '}';
    }
}
