package com.capitalnowapp.mobile.beans;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class PaymentClearData implements Serializable {

    @SerializedName("amount")
    @Expose
    private String amount;
    //Razor Pay Receipt Id
    @SerializedName("payment_id")
    @Expose
    private String payment_id;
    @SerializedName("created_at")
    @Expose
    private String transaction_initiated_at;
    @SerializedName("updated_at")
    @Expose
    private String transaction_ended_at;
    //CN Receipt Id
    @SerializedName("receipt_id")
    @Expose
    private String receipt_id;
    @SerializedName("transaction_status")
    @Expose
    private int transaction_status;

    public String getPayment_id() {
        return payment_id;
    }

    public void setPayment_id(String payment_id) {
        this.payment_id = payment_id;
    }

    public String getReceipt_id() {
        return receipt_id;
    }

    public void setReceipt_id(String receipt_id) {
        this.receipt_id = receipt_id;
    }

    public int getTransaction_status() {
        return transaction_status;
    }

    public void setTransaction_status(int transaction_status) {
        this.transaction_status = transaction_status;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getPaymentId() {
        return payment_id;
    }

    public void setPaymentId(String payment_id) {
        this.payment_id = payment_id;
    }

    public String getTransaction_initiated_at() {
        return transaction_initiated_at;
    }

    public void setTransaction_initiated_at(String transaction_initiated_at) {
        this.transaction_initiated_at = transaction_initiated_at;
    }

    public String getTransaction_ended_at() {
        return transaction_ended_at;
    }

    public void setTransaction_ended_at(String transaction_ended_at) {
        this.transaction_ended_at = transaction_ended_at;
    }

    @Override
    public String toString() {
        return "OrderData{" +
                "amount='" + amount + '\'' +
                ", payment_id='" + payment_id + '\'' +
                ", receipt_id='" + receipt_id + '\'' +
                ", transaction_status='" + transaction_status + '\'' +
                ", transaction_initiated_at='" + transaction_initiated_at + '\'' +
                ", transaction_ended_at='" + transaction_ended_at + '\'' +
                '}';
    }

    public String getTransactionMessage(){
        return transaction_status ==0?"Your Transaction has failed! If the amount has been debited from your account, please contact customer care" +
                "":"Your Transaction has been successfully processed";
    }

    public String getTransactionStatusMessage(){
        return transaction_status ==0?"Failed":"Successful";
    }

}
