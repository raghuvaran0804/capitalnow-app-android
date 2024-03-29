package com.capitalnowapp.mobile.beans;

import com.capitalnowapp.mobile.models.loan.InstalmentData;

import java.io.Serializable;
import java.util.ArrayList;

public class ApplyLoan implements Serializable {
    private String receivedOTP;
    private String otpPassword;
    private int amount;
    private int tenureDays;
    private int serviceFee;
    private int processingCharges;
    private int newProcessingCharges;
    private int total;
    private String promo_code;
    private String loanType;
    private String amazonAmount;
    private String bankAmount;
    private String amazonNumber;
    private String cashback_amt;
    private String tenureType;
    private String emiCount;
    private int Qcr_purpose_of_loan = 0;
    private String Qcr_custom_purpose;
    public String Qcr_req_promo_code;
    public String current_location;

    private ArrayList<InstalmentData> InstalmentDataList;

    public String getReceivedOTP() {
        return receivedOTP;
    }

    public void setReceivedOTP(String receivedOTP) {
        this.receivedOTP = receivedOTP;
    }

    public String getOtpPassword() {
        return otpPassword;
    }

    public void setOtpPassword(String otpPassword) {
        this.otpPassword = otpPassword;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public String getCurrent_location() {
        return current_location;
    }

    public void setCurrent_location(String current_location) {
        this.current_location = current_location;
    }

    public int getTenureDays() {
        return tenureDays;
    }

    public void setTenureDays(int tenureDays) {
        this.tenureDays = tenureDays;
    }

    public String getEmiCount() {
        return emiCount;
    }

    public void setEmiCount(String emiCount) {
        this.emiCount = emiCount;
    }

    public int getServiceFee() {
        return serviceFee;
    }

    public void setServiceFee(int serviceFee) {
        this.serviceFee = serviceFee;
    }

    public int getProcessingCharges() {
        return processingCharges;
    }

    public void setProcessingCharges(int processingCharges) {
        this.processingCharges = processingCharges;
    }

    public int getNewProcessingCharges() {
        return newProcessingCharges;
    }

    public void setNewProcessingCharges(int newProcessingCharges) {
        this.newProcessingCharges = newProcessingCharges;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public String getPromo_code() {
        return promo_code;
    }
    public void setPromo_code(String promo_code) {
        this.promo_code = promo_code;
    }

    public String getLoanType() {
        return loanType;
    }

    public void setLoanType(String loanType) {
        this.loanType = loanType;
    }

    public String getAmazonAmount() {
        return amazonAmount;
    }

    public void setAmazonAmount(String amazonAmount) {
        this.amazonAmount = amazonAmount;
    }

    public String getBankAmount() {
        return bankAmount;
    }

    public void setBankAmount(String bankAmount) {
        this.bankAmount = bankAmount;
    }

    public String getAmazonNumber() {
        return amazonNumber;
    }

    public void setAmazonNumber(String amazonNumber) {
        this.amazonNumber = amazonNumber;
    }

    public String getCashback_amt() {
        return cashback_amt;
    }

    public void setCashback_amt(String cashback_amt) {
        this.cashback_amt = cashback_amt;
    }

    public String getTenureType() {
        return tenureType;
    }

    public void setTenureType(String tenureType) {
        this.tenureType = tenureType;
    }

    public ArrayList<InstalmentData> getInstalmentDataList() {
        return InstalmentDataList;
    }

    public void setInstalmentDataList(ArrayList<InstalmentData> InstalmentDataList) {
        this.InstalmentDataList = InstalmentDataList;
    }

    public int getQcr_purpose_of_loan() {
        return Qcr_purpose_of_loan;
    }

    public void setQcr_purpose_of_loan(int qcr_purpose_of_loan) {
        Qcr_purpose_of_loan = qcr_purpose_of_loan;
    }

    public String getQcr_custom_purpose() {
        return Qcr_custom_purpose;
    }

    public void setQcr_custom_purpose(String qcr_custom_purpose) {
        Qcr_custom_purpose = qcr_custom_purpose;
    }

    public String getQcr_req_promo_code() {
        return Qcr_req_promo_code;
    }

    public void setQcr_req_promo_code(String qcr_req_promo_code) {
        this.Qcr_req_promo_code = qcr_req_promo_code;
    }
}
