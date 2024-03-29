package com.capitalnowapp.mobile.beans;

import java.io.Serializable;

public class ContactsDO implements Serializable {
    private String name;
    private String phoneNumber;
    private String photo;
    private boolean isSelected;
    private String relation = "";

    public ContactsDO(String name, String phoneNumber, String pic) {
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.photo = pic;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getRelation() {
        return relation;
    }

    public void setRelation(String relation) {
        this.relation = relation;
    }
}
