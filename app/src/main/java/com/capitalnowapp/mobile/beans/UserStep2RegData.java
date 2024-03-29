package com.capitalnowapp.mobile.beans;

import java.io.Serializable;

public class UserStep2RegData implements Serializable {
    private int graduationYear;
    private int experience;
    private int maritalStatus;
    private int residenceType;
    private int college;
    private int creditCardType;
    private String freqApps;

    public int getGraduationYear() {
        return graduationYear;
    }

    public void setGraduationYear(int graduationYear) {
        this.graduationYear = graduationYear;
    }

    public int getExperience() {
        return experience;
    }

    public void setExperience(int experience) {
        this.experience = experience;
    }

    public int getMaritalStatus() {
        return maritalStatus;
    }

    public void setMaritalStatus(int maritalStatus) {
        this.maritalStatus = maritalStatus;
    }

    public int getResidenceType() {
        return residenceType;
    }

    public void setResidenceType(int residenceType) {
        this.residenceType = residenceType;
    }

    public int getCollege() {
        return college;
    }

    public void setCollege(int college) {
        this.college = college;
    }

    public int getCreditCardType() {
        return creditCardType;
    }

    public void setCreditCardType(int creditCardType) {
        this.creditCardType = creditCardType;
    }

    public String getFreqApps() {
        return freqApps;
    }

    public void setFreqApps(String freqApps) {
        this.freqApps = freqApps;
    }

    @Override
    public String toString() {
        return "UserStep2RegData{" +
                "graduationYear=" + graduationYear +
                ", experience=" + experience +
                ", maritalStatus=" + maritalStatus +
                ", residenceType=" + residenceType +
                ", college=" + college +
                ", creditCardType=" + creditCardType +
                ", freqApps='" + freqApps + '\'' +
                '}';
    }
}
