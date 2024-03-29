package com.capitalnowapp.mobile.beans;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class UserServiceCharges implements Serializable {
    @SerializedName("from_day")
    @Expose
    private String from_day;
    @SerializedName("to_day")
    @Expose
    private String to_day;
    @SerializedName("servicecharges")
    @Expose
    private String service_charges;

    public String getFrom_day() {
        return from_day;
    }

    public String getTo_day() {
        return to_day;
    }

    public String getService_charges() {
        return service_charges;
    }

    @Override
    public String toString() {
        return "UserServiceCharges{" +
                "from_day='" + from_day + '\'' +
                ", to_day='" + to_day + '\'' +
                ", servicecharges='" + service_charges + '\'' +
                '}';
    }
}
