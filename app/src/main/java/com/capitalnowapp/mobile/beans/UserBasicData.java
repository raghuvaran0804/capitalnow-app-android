package com.capitalnowapp.mobile.beans;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;


public class UserBasicData implements Serializable {
    @SerializedName("max_loanamount")
    @Expose
    private String maxLoanAmount;
    @SerializedName("user_rating")
    @Expose
    private String userRating;
    @SerializedName("ureward_points")
    @Expose
    private String rewardPoints;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("user_mobile")
    @Expose
    private String phoneNumber;
    @SerializedName("qc_id")
    @Expose
    private String qcId;
    @SerializedName("membership_name")
    @Expose
    private String membershipName;
    @SerializedName("profile_picture")
    @Expose
    private String profilePicture;

    public String getMaxLoanAmount() {
        return maxLoanAmount;
    }

    public String getUserRating() {
        return userRating;
    }

    public String getRewardPoints() {
        return rewardPoints;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getQcId() {
        return qcId;
    }

    public String getMembershipName() {
        return membershipName;
    }

    public String getProfile_picture() {
        return profilePicture;
    }

    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }

    @Override
    public String toString() {
        return "UserBasicData{" +
                "maxLoanAmount='" + maxLoanAmount + '\'' +
                ", userRating='" + userRating + '\'' +
                ", rewardPoints='" + rewardPoints + '\'' +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", qcId='" + qcId + '\'' +
                ", membershipName='" + membershipName + '\'' +
                ", profilePicture='" + profilePicture + '\'' +
                '}';
    }
}
