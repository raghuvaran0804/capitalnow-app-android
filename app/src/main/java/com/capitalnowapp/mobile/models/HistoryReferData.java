package com.capitalnowapp.mobile.models;

import com.capitalnowapp.mobile.beans.RewardPointsData;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class HistoryReferData implements Serializable {

    @SerializedName("razor_pay_api_key")
    @Expose
    private String razorPayApiKey;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("balance")
    @Expose
    private Integer balance;
    @SerializedName("reward_points")
    @Expose
    private Integer rewardPoints;
    @SerializedName("reward_pts_data")
    @Expose
    private List<RewardPointsData> rewardPtsData = null;
    @SerializedName("cashback_data")
    @Expose
    private List<Object> cashbackData = null;

    public String getRazorPayApiKey() {
        return razorPayApiKey;
    }

    public void setRazorPayApiKey(String razorPayApiKey) {
        this.razorPayApiKey = razorPayApiKey;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getBalance() {
        return balance;
    }

    public void setBalance(Integer balance) {
        this.balance = balance;
    }

    public Integer getRewardPoints() {
        return rewardPoints;
    }

    public void setRewardPoints(Integer rewardPoints) {
        this.rewardPoints = rewardPoints;
    }

    public List<RewardPointsData> getRewardPtsData() {
        return rewardPtsData;
    }

    public void setRewardPtsData(List<RewardPointsData> rewardPtsData) {
        this.rewardPtsData = rewardPtsData;
    }

    public List<Object> getCashbackData() {
        return cashbackData;
    }

    public void setCashbackData(List<Object> cashbackData) {
        this.cashbackData = cashbackData;
    }

}