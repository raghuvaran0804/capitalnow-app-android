package com.capitalnowapp.mobile.beans;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class RewardPointsData implements Serializable {
    @SerializedName("refer_to")
    @Expose
    private String referTo;
    @SerializedName("refer_by")
    @Expose
    private String referBy;
    @SerializedName("refer_text")
    @Expose
    private String referText;
    @SerializedName("refer_id")
    @Expose
    private String referId;
    @SerializedName("amount")
    @Expose
    private String amount;
    @SerializedName("reward_type")
    @Expose
    private String rewardType;
    @SerializedName("transaction_id")
    @Expose
    private String transactionId;
    @SerializedName("date")
    @Expose
    private String date;
    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("points")
    @Expose
    private String points;
    @SerializedName("loan_id")
    @Expose
    private String loanId;
    @SerializedName("trans_id")
    @Expose
    private String transId;
    @SerializedName("refered_cid")
    @Expose
    private String referedCid;

    public String getReferTo() {
        return referTo;
    }

    public void setReferTo(String referTo) {
        this.referTo = referTo;
    }

    public String getReferBy() {
        return referBy;
    }

    public void setReferBy(String referBy) {
        this.referBy = referBy;
    }

    public String getReferText() {
        return referText;
    }

    public void setReferText(String referText) {
        this.referText = referText;
    }

    public String getReferId() {
        return referId;
    }

    public void setReferId(String referId) {
        this.referId = referId;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getRewardType() {
        return rewardType;
    }

    public void setRewardType(String rewardType) {
        this.rewardType = rewardType;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPoints() {
        return points;
    }

    public void setPoints(String points) {
        this.points = points;
    }

    public String getLoanId() {
        return loanId;
    }

    public void setLoanId(String loanId) {
        this.loanId = loanId;
    }

    public String getTransId() {
        return transId;
    }

    public void setTransId(String transId) {
        this.transId = transId;
    }

    public String getReferedCid() {
        return referedCid;
    }

    public void setReferedCid(String referedCid) {
        this.referedCid = referedCid;
    }

    @Override
    public String toString() {
        return "RewardPointsData{" +
                "refer_to='" + referTo + '\'' +
                ", refer_by='" + referBy + '\'' +
                ", refer_text='" + referText + '\'' +
                ", refer_id='" + referId + '\'' +
                ", points='" + points + '\'' +
                ", type='" + type + '\'' +
                ", loanId='" + loanId + '\'' +
                ", reward_type='" + rewardType + '\'' +
                ", amount='" + amount + '\'' +
                ", transactionId='" + transactionId + '\'' +
                ", trans_id='" + transId + '\'' +
                ", date='" + date + '\'' +
                ", refered_cid='" + referedCid + '\'' +
                '}';
    }
}
