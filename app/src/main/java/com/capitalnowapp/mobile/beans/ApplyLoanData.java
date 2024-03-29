package com.capitalnowapp.mobile.beans;

import com.capitalnowapp.mobile.models.CustomPopUp;
import com.capitalnowapp.mobile.models.MonitoringCardData;
import com.capitalnowapp.mobile.models.NachCardData;
import com.capitalnowapp.mobile.models.NachData;
import com.capitalnowapp.mobile.models.SecurityPopUp;
import com.capitalnowapp.mobile.models.TwlProcessingFee;
import com.capitalnowapp.mobile.models.loan.TenureData;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class ApplyLoanData implements Serializable {
    @SerializedName("apply_min_loan_amount")
    @Expose
    private Double applyLoanMinAmount;
    @SerializedName("nach_card_data")
    @Expose
    private NachCardData nachCardData;

    @SerializedName("security_popup")
    @Expose
    private SecurityPopUp securityPopUp;

    @SerializedName("custom_popup")
    @Expose
    private CustomPopUp customPopUp;

    @SerializedName("monitoring_card_data")
    @Expose
    private MonitoringCardData monitoringCardData;

    @SerializedName("nach_data")
    @Expose
    private NachData nachData = null;
    @SerializedName("irc")
    @Expose
    private int irc;
    @SerializedName("tenure_data")
    @Expose
    private List<TenureData> tenureDataList;
    @SerializedName("user_service_charges")
    @Expose
    private List<UserServiceCharges> serviceChargesList;
    @SerializedName("user_interest_charges")
    @Expose
    private UserInterestCharges userInterestCharges;

    @SerializedName("tanc_text")
    @Expose
    private UserTermsData userTermsData;

    @SerializedName("loan_agreement_consent")
    @Expose
    private LoanAgreementConsent loanAgreementConsent;

    @SerializedName("amazon_pay_number")
    @Expose
    private String amazonPayNumber;

    @SerializedName("show_cc_card")
    @Expose
    private Integer showCcCard;

    @SerializedName("loan_tandc_link")
    @Expose
    private String apayTermsLink;

    @SerializedName("canEdit")
    @Expose
    private boolean canEdit;

    @SerializedName("eligibility_message")
    @Expose
    private String eligibilityMessage;

    @SerializedName("loan_types")
    @Expose
    private List<LoanTypeDetails> loanTypes = null;

    @SerializedName("twl_processing_fees")
    @Expose
    private TwlProcessingFee twlProcessingFee = null;

    @SerializedName("cashback_conditions")
    @Expose
    private List<CashBackDetails> cashbackConditions = null;

    public List<UserServiceCharges> getServiceChargesList() {
        return serviceChargesList;
    }

    public UserInterestCharges getUserInterestCharges() {
        return userInterestCharges;
    }

    @Override
    public String toString() {
        return "ApplyLoanData{" +
                "serviceChargesList=" + serviceChargesList +
                ", userInterestCharges=" + userInterestCharges +
                '}';
    }

    public CustomPopUp getCustomPopUp() {
        return customPopUp;
    }

    public void setCustomPopUp(CustomPopUp customPopUp) {
        this.customPopUp = customPopUp;
    }

    public SecurityPopUp getSecurityPopUp() {
        return securityPopUp;
    }

    public void setSecurityPopUp(SecurityPopUp securityPopUp) {
        this.securityPopUp = securityPopUp;
    }

    public Integer getShowCcCard() {
        return showCcCard;
    }

    public void setShowCcCard(Integer showCcCard) {
        this.showCcCard = showCcCard;
    }

    public String getAmazonPayNumber() {
        return amazonPayNumber;
    }

    public void setAmazonPayNumber(String amazonPayNumber) {
        this.amazonPayNumber = amazonPayNumber;
    }

    public boolean isCanEdit() {
        return canEdit;
    }

    public void setCanEdit(boolean canEdit) {
        this.canEdit = canEdit;
    }

    public List<LoanTypeDetails> getLoanTypes() {
        return loanTypes;
    }

    public void setLoanTypes(List<LoanTypeDetails> loanTypes) {
        this.loanTypes = loanTypes;
    }

    public List<CashBackDetails> getCashbackConditions() {
        return cashbackConditions;
    }

    public void setCashbackConditions(List<CashBackDetails> cashbackConditions) {
        this.cashbackConditions = cashbackConditions;
    }

    public String getApayTermsLink() {
        return apayTermsLink;
    }

    public void setApayTermsLink(String apayTermsLink) {
        this.apayTermsLink = apayTermsLink;
    }

    public UserTermsData getUserTermsData() {
        return userTermsData;
    }

    public void setUserTermsData(UserTermsData userTermsData) {
        this.userTermsData = userTermsData;
    }

    public Double getApplyLoanMinAmount() {
        return applyLoanMinAmount;
    }

    public void setApplyLoanMinAmount(Double applyLoanMinAmount) {
        this.applyLoanMinAmount = applyLoanMinAmount;
    }

    public List<TenureData> getTenureDataList() {
        return tenureDataList;
    }

    public void setTenureDataList(List<TenureData> tenureDataList) {
        this.tenureDataList = tenureDataList;
    }

    public void setServiceChargesList(List<UserServiceCharges> serviceChargesList) {
        this.serviceChargesList = serviceChargesList;
    }

    public void setUserInterestCharges(UserInterestCharges userInterestCharges) {
        this.userInterestCharges = userInterestCharges;
    }

    public int getIrc() {
        return irc;
    }

    public void setIrc(int irc) {
        this.irc = irc;
    }

    public String getEligibilityMessage() {
        return eligibilityMessage;
    }

    public void setEligibilityMessage(String eligibilityMessage) {
        this.eligibilityMessage = eligibilityMessage;
    }

    public LoanAgreementConsent getLoanAgreementConsent() {
        return loanAgreementConsent;
    }

    public void setLoanAgreementConsent(LoanAgreementConsent loanAgreementConsent) {
        this.loanAgreementConsent = loanAgreementConsent;
    }

    public TwlProcessingFee getTwlProcessingFee() {
        return twlProcessingFee;
    }

    public void setTwlProcessingFee(TwlProcessingFee twlProcessingFee) {
        this.twlProcessingFee = twlProcessingFee;
    }

    public NachCardData getNachCardData() {
        return nachCardData;
    }

    public NachData getNachData() {
        return nachData;
    }


    public void setNachData(NachData nachData) {
        this.nachData = nachData;
    }

    public void setNachCardData(NachCardData nachCardData) {
        this.nachCardData = nachCardData;
    }

    public MonitoringCardData getMonitoringCardData() {
        return monitoringCardData;
    }

    public void setMonitoringCardData(MonitoringCardData monitoringCardData) {
        this.monitoringCardData = monitoringCardData;
    }
}
