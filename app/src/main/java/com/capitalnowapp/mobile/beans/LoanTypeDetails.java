package com.capitalnowapp.mobile.beans;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class LoanTypeDetails implements Serializable {
    @SerializedName("loan_type")
    @Expose
    private String loanType;
    @SerializedName("loan_text")
    @Expose
    private String loanText;
    @SerializedName("loan_sub_text")
    @Expose
    private String loanSubText;
    @SerializedName("is_checked")
    @Expose
    private String check;
    @SerializedName("offer_img")
    @Expose
    private String offerImg;
    @SerializedName("loan_tandc")
    @Expose
    private String loanTerms;
    @SerializedName("loan_link_keyword")
    @Expose
    private String loanTermsKeyword;

    public String getLoanType() {
        return loanType;
    }

    public void setLoanType(String loanType) {
        this.loanType = loanType;
    }

    public String getLoanText() {
        return loanText;
    }

    public void setLoanText(String loanText) {
        this.loanText = loanText;
    }

    public String getLoanSubText() {
        return loanSubText;
    }

    public void setLoanSubText(String loanSubText) {
        this.loanSubText = loanSubText;
    }

    public String getCheck() {
        return check;
    }

    public void setCheck(String check) {
        this.check = check;
    }

    public String getOfferImg() {
        return offerImg;
    }

    public void setOfferImg(String offerImg) {
        this.offerImg = offerImg;
    }

    public String getLoanTerms() {
        return loanTerms;
    }

    public void setLoanTerms(String loanTerms) {
        this.loanTerms = loanTerms;
    }

    public String getLoanTermsKeyword() {
        return loanTermsKeyword;
    }

    public void setLoanTermsKeyword(String loanTermsKeyword) {
        this.loanTermsKeyword = loanTermsKeyword;
    }
}
