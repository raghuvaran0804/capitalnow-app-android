package com.capitalnowapp.mobile.beans;

import java.io.Serializable;

public class UploadDocuments implements Serializable {
    private String userId;
    private String idProof;
    private boolean isIdProofExt;
    private String addressProof;
    private String addressProofPwd;
    private boolean isAddressProofExt;
    private String salSlip;
    private String salSlipPassword;
    private boolean isSalSlipExt;
    private String bankStmt;
    private String bankStmtPassword;
    private boolean isBankStmtExt;
    private String addressProof2;
    private String bankStmt2;
    private String bankStmt3;
    private String customersignature;
    private String signature_consent_req;

    private String present_address_pwd;
    private String proof_present_address = "";
    private String proof_present_address2 = "";
    private Boolean present_add_as_poa;


    public UploadDocuments(String userId) {
        this.userId = userId;
        this.idProof = "";
        this.isIdProofExt = false;
        this.addressProof = "";
        this.customersignature = "";
        this.isAddressProofExt = false;
        this.salSlip = "";
        this.salSlipPassword = "";
        this.isSalSlipExt = false;
        this.bankStmt = "";
        this.bankStmtPassword = "";
        this.isBankStmtExt = false;
        this.addressProof2 = "";
        this.bankStmt2 = "";
        this.bankStmt3 = "";
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getIdProof() {
        return idProof;
    }

    public void setIdProof(String idProof) {
        this.idProof = idProof;
    }

    public boolean isIdProofExt() {
        return isIdProofExt;
    }

    public void setIdProofExt(boolean idProofExt) {
        isIdProofExt = idProofExt;
    }

    public String getAddressProof() {
        return addressProof;
    }

    public void setAddressProof(String addressProof) {
        this.addressProof = addressProof;
    }

    public boolean isAddressProofExt() {
        return isAddressProofExt;
    }

    public void setAddressProofExt(boolean addressProofExt) {
        isAddressProofExt = addressProofExt;
    }

    public String getSalSlip() {
        return salSlip;
    }

    public void setSalSlip(String salSlip) {
        this.salSlip = salSlip;
    }

    public String getSalSlipPassword() {
        return salSlipPassword;
    }

    public void setSalSlipPassword(String salSlipPassword) {
        this.salSlipPassword = salSlipPassword;
    }

    public boolean isSalSlipExt() {
        return isSalSlipExt;
    }

    public void setSalSlipExt(boolean salSlipExt) {
        isSalSlipExt = salSlipExt;
    }

    public String getBankStmt() {
        return bankStmt;
    }

    public void setBankStmt(String bankStmt) {
        this.bankStmt = bankStmt;
    }

    public String getBankStmtPassword() {
        return bankStmtPassword;
    }

    public void setBankStmtPassword(String bankStmtPassword) {
        this.bankStmtPassword = bankStmtPassword;
    }

    public boolean isBankStmtExt() {
        return isBankStmtExt;
    }

    public void setBankStmtExt(boolean bankStmtExt) {
        isBankStmtExt = bankStmtExt;
    }

    public String getAddressProof2() {
        return addressProof2;
    }

    public void setAddressProof2(String addressProof2) {
        this.addressProof2 = addressProof2;
    }

    public String getBankStmt2() {
        return bankStmt2;
    }

    public void setBankStmt2(String bankStmt2) {
        this.bankStmt2 = bankStmt2;
    }

    public String getBankStmt3() {
        return bankStmt3;
    }

    public void setBankStmt3(String bankStmt3) {
        this.bankStmt3 = bankStmt3;
    }

    @Override
    public String toString() {
        return "UploadDocuments{" +
                "userId='" + userId + '\'' +
                ", idProof='" + idProof + '\'' +
                ", isIdProofExt=" + isIdProofExt +
                ", addressProof='" + addressProof + '\'' +
                ", isAddressProofExt=" + isAddressProofExt +
                ", salSlip='" + salSlip + '\'' +
                ", salSlipPassword='" + salSlipPassword + '\'' +
                ", isSalSlipExt=" + isSalSlipExt +
                ", bankStmt='" + bankStmt + '\'' +
                ", bankStmtPassword='" + bankStmtPassword + '\'' +
                ", isBankStmtExt=" + isBankStmtExt +
                ", addressProof2='" + addressProof2 + '\'' +
                ", bankStmt2='" + bankStmt2 + '\'' +
                ", bankStmt3='" + bankStmt3 + '\'' +
                '}';
    }

    public String getAddressProofPwd() {
        return addressProofPwd;
    }

    public void setAddressProofPwd(String addressProofPwd) {
        this.addressProofPwd = addressProofPwd;
    }

    public String getCustomersignature() {
        return customersignature;
    }

    public void setCustomersignature(String customersignature) {
        this.customersignature = customersignature;
    }

    public String getSignature_concent() {
        return signature_consent_req;
    }

    public void setSignature_concent(String signature_concent) {
        this.signature_consent_req = signature_concent;
    }

    public String getPresent_address_pwd() {
        return present_address_pwd;
    }

    public void setPresent_address_pwd(String present_address_pwd) {
        this.present_address_pwd = present_address_pwd;
    }

    public String getProof_present_address() {
        return proof_present_address;
    }

    public void setProof_present_address(String proof_present_address) {
        this.proof_present_address = proof_present_address;
    }

    public Boolean getPresent_add_as_poa() {
        return present_add_as_poa;
    }

    public void setPresent_add_as_poa(Boolean present_add_as_poa) {
        this.present_add_as_poa = present_add_as_poa;
    }

    public String getProof_present_address2() {
        return proof_present_address2;
    }

    public void setProof_present_address2(String proof_present_address2) {
        this.proof_present_address2 = proof_present_address2;
    }
}
