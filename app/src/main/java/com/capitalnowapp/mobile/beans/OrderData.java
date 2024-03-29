package com.capitalnowapp.mobile.beans;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class OrderData implements Serializable {

    @SerializedName("cid")
    @Expose
    private String cId;
    @SerializedName("order_id")
    @Expose
    private String orderId;
    @SerializedName("redirect_time")
    @Expose
    private int redirect_time;
    @SerializedName("razor_pay_api_key")
    @Expose
    private String razorPayApiKey;
    @SerializedName("user_name")
    @Expose
    private String userName;
    @SerializedName("txnid")
    @Expose
    private String txnid;
    @SerializedName("surl")
    @Expose
    private String surl;
    @SerializedName("furl")
    @Expose
    private String furl;
    @SerializedName("mkey")
    @Expose
    private String mkey;
    @SerializedName("mEmail")
    @Expose
    private String mEmail;
    @SerializedName("mSign")
    @Expose
    private String mSign;
    @SerializedName("productinfo")
    @Expose
    private String productinfo;
    @SerializedName("isPayULive")
    @Expose
    private Boolean isPayULive;
    @SerializedName("hash")
    @Expose
    private String hash;
    @SerializedName("rcptid")
    @Expose
    private String receiptId;
    @SerializedName("razor_pay_order_id")
    @Expose
    private String razorPayOrderId;
    @SerializedName("amount")
    @Expose
    private String amount;
    @SerializedName("user_mobile")
    @Expose
    private String userMobile;
    @SerializedName("user_email")
    @Expose
    private String userEmail;

    @SerializedName("access_key")
    @Expose
    private String accessKey;
    @SerializedName("gateway_type")
    @Expose
    private Integer gatewayType;
    @SerializedName("checkout_name")
    @Expose
    private String checkoutName;
    @SerializedName("checkout_description")
    @Expose
    private String checkoutDescription;
    @SerializedName("checkout_image")
    @Expose
    private String checkoutImage;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("is_gpay")
    @Expose
    private Boolean isGpay = false;
    @SerializedName("is_phonepe")
    @Expose
    private Boolean isPhonepe = false;
    @SerializedName("is_paytm")
    @Expose
    private Boolean isPaytm = false;

    @SerializedName("cca_gateway_details")
    @Expose
    private CcaGatewayDetails ccaGatewayDetails;

    public String getAccessKey() {
        return accessKey;
    }

    public void setAccessKey(String accessKey) {
        this.accessKey = accessKey;
    }

    public Boolean getGpay() {
        return isGpay;
    }

    public void setGpay(Boolean gpay) {
        isGpay = gpay;
    }

    public Boolean getPhonepe() {
        return isPhonepe;
    }

    public void setPhonepe(Boolean phonepe) {
        isPhonepe = phonepe;
    }

    public Boolean getPaytm() {
        return isPaytm;
    }

    public void setPaytm(Boolean paytm) {
        isPaytm = paytm;
    }

    public String getReceiptId() {
        return receiptId;
    }

    public void setReceiptId(String receiptId) {
        this.receiptId = receiptId;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserMobile() {
        return userMobile;
    }

    public void setUserMobile(String userMobile) {
        this.userMobile = userMobile;
    }

    public String getRazorPayOrderId() {
        return razorPayOrderId;
    }

    public void setRazorPayOrderId(String razorPayOrderId) {
        this.razorPayOrderId = razorPayOrderId;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getCheckoutDescription() {
        return checkoutDescription;
    }

    public void setCheckoutDescription(String checkoutDescription) {
        this.checkoutDescription = checkoutDescription;
    }

    public String getCheckoutName() {
        return checkoutName;
    }

    public void setCheckoutName(String checkoutName) {
        this.checkoutName = checkoutName;
    }

    public String getCheckoutImage() {
        return checkoutImage;
    }

    public void setCheckoutImage(String checkoutImage) {
        this.checkoutImage = checkoutImage;
    }


    public String getRazorPayApiKey() {
        return razorPayApiKey;
    }

    public void setRazorPayApiKey(String razorPayApiKey) {
        this.razorPayApiKey = razorPayApiKey;
    }

    public String getTxnid() {
        return txnid;
    }

    public void setTxnid(String txnid) {
        this.txnid = txnid;
    }

    public String getSurl() {
        return surl;
    }

    public void setSurl(String surl) {
        this.surl = surl;
    }

    public String getFurl() {
        return furl;
    }

    public void setFurl(String furl) {
        this.furl = furl;
    }

    public String getMkey() {
        return mkey;
    }

    public void setMkey(String mkey) {
        this.mkey = mkey;
    }

    public String getProductinfo() {
        return productinfo;
    }

    public void setProductinfo(String productinfo) {
        this.productinfo = productinfo;
    }

    public Boolean getIsPayULive() {
        return isPayULive;
    }

    public void setIsPayULive(Boolean isPayULive) {
        this.isPayULive = isPayULive;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public Integer getGatewayType() {
        return gatewayType;
    }

    public void setGatewayType(Integer gatewayType) {
        this.gatewayType = gatewayType;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getmEmail() {
        return mEmail;
    }

    public void setmEmail(String mEmail) {
        this.mEmail = mEmail;
    }

    public String getmSign() {
        return mSign;
    }

    public void setmSign(String mSign) {
        this.mSign = mSign;
    }

    public Boolean getPayULive() {
        return isPayULive;
    }

    public void setPayULive(Boolean payULive) {
        isPayULive = payULive;
    }

    public String getcId() {
        return cId;
    }

    public void setcId(String cId) {
        this.cId = cId;
    }

    public int getRedirect_time() {
        return redirect_time;
    }

    public void setRedirect_time(int redirect_time) {
        this.redirect_time = redirect_time;
    }

    public CcaGatewayDetails getCcaGatewayDetails() {
        return ccaGatewayDetails;
    }

    public void setCcaGatewayDetails(CcaGatewayDetails ccaGatewayDetails) {
        this.ccaGatewayDetails = ccaGatewayDetails;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }
}
