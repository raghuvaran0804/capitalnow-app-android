package com.capitalnowapp.mobile.beans;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class LoanAgreementConsent implements Serializable {

    @SerializedName("ubac_accept_message")
    @Expose
    private String ubac_accept_message;
    @SerializedName("agreement_validation")
    @Expose
    private String agreement_validation;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("show_star")
    @Expose
    private Boolean showStar;
    @SerializedName("ubac_show_menu")
    @Expose
    private Boolean ubac_show_menu;
    @SerializedName("loan_id")
    @Expose
    private String loanId;
    @SerializedName("passcode")
    @Expose
    private String passcode;
    @SerializedName("agreement_link")
    @Expose
    private String agreementLink;
    @SerializedName("agreement_hint")
    @Expose
    private String agreementHint;
    @SerializedName("action_required")
    @Expose
    private String action_required;
    @SerializedName("ubac_accept_status")
    @Expose
    private Boolean ubacAcceptStatus;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Boolean getShowStar() {
        return showStar;
    }

    public void setShowStar(Boolean showStar) {
        this.showStar = showStar;
    }

    public String getLoanId() {
        return loanId;
    }

    public void setLoanId(String loanId) {
        this.loanId = loanId;
    }

    public String getPasscode() {
        return passcode;
    }

    public void setPasscode(String passcode) {
        this.passcode = passcode;
    }

    public String getAgreementLink() {
        return agreementLink;
    }

    public void setAgreementLink(String agreementLink) {
        this.agreementLink = agreementLink;
    }

    public String getAgreementHint() {
        return agreementHint;
    }

    public void setAgreementHint(String agreementHint) {
        this.agreementHint = agreementHint;
    }

    public Boolean getUbacAcceptStatus() {
        return ubacAcceptStatus;
    }

    public void setUbacAcceptStatus(Boolean ubacAcceptStatus) {
        this.ubacAcceptStatus = ubacAcceptStatus;
    }

    public String getAction_required() {
        return action_required;
    }

    public void setAction_required(String action_required) {
        this.action_required = action_required;
    }

    public String getUbac_accept_message() {
        return ubac_accept_message;
    }

    public void setUbac_accept_message(String ubac_accept_message) {
        this.ubac_accept_message = ubac_accept_message;
    }

    public String getAgreement_validation() {
        return agreement_validation;
    }

    public void setAgreement_validation(String agreement_validation) {
        this.agreement_validation = agreement_validation;
    }

    public Boolean getUbac_show_menu() {
        return ubac_show_menu;
    }

    public void setUbac_show_menu(Boolean ubac_show_menu) {
        this.ubac_show_menu = ubac_show_menu;
    }
}
