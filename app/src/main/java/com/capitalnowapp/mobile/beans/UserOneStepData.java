package com.capitalnowapp.mobile.beans;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class UserOneStepData implements Serializable {
    @SerializedName("qc_id")
    @Expose
    private String qcId;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("sec_email")
    @Expose
    private String secEmail;
    @SerializedName("user_mobile")
    @Expose
    private String userMobile;
    @SerializedName("alt_mobile")
    @Expose
    private String altMobile;
    @SerializedName("dob")
    @Expose
    private String dob;
    @SerializedName("pan")
    @Expose
    private String panNumber;
    @SerializedName("present_address1")
    @Expose
    private String presentAddress1;
    @SerializedName("present_address2")
    @Expose
    private String presentAddress2;
    @SerializedName("present_address3")
    @Expose
    private String presentAddress3;
    @SerializedName("present_landmark")
    @Expose
    private String presentLandmark;
    @SerializedName("proinfo_address1")
    @Expose
    private String companyAddress1;
    @SerializedName("proinfo_address2")
    @Expose
    private String companyAddress2;
    @SerializedName("proinfo_name")
    @Expose
    private String companyName;
    @SerializedName("proinfo_department")
    @Expose
    private String department;
    @SerializedName("proinfo_designation")
    @Expose
    private String designation;
    @SerializedName("proinfo_ctc")
    @Expose
    private String ctc;
    @SerializedName("proinfo_city_id")
    @Expose
    private String city_id;
    @SerializedName("proinfo_phone")
    @Expose
    private String officePhoneNumber;
    @SerializedName("customer_bank_account_number")
    @Expose
    private String bankAccountNumber;
    @SerializedName("customer_bank_ifsc_code")
    @Expose
    private String bankIFSCCode;
    @SerializedName("rf_type")
    @Expose
    private String referType;
    @SerializedName("rf_frnd_number")
    @Expose
    private String promoCode = "";
    @SerializedName("proinfo_type")
    @Expose
    private String proInfoType;
    @SerializedName("proinfo_monthly_sal")
    @Expose
    private String monthlyNetSalary;

    public String getMonthlyNetSalary() {
        return monthlyNetSalary;
    }

    public void setMonthlyNetSalary(String monthlyNetSalary) {
        this.monthlyNetSalary = monthlyNetSalary;
    }

    public String getQcId() {
        return qcId;
    }

    public void setQcId(String qcId) {
        this.qcId = qcId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSecEmail() {
        return secEmail;
    }

    public void setSecEmail(String secEmail) {
        this.secEmail = secEmail;
    }

    public String getUserMobile() {
        return userMobile;
    }

    public void setUserMobile(String userMobile) {
        this.userMobile = userMobile;
    }

    public String getAltMobile() {
        return altMobile;
    }

    public void setAltMobile(String altMobile) {
        this.altMobile = altMobile;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getPanNumber() {
        return panNumber;
    }

    public void setPanNumber(String panNumber) {
        this.panNumber = panNumber;
    }

    public String getPresentAddress1() {
        return presentAddress1;
    }

    public void setPresentAddress1(String presentAddress1) {
        this.presentAddress1 = presentAddress1;
    }

    public String getPresentAddress2() {
        return presentAddress2;
    }

    public void setPresentAddress2(String presentAddress2) {
        this.presentAddress2 = presentAddress2;
    }

    public String getPresentAddress3() {
        return presentAddress3;
    }

    public void setPresentAddress3(String presentAddress3) {
        this.presentAddress3 = presentAddress3;
    }

    public String getPresentLandmark() {
        return presentLandmark;
    }

    public void setPresentLandmark(String presentLandmark) {
        this.presentLandmark = presentLandmark;
    }

    public String getCompanyAddress1() {
        return companyAddress1;
    }

    public void setCompanyAddress1(String companyAddress1) {
        this.companyAddress1 = companyAddress1;
    }

    public String getCompanyAddress2() {
        return companyAddress2;
    }

    public void setCompanyAddress2(String companyAddress2) {
        this.companyAddress2 = companyAddress2;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    public String getCtc() {
        return ctc;
    }

    public void setCtc(String ctc) {
        this.ctc = ctc;
    }

    public String getCity_id() {
        return city_id;
    }

    public void setCity_id(String city_id) {
        this.city_id = city_id;
    }

    public String getOfficePhoneNumber() {
        return officePhoneNumber;
    }

    public void setOfficePhoneNumber(String officePhoneNumber) {
        this.officePhoneNumber = officePhoneNumber;
    }

    public String getBankAccountNumber() {
        return bankAccountNumber;
    }

    public void setBankAccountNumber(String bankAccountNumber) {
        this.bankAccountNumber = bankAccountNumber;
    }

    public String getBankIFSCCode() {
        return bankIFSCCode;
    }

    public void setBankIFSCCode(String bankIFSCCode) {
        this.bankIFSCCode = bankIFSCCode;
    }

    public String getReferType() {
        return referType;
    }

    public void setReferType(String referType) {
        this.referType = referType;
    }

    public String getPromoCode() {
        return promoCode;
    }

    public void setPromoCode(String promoCode) {
        this.promoCode = promoCode;
    }

    public String getProInfoType() {
        return proInfoType;
    }

    public void setProInfoType(String proInfoType) {
        this.proInfoType = proInfoType;
    }

    @Override
    public String toString() {
        return "UserOneStepData{" +
                "qcId='" + qcId + '\'' +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", secEmail='" + secEmail + '\'' +
                ", userMobile='" + userMobile + '\'' +
                ", altMobile='" + altMobile + '\'' +
                ", dob='" + dob + '\'' +
                ", panNumber='" + panNumber + '\'' +
                ", presentAddress1='" + presentAddress1 + '\'' +
                ", presentAddress2='" + presentAddress2 + '\'' +
                ", presentAddress3='" + presentAddress3 + '\'' +
                ", presentLandmark='" + presentLandmark + '\'' +
                ", companyAddress1='" + companyAddress1 + '\'' +
                ", companyAddress2='" + companyAddress2 + '\'' +
                ", companyName='" + companyName + '\'' +
                ", department='" + department + '\'' +
                ", designation='" + designation + '\'' +
                ", ctc='" + ctc + '\'' +
                ", city_id='" + city_id + '\'' +
                ", officePhoneNumber='" + officePhoneNumber + '\'' +
                ", bankAccountNumber='" + bankAccountNumber + '\'' +
                ", bankIFSCCode='" + bankIFSCCode + '\'' +
                ", referType='" + referType + '\'' +
                ", promoCode='" + promoCode + '\'' +
                ", proInfoType='" + proInfoType + '\'' +
                '}';
    }
}
