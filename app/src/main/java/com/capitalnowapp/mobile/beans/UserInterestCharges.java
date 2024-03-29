package com.capitalnowapp.mobile.beans;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class UserInterestCharges implements Serializable {
    @SerializedName("max_loanamount")
    @Expose
    private int max_loan_amount;
    @SerializedName("processing_charges")
    @Expose
    private String processing_charges;

    public int getMax_loan_amount() {
        return max_loan_amount;
    }

    public String getProcessing_charges() {
        return processing_charges;
    }

    @Override
    public String toString() {
        return "UserInterestCharges{" +
                "max_loan_amount=" + max_loan_amount +
                ", processing_charges='" + processing_charges + '\'' +
                '}';
    }
}
