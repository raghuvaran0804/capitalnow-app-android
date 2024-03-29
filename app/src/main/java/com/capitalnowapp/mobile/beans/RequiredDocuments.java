package com.capitalnowapp.mobile.beans;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class RequiredDocuments implements Serializable {
    @SerializedName("status_redirect")
    @Expose
    private int statusRedirect;
    @SerializedName("proof_id")
    @Expose
    private int proofOfId;
    @SerializedName("proof_address")
    @Expose
    private int proofOfAddress;
    @SerializedName("req_proof_present_address")
    @Expose
    private int req_proof_present_address;
    @SerializedName("proof_present_address")
    @Expose
    private int proofOfPresentAddress;
    @SerializedName("sal_slip")
    @Expose
    private int salSlip;
    @SerializedName("bank_statement")
    @Expose
    private int bankStatement;
    @SerializedName("signature_consent_req")
    @Expose
    private String signature_consent_req;
    @SerializedName("customersignature")
    @Expose
    private int signature;
    @SerializedName("consent_text")
    @Expose
    private UserTermsData userTermsData;

    public String getSignature_consent_req() {
        return signature_consent_req;
    }

    public void setSignature_consent_req(String signature_consent_req) {
        this.signature_consent_req = signature_consent_req;
    }

    public int getStatusRedirect() {
        return statusRedirect;
    }

    public int getProofOfId() {
        return proofOfId;
    }

    public int getProofOfAddress() {
        return proofOfAddress;
    }

    public int getSalSlip() {
        return salSlip;
    }

    public int getBankStatement() {
        return bankStatement;
    }

    @Override
    public String toString() {
        return "RequiredDocuments{" +
                "statusRedirect=" + statusRedirect +
                ", proofOfId=" + proofOfId +
                ", proofOfAddress=" + proofOfAddress +
                ", proofOfPresentAddress=" + proofOfPresentAddress +
                ", salSlip=" + salSlip +
                ", bankStatement=" + bankStatement +
                '}';
    }

    public int getSignature() {
        return signature;
    }

    public void setSignature(int signature) {
        this.signature = signature;
    }

    public UserTermsData getUserTermsData() {
        return userTermsData;
    }

    public void setUserTermsData(UserTermsData userTermsData) {
        this.userTermsData = userTermsData;
    }

    public int getReq_proof_present_address() {
        return req_proof_present_address;
    }

    public void setReq_proof_present_address(int req_proof_present_address) {
        this.req_proof_present_address = req_proof_present_address;
    }

    public int getProofOfPresentAddress() {
        return proofOfPresentAddress;
    }

    public void setProofOfPresentAddress(int proofOfPresentAddress) {
        this.proofOfPresentAddress = proofOfPresentAddress;
    }
}
