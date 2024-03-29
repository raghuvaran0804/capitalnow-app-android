package com.capitalnowapp.mobile.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class SubmitInitialDocsReq implements Serializable {

    @SerializedName("address_proof_pwd")
    @Expose
    public String addressProofPwd;
    @SerializedName("user_id")
    @Expose
    public String userId;
    @SerializedName("id_proof_url")
    @Expose
    public String idProofUrl;
    @SerializedName("address_proof_url")
    @Expose
    public String addressProofUrl;
    @SerializedName("sal_slip_url")
    @Expose
    public String salSlipUrl;
    @SerializedName("sal_slip_password")
    @Expose
    public String salSlipPassword;
    @SerializedName("bank_statements_url")
    @Expose
    public String bankStatementsUrl;
    @SerializedName("bank_statements_password")
    @Expose
    public String bankStatementsPassword;
    @SerializedName("api_key")
    @Expose
    private String apiKey;
    @SerializedName("proof_present_address")
    @Expose
    private String proof_present_address;

    @SerializedName("device_unique_id")
    @Expose
    private String deviceUniqueId = "";

    @SerializedName("present_address_pwd")
    @Expose
    private String present_address_pwd = "";

    @SerializedName("present_add_as_poa")
    @Expose
    private boolean present_add_as_poa = false;

    @SerializedName("customersignature")
    @Expose
    private String customersignature = "";

    @SerializedName("token")
    @Expose
    private String usertoken;


    private String signature_consent = "";

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getIdProofUrl() {
        return idProofUrl;
    }

    public void setIdProofUrl(String idProofUrl) {
        this.idProofUrl = idProofUrl;
    }

    public String getAddressProofUrl() {
        return addressProofUrl;
    }

    public void setAddressProofUrl(String addressProofUrl) {
        this.addressProofUrl = addressProofUrl;
    }

    public String getSalSlipUrl() {
        return salSlipUrl;
    }

    public void setSalSlipUrl(String salSlipUrl) {
        this.salSlipUrl = salSlipUrl;
    }

    public String getSalSlipPassword() {
        return salSlipPassword;
    }

    public void setSalSlipPassword(String salSlipPassword) {
        this.salSlipPassword = salSlipPassword;
    }

    public String getBankStatementsUrl() {
        return bankStatementsUrl;
    }

    public void setBankStatementsUrl(String bankStatementsUrl) {
        this.bankStatementsUrl = bankStatementsUrl;
    }

    public String getBankStatementsPassword() {
        return bankStatementsPassword;
    }

    public void setBankStatementsPassword(String bankStatementsPassword) {
        this.bankStatementsPassword = bankStatementsPassword;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getAddressProofPwd() {
        return addressProofPwd;
    }

    public void setAddressProofPwd(String addressProofPwd) {
        this.addressProofPwd = addressProofPwd;
    }

    public String getDeviceUniqueId() {
        return deviceUniqueId;
    }

    public void setDeviceUniqueId(String deviceUniqueId) {
        this.deviceUniqueId = deviceUniqueId;
    }

    public String getCustomersignature() {
        return customersignature;
    }

    public void setCustomersignature(String customersignature) {
        this.customersignature = customersignature;
    }

    public String getSignature_consent() {
        return signature_consent;
    }

    public void setSignature_consent(String signature_consent) {
        this.signature_consent = signature_consent;
    }

    public String getProof_present_address() {
        return proof_present_address;
    }

    public void setProof_present_address(String proof_present_address) {
        this.proof_present_address = proof_present_address;
    }

    public String getPresent_address_pwd() {
        return present_address_pwd;
    }

    public void setPresent_address_pwd(String present_address_pwd) {
        this.present_address_pwd = present_address_pwd;
    }

    public boolean isPresent_add_as_poa() {
        return present_add_as_poa;
    }

    public void setPresent_add_as_poa(boolean present_add_as_poa) {
        this.present_add_as_poa = present_add_as_poa;
    }

    public String getUsertoken() {
        return usertoken;
    }

    public void setUsertoken(String usertoken) {
        this.usertoken = usertoken;
    }
}
