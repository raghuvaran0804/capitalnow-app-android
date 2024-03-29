package com.capitalnowapp.mobile.beans;

import com.capitalnowapp.mobile.util.Utility;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class UserData implements Serializable {
    @SerializedName("loan_eligibility_limit")
    @Expose
    private String loanEligibilityLimit;
    @SerializedName("stakeholder_id")
    @Expose
    private String stakeholder_id;
    @SerializedName("has_taken_first_loan")
    @Expose
    private String hasTakenFirstLoan;
    @SerializedName("current_loan_availed")
    @Expose
    private String currentLoanAvailed;
    @SerializedName("current_eligible_limit")
    @Expose
    private String currentEligibleLimit;
    @SerializedName("user_basic_data")
    @Expose
    private UserBasicData userBasicData;
    @SerializedName("customer_relationship_avatar")
    @Expose
    private String customer_relationship_avatar;
    @SerializedName("customer_relationship_details")
    @Expose
    private String account_mng_details;
    @SerializedName("status")
    @Expose
    private String loanStatus;
    @SerializedName("status_code")
    @Expose
    private String statusCode;
    @SerializedName("twl_current_loan_availed")
    @Expose
    private String twlCurrentLoanAvailed;
    @SerializedName("show_pl_card")
    @Expose
    private Boolean showPlCard;
    @SerializedName("show_pl_ofr_card")
    @Expose
    private boolean showPlOfferCard;
    @SerializedName("show_loan_details")
    @Expose
    private boolean showPlofferLoanDetails;
    @SerializedName("show_twl_card")
    @Expose
    private Boolean showTwlCard;
    @SerializedName("twl_loan_eligibility_limit")
    @Expose
    private String twlLoanEligibilityLimit;
    @SerializedName("twl_current_eligible_limit")
    @Expose
    private String twlCurrentEligibleLimit;
    @SerializedName("has_membership")
    @Expose
    private String hasMembership;
    @SerializedName("per_elig_title")
    @Expose
    private String perEligTitle;
    @SerializedName("per_elig_text")
    @Expose
    private String perEligText;
    @SerializedName("per_elig_notes")
    @Expose
    private String perEligNotes;
    @SerializedName("per_elig_cta_text")
    @Expose
    private String perEligCtaText;
    @SerializedName("per_elig_sub_text")
    @Expose
    private String perEligSubText;
    @SerializedName("per_elig_status_redirect")
    @Expose
    private Integer perEligStatusRedirect;
    @SerializedName("twl_elig_title")
    @Expose
    private String twlEligTitle;
    @SerializedName("twl_elig_text")
    @Expose
    private String twlEligText;
    @SerializedName("twl_elig_notes")
    @Expose
    private String twlEligNotes;
    @SerializedName("twl_elig_cta_text")
    @Expose
    private String twlEligCtaText;
    @SerializedName("twl_elig_status_redirect")
    @Expose
    private Integer twlEligStatusRedirect;
    @SerializedName("pcl_loan_id")
    @Expose
    private String pclLoanId;
    @SerializedName("pcl_loan_am")
    @Expose
    private String pclLoanAmount;
    @SerializedName("pcl_utr_no")
    @Expose
    private String pclUtrNo;
    @SerializedName("pcl_tenure")
    @Expose
    private String pclTenure;
    @SerializedName("pcl_emi_am")
    @Expose
    private String pclEmiAmount;
    @SerializedName("not_elig_text")
    @Expose
    private String notEligText;
    @SerializedName("elig_pretext")
    @Expose
    private Boolean perEligPreText;
    @SerializedName("twl_elig_pretext")
    @Expose
    private Boolean twlEligPreText;

    public String getNotEligText() {
        return notEligText;
    }

    public void setNotEligText(String notEligText) {
        this.notEligText = notEligText;
    }

    public String getPerEligSubText() {
        return perEligSubText;
    }

    public void setPerEligSubText(String perEligSubText) {
        this.perEligSubText = perEligSubText;
    }

    public Boolean getPerEligPreText() {
        return perEligPreText;
    }

    public void setPerEligPreText(Boolean perEligPreText) {
        this.perEligPreText = perEligPreText;
    }

    public Boolean getTwlEligPreText() {
        return twlEligPreText;
    }

    public void setTwlEligPreText(Boolean twlEligPreText) {
        this.twlEligPreText = twlEligPreText;
    }

    public String getPclLoanId() {
        return pclLoanId;
    }

    public void setPclLoanId(String pclLoanId) {
        this.pclLoanId = pclLoanId;
    }

    public String getPclLoanAmount() {
        return pclLoanAmount;
    }

    public void setPclLoanAmount(String pclLoanAmount) {
        this.pclLoanAmount = pclLoanAmount;
    }

    public String getPclUtrNo() {
        return pclUtrNo;
    }

    public void setPclUtrNo(String pclUtrNo) {
        this.pclUtrNo = pclUtrNo;
    }

    public String getPclTenure() {
        return pclTenure;
    }

    public void setPclTenure(String pclTenure) {
        this.pclTenure = pclTenure;
    }

    public String getPclEmiAmount() {
        return pclEmiAmount;
    }

    public void setPclEmiAmount(String pclEmiAmount) {
        this.pclEmiAmount = pclEmiAmount;
    }

    public String getHasMembership() {
        return hasMembership;
    }

    public void setHasMembership(String hasMembership) {
        this.hasMembership = hasMembership;
    }

    public String getLoanEligibilityLimit() {
        return loanEligibilityLimit;
    }

    public Boolean getShowPlCard() {
        return showPlCard;
    }

    public Boolean getShowPlOfferCard() {
        return showPlOfferCard;
    }

    public void setShowPlOfferCard(Boolean showPlOfferCard) {
        this.showPlOfferCard = showPlOfferCard;
    }

    public boolean isShowPlofferLoanDetails() {
        return showPlofferLoanDetails;
    }

    public void setShowPlofferLoanDetails(boolean showPlofferLoanDetails) {
        this.showPlofferLoanDetails = showPlofferLoanDetails;
    }

    public void setShowPlCard(Boolean showPlCard) {
        this.showPlCard = showPlCard;
    }

    public Boolean getShowTwlCard() {
        return showTwlCard;
    }

    public void setShowTwlCard(Boolean showTwlCard) {
        this.showTwlCard = showTwlCard;
    }

    public String getCurrentLoanAvailed() {
        return currentLoanAvailed;
    }

    public String getLoanStatus() {
        return loanStatus;
    }

    public String getCurrentEligibleLimit() {
        return currentEligibleLimit;
    }

    public UserBasicData getUserBasicData() {
        return userBasicData;
    }

    public String getCustomer_relationship_avatar() {
        return customer_relationship_avatar;
    }

    public String getAccount_mng_details() {
        return Utility.validString(account_mng_details) ? account_mng_details : Utility.NO_VALUE;
    }

    @Override
    public String toString() {
        return "UserData{" +
                "loanEligibilityLimit='" + loanEligibilityLimit + '\'' +
                ", currentLoanAvailed='" + currentLoanAvailed + '\'' +
                ", currentEligibleLimit='" + currentEligibleLimit + '\'' +
                ", userBasicData=" + userBasicData + '\'' +
                ", currentEligibleLimit='" + currentEligibleLimit + '\'' +
                '}';
    }

    public String getHasTakenFirstLoan() {
        return hasTakenFirstLoan;
    }

    public void setHasTakenFirstLoan(String hasTakenFirstLoan) {
        this.hasTakenFirstLoan = hasTakenFirstLoan;
    }

    public String getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }

    public String getStakeholder_id() {
        return stakeholder_id;
    }

    public void setStakeholder_id(String stakeholder_id) {
        this.stakeholder_id = stakeholder_id;
    }

    public String getTwlCurrentLoanAvailed() {
        return twlCurrentLoanAvailed;
    }

    public void setTwlCurrentLoanAvailed(String twlCurrentLoanAvailed) {
        this.twlCurrentLoanAvailed = twlCurrentLoanAvailed;
    }

    public String getTwlLoanEligibilityLimit() {
        return twlLoanEligibilityLimit;
    }

    public void setTwlLoanEligibilityLimit(String twlLoanEligibilityLimit) {
        this.twlLoanEligibilityLimit = twlLoanEligibilityLimit;
    }

    public String getTwlCurrentEligibleLimit() {
        return twlCurrentEligibleLimit;
    }

    public void setTwlCurrentEligibleLimit(String twlCurrentEligibleLimit) {
        this.twlCurrentEligibleLimit = twlCurrentEligibleLimit;
    }

    public String getPerEligTitle() {
        return perEligTitle;
    }

    public void setPerEligTitle(String perEligTitle) {
        this.perEligTitle = perEligTitle;
    }

    public String getPerEligText() {
        return perEligText;
    }

    public void setPerEligText(String perEligText) {
        this.perEligText = perEligText;
    }

    public String getPerEligNotes() {
        return perEligNotes;
    }

    public void setPerEligNotes(String perEligNotes) {
        this.perEligNotes = perEligNotes;
    }

    public String getPerEligCtaText() {
        return perEligCtaText;
    }

    public void setPerEligCtaText(String perEligCtaText) {
        this.perEligCtaText = perEligCtaText;
    }

    public Integer getPerEligStatusRedirect() {
        return perEligStatusRedirect;
    }

    public void setPerEligStatusRedirect(Integer perEligStatusRedirect) {
        this.perEligStatusRedirect = perEligStatusRedirect;
    }

    public String getTwlEligTitle() {
        return twlEligTitle;
    }

    public void setTwlEligTitle(String twlEligTitle) {
        this.twlEligTitle = twlEligTitle;
    }

    public String getTwlEligText() {
        return twlEligText;
    }

    public void setTwlEligText(String twlEligText) {
        this.twlEligText = twlEligText;
    }

    public String getTwlEligNotes() {
        return twlEligNotes;
    }

    public void setTwlEligNotes(String twlEligNotes) {
        this.twlEligNotes = twlEligNotes;
    }

    public String getTwlEligCtaText() {
        return twlEligCtaText;
    }

    public void setTwlEligCtaText(String twlEligCtaText) {
        this.twlEligCtaText = twlEligCtaText;
    }

    public Integer getTwlEligStatusRedirect() {
        return twlEligStatusRedirect;
    }

    public void setTwlEligStatusRedirect(Integer twlEligStatusRedirect) {
        this.twlEligStatusRedirect = twlEligStatusRedirect;
    }

}
