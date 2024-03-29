package com.capitalnowapp.mobile.beans;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class BankDetails implements Serializable {
    @SerializedName("len_bank_name")
    @Expose
    private String bankName;
    @SerializedName("len_branch_name")
    @Expose
    private String branchName;
    @SerializedName("len_ifsc_code")
    @Expose
    private String ifscCode;
    @SerializedName("len_account_name")
    @Expose
    private String accountName;
    @SerializedName("len_account_number")
    @Expose
    private String accountNumber;

    public String getBankName() {
        return bankName;
    }

    public String getBranchName() {
        return branchName;
    }

    public String getIfscCode() {
        return ifscCode;
    }

    public String getAccountName() {
        return accountName;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    @Override
    public String toString() {
        return "BankDetails{" +
                "bankName='" + bankName + '\'' +
                ", branchName='" + branchName + '\'' +
                ", ifscCode='" + ifscCode + '\'' +
                ", accountName='" + accountName + '\'' +
                ", accountNumber='" + accountNumber + '\'' +
                '}';
    }
}
